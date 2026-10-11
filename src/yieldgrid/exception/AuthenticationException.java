package yieldgrid.exception;

/*
 * COURSE CONCEPT:
 * Custom Exception, Security Error Handling
 *
 * PURPOSE:
 * AuthenticationException is thrown during login if credentials do not match
 * or if a deactivated/unrecognized user account attempts access.
 */

// EXCEPTION HANDLING:
// Custom exception used to isolate authentication failures cleanly from system crashes.
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
