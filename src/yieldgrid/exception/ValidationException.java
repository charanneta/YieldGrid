package yieldgrid.exception;

/*
 * COURSE CONCEPT:
 * Custom Exception, Exception Hierarchy, Robust Error Handling
 *
 * PURPOSE:
 * ValidationException is thrown when user input violates business rules
 * (e.g., blank fields, non-positive quantity, invalid price, duplicate username)
 * before processing database operations.
 */

// EXCEPTION HANDLING:
// Inheriting from Exception creates a checked exception that calling methods
// must explicitly handle or declare with the 'throws' keyword.
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
