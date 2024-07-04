package com.alexaf.salarycalc.account.repository;

import com.alexaf.salarycalc.user.repository.TgUserEntity;
import com.alexaf.salarycalc.utils.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_entity")
public class AccountEntity extends BaseEntity {

    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance; // сумма денег на счёте

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_on_user"))
    private TgUserEntity owner; // владелец счёта

}