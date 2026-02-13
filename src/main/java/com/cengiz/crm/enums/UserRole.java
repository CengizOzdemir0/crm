package com.cengiz.crm.enums;

/**
 * User Roles
 */
public enum UserRole {
    ADMIN("Administrator"),
    MANAGER("Manager"),
    SALES_REP("Sales Representative"),
    SUPPORT("Support Staff"),
    USER("Regular User");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
