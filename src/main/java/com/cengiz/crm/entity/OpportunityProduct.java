package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * OpportunityProduct Entity
 * Represents products associated with an opportunity
 */
@Entity
@Table(name = "opportunity_products", indexes = {
        @Index(name = "idx_opp_product_opportunity", columnList = "opportunity_id"),
        @Index(name = "idx_opp_product_product", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityProduct extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Quantity is required")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Calculate line total before discount
     */
    @Transient
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Calculate discount amount
     */
    @Transient
    public BigDecimal getDiscountAmount() {
        return getSubtotal().multiply(discountPercentage).divide(BigDecimal.valueOf(100));
    }

    /**
     * Calculate total after discount
     */
    @Transient
    public BigDecimal getTotalAfterDiscount() {
        return getSubtotal().subtract(getDiscountAmount());
    }

    /**
     * Calculate tax amount
     */
    @Transient
    public BigDecimal getTaxAmount() {
        return getTotalAfterDiscount().multiply(taxRate).divide(BigDecimal.valueOf(100));
    }

    /**
     * Calculate final total including tax
     */
    @Transient
    public BigDecimal getTotal() {
        return getTotalAfterDiscount().add(getTaxAmount());
    }
}
