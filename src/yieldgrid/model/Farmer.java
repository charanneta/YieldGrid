package yieldgrid.model;

/*
 * COURSE CONCEPT:
 * Inheritance, Polymorphism, Method Overriding, Super Keyword
 *
 * PURPOSE:
 * Farmer models an agricultural producer in YieldGrid who lists crops,
 * manages pricing/stock, receives incoming customer orders, and tracks revenue.
 */

// OOP CONCEPT: Inheritance
// Farmer inherits all common authentication and profile fields from User.
// This prevents code duplication and allows polymorphic handling of Users.
public class Farmer extends User {

    // OOP CONCEPT: Constructor & super Keyword
    // Calls the superclass constructor to initialize inherited fields
    public Farmer(int userId, String name, String username, String password, String contactInfo) {
        super(userId, name, username, password, "FARMER", contactInfo);
    }

    // OOP CONCEPT: Constructor Overloading
    // Used during new farmer registration prior to database ID assignment
    public Farmer(String name, String username, String password, String contactInfo) {
        super(name, username, password, "FARMER", contactInfo);
    }

    // OOP CONCEPT: Polymorphism & Method Overriding
    // Overrides the abstract method defined in User to return farmer-specific description
    @Override
    public String getRoleDescription() {
        return "Farmer (Producer): Manages crop inventory, sets pricing, and fulfills customer orders.";
    }

    // OOP CONCEPT: Method Overriding
    @Override
    public String getDashboardTitle() {
        return "YieldGrid — Farmer Inventory & Order Center";
    }
}
