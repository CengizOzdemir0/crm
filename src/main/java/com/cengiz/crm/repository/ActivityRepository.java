package com.cengiz.crm.repository;

import com.cengiz.crm.entity.Activity;
import com.cengiz.crm.enums.ActivityStatus;
import com.cengiz.crm.enums.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByIsDeletedFalse();

    List<Activity> findByStatusAndIsDeletedFalse(ActivityStatus status);

    List<Activity> findByActivityTypeAndIsDeletedFalse(ActivityType activityType);

    List<Activity> findByAssignedToIdAndIsDeletedFalse(Long assignedToId);

    List<Activity> findByCustomerIdAndIsDeletedFalse(Long customerId);

    List<Activity> findByOpportunityIdAndIsDeletedFalse(Long opportunityId);

    @Query("SELECT a FROM Activity a WHERE a.isDeleted = false AND " +
            "a.dueDate BETWEEN :startDate AND :endDate")
    List<Activity> findByDueDateBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Activity a WHERE a.isDeleted = false AND " +
            "a.dueDate < :now AND a.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Activity> findOverdueActivities(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.isDeleted = false AND " +
            "a.assignedTo.id = :userId AND a.status = :status")
    long countByUserAndStatus(@Param("userId") Long userId, @Param("status") ActivityStatus status);
}
