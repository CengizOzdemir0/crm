package com.cengiz.crm.repository;

import com.cengiz.crm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsDeletedFalse();

    List<Product> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    Optional<Product> findByProductCodeAndIsDeletedFalse(String productCode);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.productCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> searchProducts(@Param("search") String search);
}
