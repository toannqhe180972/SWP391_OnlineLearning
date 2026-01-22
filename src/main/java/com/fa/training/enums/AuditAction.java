package com.fa.training.enums;

/**
 * Audit log action types
 */
public enum AuditAction {
    REGISTER("User Registration"),
    LOGIN("User Login"),
    LOGOUT("User Logout"),
    CREATE("Create"),
    UPDATE("Update"),
    DELETE("Delete"),
    CREATE_USER("Create User"),
    UPDATE_USER("Update User"),
    DELETE_USER("Delete User"),
    CREATE_SETTING("Create Setting"),
    UPDATE_SETTING("Update Setting"),
    TOGGLE_SETTING("Toggle Setting Status"),
    DELETE_SETTING("Delete Setting");

    private final String description;

    AuditAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
