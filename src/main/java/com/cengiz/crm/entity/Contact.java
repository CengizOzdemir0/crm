package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Contact Entity
 * Represents individual contacts associated with customers
 */
@Entity
@Table(name = "contacts", indexes = {
        @Index(name = "idx_contact_email", columnList = "email"),
        @Index(name = "idx_contact_customer", columnList = "customer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "department", length = 100)
    private String department;

    @Email(message = "Invalid email format")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "is_decision_maker")
    @Builder.Default
    private Boolean isDecisionMaker = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "linkedin_url", length = 200)
    private String linkedinUrl;

    @Column(name = "twitter_handle", length = 100)
    private String twitterHandle;

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
