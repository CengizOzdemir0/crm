package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import com.cengiz.crm.enums.OpportunityStage;
import com.cengiz.crm.enums.OpportunityStatus;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Opportunity Entity
 * Represents sales opportunities in the pipeline
 */
@Entity
@Table(name = "opportunities", indexes = {
        @Index(name = "idx_opportunity_customer", columnList = "customer_id"),
        @Index(name = "idx_opportunity_stage", columnList = "stage"),
        @Index(name = "idx_opportunity_status", columnList = "status"),
        @Index(name = "idx_opportunity_owner", columnList = "owner_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity extends BaseEntity {

    @NotBlank(message = "Opportunity name is required")
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Value is required")
    @Column(name = "value", nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false, length = 30)
    @Builder.Default
    private OpportunityStage stage = OpportunityStage.PROSPECTING;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OpportunityStatus status = OpportunityStatus.OPEN;

    @Column(name = "probability")
    private Integer probability; // 0-100%

    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;

    @Column(name = "actual_close_date")
    private LocalDate actualCloseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "next_step", length = 500)
    private String nextStep;

    @Column(name = "tags", length = 500)
    private String tags;

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<OpportunityProduct> products = new HashSet<>();

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Activity> activities = new HashSet<>();

    /**
     * Calculate weighted value (value * probability)
     */
    @Transient
    public BigDecimal getWeightedValue() {
        if (value == null || probability == null) {
            return BigDecimal.ZERO;
        }
        return value.multiply(BigDecimal.valueOf(probability)).divide(BigDecimal.valueOf(100));
    }

    /**
     * Check if opportunity is overdue
     */
    @Transient
    public boolean isOverdue() {
        return expectedCloseDate != null &&
                expectedCloseDate.isBefore(LocalDate.now()) &&
                status == OpportunityStatus.OPEN;
    }

    /**
     * Move to next stage
     */
    public void moveToNextStage() {
        if (stage != null) {
            OpportunityStage[] stages = OpportunityStage.values();
            int currentIndex = stage.ordinal();
            if (currentIndex < stages.length - 1) {
                this.stage = stages[currentIndex + 1];
                updateProbabilityByStage();
            }
        }
    }

    /**
     * Update probability based on stage
     */
    private void updateProbabilityByStage() {
        switch (stage) {
            case PROSPECTING:
                this.probability = 10;
                break;
            case QUALIFICATION:
                this.probability = 20;
                break;
            case NEEDS_ANALYSIS:
                this.probability = 40;
                break;
            case PROPOSAL:
                this.probability = 60;
                break;
            case NEGOTIATION:
                this.probability = 80;
                break;
            case CLOSED_WON:
                this.probability = 100;
                this.status = OpportunityStatus.WON;
                this.actualCloseDate = LocalDate.now();
                break;
            case CLOSED_LOST:
                this.probability = 0;
                this.status = OpportunityStatus.LOST;
                this.actualCloseDate = LocalDate.now();
                break;
        }
    }
}
