package com.cengiz.crm.repository;

import com.cengiz.crm.entity.OpportunityProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityProductRepository extends JpaRepository<OpportunityProduct, Long> {

    List<OpportunityProduct> findByOpportunityIdAndIsDeletedFalse(Long opportunityId);

    List<OpportunityProduct> findByProductIdAndIsDeletedFalse(Long productId);
}
