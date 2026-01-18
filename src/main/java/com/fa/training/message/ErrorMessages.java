package com.fa.training.message;

/**
 * Error messages for user input validation and business logic
 */
public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Authentication errors
    public static final String USERNAME_EXISTS = "Username already exists!";
    public static final String EMAIL_EXISTS = "Email already exists!";
    public static final String INVALID_CREDENTIALS = "Invalid credentials.";
    public static final String PASSWORD_INCORRECT = "Incorrect!";

    // File upload errors
    public static final String FILE_EMPTY = "File is empty";
    public static final String FILE_TOO_LARGE = "File size exceeds 1MB limit";
    public static final String FILE_INVALID_TYPE = "Invalid file type. Only jpg, jpeg, png, gif allowed";

    // Generic errors
    public static final String SEND_EMAIL_FAILED = "Failed to send email: ";
}
