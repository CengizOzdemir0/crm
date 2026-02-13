package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import com.cengiz.crm.enums.UserRole;
import com.cengiz.crm.enums.UserStatus;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity
 * Represents system users with roles and permissions
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "department", length = 100)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "must_change_password")
    @Builder.Default
    private Boolean mustChangePassword = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Lead> assignedLeads = new HashSet<>();

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Activity> assignedActivities = new HashSet<>();

    /**
     * Get full name
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if account is locked
     */
    @Transient
    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }

    /**
     * Check if account is active
     */
    @Transient
    public boolean isActive() {
        return status == UserStatus.ACTIVE && !isAccountLocked();
    }

    /**
     * Record successful login
     */
    public void recordSuccessfulLogin(String ipAddress) {
        this.lastLoginAt = LocalDateTime.now();
        this.lastLoginIp = ipAddress;
        this.failedLoginAttempts = 0;
        this.accountLockedUntil = null;
    }

    /**
     * Record failed login attempt
     */
    public void recordFailedLogin() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountLockedUntil = LocalDateTime.now().plusMinutes(30);
        }
    }
}
