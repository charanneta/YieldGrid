package yieldgrid.interfaces;

/*
 * COURSE CONCEPT:
 * Interfaces, Abstraction, Polymorphism
 *
 * PURPOSE:
 * DashboardActions defines a standard contract for any user dashboard in YieldGrid.
 * Both FarmerDashboard and CustomerDashboard implement this interface, ensuring
 * uniform navigation lifecycle behaviors such as refreshing displayed data,
 * viewing user profile details, and securely logging out of the session.
 */

// OOP CONCEPT: Interface
// This interface defines operations that a dashboard-related class
// must support without forcing the implementation into one specific class hierarchy.
public interface DashboardActions {

    // Abstraction: Specifies common behavior contract
    void refreshData();

    void showProfile();

    void logout();
}
