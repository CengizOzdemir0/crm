package com.cengiz.crm.repository;

import com.cengiz.crm.entity.Opportunity;
import com.cengiz.crm.enums.OpportunityStage;
import com.cengiz.crm.enums.OpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    List<Opportunity> findByIsDeletedFalse();

    List<Opportunity> findByStatusAndIsDeletedFalse(OpportunityStatus status);

    List<Opportunity> findByStageAndIsDeletedFalse(OpportunityStage stage);

    List<Opportunity> findByOwnerIdAndIsDeletedFalse(Long ownerId);

    List<Opportunity> findByCustomerIdAndIsDeletedFalse(Long customerId);

    @Query("SELECT o FROM Opportunity o WHERE o.isDeleted = false AND " +
            "LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Opportunity> searchOpportunities(@Param("search") String search);

    @Query("SELECT SUM(o.value) FROM Opportunity o WHERE o.isDeleted = false AND o.status = :status")
    BigDecimal sumValueByStatus(@Param("status") OpportunityStatus status);

    @Query("SELECT COUNT(o) FROM Opportunity o WHERE o.isDeleted = false AND o.status = :status")
    long countByStatus(@Param("status") OpportunityStatus status);

    @Query("SELECT o FROM Opportunity o LEFT JOIN FETCH o.products WHERE o.id = :id AND o.isDeleted = false")
    Opportunity findByIdWithProducts(@Param("id") Long id);
}
