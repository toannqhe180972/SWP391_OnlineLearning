package com.fa.training.message;

/**
 * Success messages for user feedback
 */
public final class SuccessMessages {

    private SuccessMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Authentication
    public static final String REGISTRATION_SUCCESS = "Registration successful! Please login.";
    public static final String RESET_LINK_SENT = "Reset link sent (Simulation).";

    // Email
    public static final String EMAIL_SENT_SUCCESS = "Email sent successfully to ";

    // Profile updates
    public static final String PROFILE_UPDATED = "Profile updated successfully!";
    public static final String AVATAR_UPLOADED = "Avatar uploaded successfully!";
    public static final String PASSWORD_CHANGED = "Password changed successfully!";
}
