package yieldgrid.service;

import java.util.ArrayList;
import yieldgrid.model.CartItem;
import yieldgrid.model.Customer;
import yieldgrid.model.Farmer;
import yieldgrid.model.User;

/*
 * COURSE CONCEPT:
 * Encapsulation, State Management, Polymorphism, Collections (ArrayList)
 *
 * PURPOSE:
 * SessionManager maintains the in-memory application session for the currently
 * authenticated user. It holds the active User reference (polymorphically Farmer or Customer)
 * and manages the customer's active shopping cart items during their GUI session.
 */

public class SessionManager {

    // Encapsulated static state
    private static User currentUser = null;

    // JAVA COLLECTIONS: ArrayList
    // In-memory cart holding active CartItem objects during customer session
    private static final ArrayList<CartItem> activeCart = new ArrayList<>();

    // Private constructor prevents direct instantiation (Singleton-style utility)
    private SessionManager() {
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        // Reset cart whenever a new user logs in
        activeCart.clear();
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // CONDITIONAL LOGIC & POLYMORPHISM:
    public static boolean isFarmer() {
        return currentUser instanceof Farmer;
    }

    public static boolean isCustomer() {
        return currentUser instanceof Customer;
    }

    public static Farmer getCurrentFarmer() {
        if (isFarmer()) {
            return (Farmer) currentUser;
        }
        return null;
    }

    public static Customer getCurrentCustomer() {
        if (isCustomer()) {
            return (Customer) currentUser;
        }
        return null;
    }

    public static void logout() {
        currentUser = null;
        activeCart.clear();
    }

    // CART MANAGEMENT (In-memory ArrayList operations):
    public static ArrayList<CartItem> getActiveCart() {
        return activeCart;
    }

    public static void clearCart() {
        activeCart.clear();
    }
}
