package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import com.cengiz.crm.enums.LeadSource;
import com.cengiz.crm.enums.LeadStatus;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lead Entity
 * Represents potential customers (leads) in the sales pipeline
 */
@Entity
@Table(name = "leads", indexes = {
        @Index(name = "idx_lead_email", columnList = "email"),
        @Index(name = "idx_lead_status", columnList = "status"),
        @Index(name = "idx_lead_source", columnList = "source"),
        @Index(name = "idx_lead_assigned", columnList = "assigned_to_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Email(message = "Invalid email format")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "website", length = 200)
    private String website;

    // Address Information
    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    // Lead Information
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 50)
    private LeadSource source;

    @Column(name = "rating")
    private Integer rating; // 1-5 stars (lead quality)

    @Column(name = "estimated_value", precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "tags", length = 500)
    private String tags;

    // Conversion Information
    @Column(name = "is_converted")
    @Builder.Default
    private Boolean isConverted = false;

    @Column(name = "converted_at")
    private LocalDate convertedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_customer_id")
    private Customer convertedCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_opportunity_id")
    private Opportunity convertedOpportunity;

    // Social Media
    @Column(name = "linkedin_url", length = 200)
    private String linkedinUrl;

    @Column(name = "twitter_handle", length = 100)
    private String twitterHandle;

    /**
     * Get full name
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Convert lead to customer
     */
    public void convertToCustomer(Customer customer, Opportunity opportunity) {
        this.isConverted = true;
        this.convertedAt = LocalDate.now();
        this.convertedCustomer = customer;
        this.convertedOpportunity = opportunity;
        this.status = LeadStatus.CONVERTED;
    }

    /**
     * Check if lead is qualified
     */
    @Transient
    public boolean isQualified() {
        return status == LeadStatus.QUALIFIED;
    }
}
