package yieldgrid.exception;

/*
 * COURSE CONCEPT:
 * Custom Exception, Business Rule Violation
 *
 * PURPOSE:
 * InsufficientStockException is thrown when a customer attempts to purchase
 * more units of a crop than currently available in farmer inventory.
 */

// EXCEPTION HANDLING:
// Checked exception ensuring that order processing checks stock availability before debiting.
public class InsufficientStockException extends Exception {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
