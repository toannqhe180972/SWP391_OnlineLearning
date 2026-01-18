package com.fa.training.constant;

/**
 * Application-wide constants for file upload configuration
 */
public final class FileConstants {

    private FileConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Upload directories
    public static final String UPLOAD_DIR = "src/main/resources/static/uploads/avatars/";
    public static final String UPLOAD_URL_PREFIX = "/uploads/avatars/";

    // File size limits
    public static final long MAX_AVATAR_SIZE = 1024 * 1024; // 1MB

    // Allowed file extensions
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = { "jpg", "jpeg", "png", "gif" };
}
