package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Ürün Entity
 * Satılabilecek ürün/hizmetleri temsil eder
 * 
 * @author Cengiz
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_code", columnList = "product_code"),
        @Index(name = "idx_product_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @NotBlank(message = "Ürün adı gereklidir")
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotBlank(message = "Ürün kodu gereklidir")
    @Size(max = 50)
    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String productCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", length = 100)
    private String category;

    @NotNull(message = "Birim fiyat gereklidir")
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "cost_price", precision = 15, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Kar marjını hesapla
     */
    @Transient
    public BigDecimal getProfitMargin() {
        if (unitPrice == null || costPrice == null || costPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return unitPrice.subtract(costPrice)
                .divide(costPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
