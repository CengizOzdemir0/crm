package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import com.cengiz.crm.enums.CustomerStatus;
import com.cengiz.crm.enums.CustomerType;
import com.cengiz.crm.enums.Industry;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Customer Entity
 * Represents customers in the CRM system
 */
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_email", columnList = "email"),
        @Index(name = "idx_customer_company", columnList = "company_name"),
        @Index(name = "idx_customer_status", columnList = "status"),
        @Index(name = "idx_customer_type", columnList = "customer_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @NotBlank(message = "Company name is required")
    @Size(max = 200)
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 20)
    private CustomerType customerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "industry", length = 50)
    private Industry industry;

    @Email(message = "Invalid email format")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "tax_number", length = 50)
    private String taxNumber;

    @Column(name = "tax_office", length = 100)
    private String taxOffice;

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

    // Business Information
    @Column(name = "employee_count")
    private Integer employeeCount;

    @Column(name = "annual_revenue", precision = 15, scale = 2)
    private BigDecimal annualRevenue;

    @Column(name = "customer_since")
    private LocalDate customerSince;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;

    // Account Manager
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_manager_id")
    private User accountManager;

    // Rating and Notes
    @Column(name = "rating")
    private Integer rating; // 1-5 stars

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "tags", length = 500)
    private String tags;

    // Social Media
    @Column(name = "linkedin_url", length = 200)
    private String linkedinUrl;

    @Column(name = "twitter_handle", length = 100)
    private String twitterHandle;

    @Column(name = "facebook_url", length = 200)
    private String facebookUrl;

    // Relationships
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Contact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Opportunity> opportunities = new HashSet<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Activity> activities = new HashSet<>();

    /**
     * Add contact to customer
     */
    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setCustomer(this);
    }

    /**
     * Remove contact from customer
     */
    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setCustomer(null);
    }

    /**
     * Calculate total opportunity value
     */
    @Transient
    public BigDecimal getTotalOpportunityValue() {
        return opportunities.stream()
                .map(Opportunity::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
