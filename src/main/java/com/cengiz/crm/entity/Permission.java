package com.cengiz.crm.entity;

import com.cengiz.crm.entity.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Permission Entity
 * Represents granular permissions for users
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @NotBlank(message = "Permission name is required")
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotBlank(message = "Permission code is required")
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "category", length = 50)
    private String category;
}
