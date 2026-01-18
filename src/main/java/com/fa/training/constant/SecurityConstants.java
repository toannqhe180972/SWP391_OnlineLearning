package com.fa.training.constant;

/**
 * Role and security-related constants
 */
public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Role names
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    // Provider types
    public static final String PROVIDER_LOCAL = "LOCAL";
    public static final String PROVIDER_GOOGLE = "GOOGLE";

    // Password generation
    public static final int RANDOM_PASSWORD_LENGTH = 12;
}
