package com.cengiz.crm.repository;

import com.cengiz.crm.entity.Lead;
import com.cengiz.crm.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByIsDeletedFalse();

    List<Lead> findByStatusAndIsDeletedFalse(LeadStatus status);

    List<Lead> findByAssignedToIdAndIsDeletedFalse(Long assignedToId);

    List<Lead> findByIsConvertedAndIsDeletedFalse(Boolean isConverted);

    @Query("SELECT l FROM Lead l WHERE l.isDeleted = false AND " +
            "(LOWER(l.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(l.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(l.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(l.companyName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Lead> searchLeads(@Param("search") String search);

    @Query("SELECT COUNT(l) FROM Lead l WHERE l.isDeleted = false AND l.status = :status")
    long countByStatus(@Param("status") LeadStatus status);

    @Query("SELECT COUNT(l) FROM Lead l WHERE l.isDeleted = false AND l.isConverted = true")
    long countConverted();
}
