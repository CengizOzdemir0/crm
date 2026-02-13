package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import com.cengiz.crm.enums.ActivityStatus;
import com.cengiz.crm.enums.ActivityType;
import com.cengiz.crm.enums.Priority;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Activity Entity
 * Represents tasks, meetings, calls, and emails
 */
@Entity
@Table(name = "activities", indexes = {
        @Index(name = "idx_activity_type", columnList = "activity_type"),
        @Index(name = "idx_activity_status", columnList = "status"),
        @Index(name = "idx_activity_assigned", columnList = "assigned_to_id"),
        @Index(name = "idx_activity_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity extends BaseEntity {

    @NotBlank(message = "Subject is required")
    @Size(max = 200)
    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 20)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ActivityStatus status = ActivityStatus.PLANNED;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", nullable = false)
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "outcome", columnDefinition = "TEXT")
    private String outcome;

    @Column(name = "is_reminder_sent")
    @Builder.Default
    private Boolean isReminderSent = false;

    /**
     * Mark activity as completed
     */
    public void complete(String outcome) {
        this.status = ActivityStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
        this.outcome = outcome;
    }

    /**
     * Check if activity is overdue
     */
    @Transient
    public boolean isOverdue() {
        return dueDate != null &&
                dueDate.isBefore(LocalDateTime.now()) &&
                status != ActivityStatus.COMPLETED &&
                status != ActivityStatus.CANCELLED;
    }

    /**
     * Check if activity is due today
     */
    @Transient
    public boolean isDueToday() {
        if (dueDate == null)
            return false;
        LocalDateTime now = LocalDateTime.now();
        return dueDate.toLocalDate().equals(now.toLocalDate());
    }
}
