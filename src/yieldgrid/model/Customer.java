package yieldgrid.model;

/*
 * COURSE CONCEPT:
 * Inheritance, Polymorphism, Method Overriding, Super Keyword
 *
 * PURPOSE:
 * Customer models a retail consumer or local buyer in YieldGrid who browses
 * farm-fresh produce, maintains a shopping cart, places orders, and tracks order status.
 */

// OOP CONCEPT: Inheritance
// Customer inherits common user properties and methods from User.
public class Customer extends User {

    // OOP CONCEPT: Constructor & super Keyword
    // Passes common credentials and role to the User superclass constructor
    public Customer(int userId, String name, String username, String password, String contactInfo) {
        super(userId, name, username, password, "CUSTOMER", contactInfo);
    }

    // OOP CONCEPT: Constructor Overloading
    // Used for registering new customers before database assignment
    public Customer(String name, String username, String password, String contactInfo) {
        super(name, username, password, "CUSTOMER", contactInfo);
    }

    // OOP CONCEPT: Polymorphism & Method Overriding
    // Specific implementation of role description for retail customers
    @Override
    public String getRoleDescription() {
        return "Customer (Consumer): Direct-to-Consumer buyer browsing fresh crops and placing direct orders.";
    }

    // OOP CONCEPT: Method Overriding
    @Override
    public String getDashboardTitle() {
        return "YieldGrid — Direct Farm Marketplace";
    }
}
