package yieldgrid.model;

/*
 * COURSE CONCEPT:
 * Java Enum (Enumeration)
 *
 * PURPOSE:
 * OrderStatus defines the strict set of lifecycle states that a customer order
 * can transition through in YieldGrid (PENDING -> CONFIRMED -> COMPLETED, or CANCELLED).
 * Using an enum provides type safety, prevents arbitrary invalid string inputs,
 * and makes conditional logic robust and clean.
 */

// JAVA ENUM CONCEPT:
// OrderStatus is represented using an enum because an order must only
// hold one value from a predefined set of valid business states.
// This is much safer and clearer than using unrestricted String literals.
public enum OrderStatus {
    PENDING("Order Placed, Waiting for Farmer Confirmation"),
    CONFIRMED("Confirmed by Farmer, Preparing for Delivery/Pickup"),
    COMPLETED("Successfully Delivered and Fulfilled"),
    CANCELLED("Order Cancelled");

    // OOP CONCEPT: Encapsulation
    // Private field stores the human-readable description for display in the GUI
    private final String displayDescription;

    // OOP CONCEPT: Constructor
    // Constructor initializes the associated description for each enum constant
    OrderStatus(String displayDescription) {
        this.displayDescription = displayDescription;
    }

    // OOP CONCEPT: Method
    // Getter method providing controlled access to the encapsulated description
    public String getDisplayDescription() {
        return displayDescription;
    }

    public boolean canTransitionTo(OrderStatus target) {
        if (target == null || this == target) {
            return false;
        }
        return (this == PENDING && (target == CONFIRMED || target == CANCELLED))
                || (this == CONFIRMED && (target == COMPLETED || target == CANCELLED));
    }

    // METHOD / STRING HANDLING:
    // Helper method to safely parse a string from MySQL into the enum constant
    public static OrderStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return PENDING;
        }
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(statusStr.trim())) {
                return status;
            }
        }
        return PENDING;
    }
}
