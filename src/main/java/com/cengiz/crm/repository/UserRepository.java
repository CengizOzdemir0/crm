package com.cengiz.crm.repository;

import com.cengiz.crm.entity.User;
import com.cengiz.crm.enums.UserRole;
import com.cengiz.crm.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    List<User> findByIsDeletedFalse();

    List<User> findByRoleAndIsDeletedFalse(UserRole role);

    List<User> findByStatusAndIsDeletedFalse(UserStatus status);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchUsers(@Param("search") String search);

    boolean existsByEmailAndIsDeletedFalse(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isDeleted = false AND u.status = :status")
    long countByStatus(@Param("status") UserStatus status);
}
