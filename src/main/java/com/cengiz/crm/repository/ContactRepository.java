package com.cengiz.crm.repository;

import com.cengiz.crm.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByIsDeletedFalse();

    List<Contact> findByCustomerIdAndIsDeletedFalse(Long customerId);

    List<Contact> findByIsPrimaryAndIsDeletedFalse(Boolean isPrimary);

    @Query("SELECT c FROM Contact c WHERE c.isDeleted = false AND " +
            "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Contact> searchContacts(@Param("search") String search);
}
