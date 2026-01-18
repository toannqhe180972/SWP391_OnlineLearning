package com.fa.training.constant;

/**
 * View and template name constants
 */
public final class ViewConstants {

    private ViewConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Auth views
    public static final String VIEW_LOGIN = "login";
    public static final String VIEW_REGISTER = "register";
    public static final String VIEW_RESET_PASSWORD = "reset-password";

    // User views
    public static final String VIEW_PROFILE = "profile";
    public static final String VIEW_CHANGE_PASSWORD = "change-password";

    // Admin views
    public static final String VIEW_ADMIN_USER_LIST = "admin/user-list";
    public static final String VIEW_ADMIN_USER_DETAIL = "admin/user-detail";
    public static final String VIEW_ADMIN_SETTINGS_LIST = "admin/settings-list";
    public static final String VIEW_ADMIN_SETTINGS_DETAIL = "admin/settings-detail";
    public static final String VIEW_ADMIN_AUDIT_LIST = "admin/audit-list";

    // Redirect paths
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String REDIRECT_ADMIN_USERS = "redirect:/admin/users";
    public static final String REDIRECT_ADMIN_SETTINGS = "redirect:/admin/settings";
    public static final String REDIRECT_PROFILE = "redirect:/profile";
}
