package com.cengiz.crm.repository;

import com.cengiz.crm.entity.Customer;
import com.cengiz.crm.enums.CustomerStatus;
import com.cengiz.crm.enums.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByIsDeletedFalse();

    List<Customer> findByStatusAndIsDeletedFalse(CustomerStatus status);

    List<Customer> findByIndustryAndIsDeletedFalse(Industry industry);

    List<Customer> findByAccountManagerIdAndIsDeletedFalse(Long accountManagerId);

    @Query("SELECT c FROM Customer c WHERE c.isDeleted = false AND " +
            "(LOWER(c.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.city) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Customer> searchCustomers(@Param("search") String search);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.isDeleted = false AND c.status = :status")
    long countByStatus(@Param("status") CustomerStatus status);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.contacts WHERE c.id = :id AND c.isDeleted = false")
    Customer findByIdWithContacts(@Param("id") Long id);
}
