package com.alexaf.salarycalc.transaction.repository;

import com.alexaf.salarycalc.account.repository.AccountEntity;
import com.alexaf.salarycalc.transaction.event.InvokeTransactionEvent;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import com.alexaf.salarycalc.utils.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "transaction_entity")
public class TransactionEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_on_tguser_id"))
    private TgUserEntity initiator;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_on_account_id"))
    private AccountEntity accrualAccount;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public TransactionEntity(TgUserEntity initiator, AccountEntity accrualAccount, BigDecimal amount) {
        this.initiator = initiator;
        this.accrualAccount = accrualAccount;
        this.amount = amount;
    }

    @PrePersist
    public void prePersist() {
        registerEvent(new InvokeTransactionEvent(this));
    }

    public void apply() {
        if (completedAt != null)
            throw new IllegalStateException("Транзакция уже выполнена");

        accrualAccount.updateBalance(amount);
        setCompletedAt(LocalDateTime.now());
    }

}