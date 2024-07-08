package com.alexaf.salarycalc.goal.repository;

import com.alexaf.salarycalc.contribution.repository.Contribution;
import com.alexaf.salarycalc.goal.GoalType;
import com.alexaf.salarycalc.user.repository.User;
import com.alexaf.salarycalc.utils.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "goal")
public class Goal extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    @Column(name = "type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private GoalType type;

    @Column(name = "balance", precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "target_amount", precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "monthly_amount", precision = 12, scale = 2)
    private BigDecimal monthlyAmount; // может быть и процентом, и целым значением

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL)
    private Set<Contribution> contributions;

    public Goal(User user, GoalType type, String name) {
        this.user = user;
        this.type = type;
        this.name = name;
    }

}
