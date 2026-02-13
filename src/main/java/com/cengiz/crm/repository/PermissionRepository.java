package com.cengiz.crm.repository;

import com.cengiz.crm.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByIsDeletedFalse();

    Optional<Permission> findByCodeAndIsDeletedFalse(String code);

    List<Permission> findByCategoryAndIsDeletedFalse(String category);
}
