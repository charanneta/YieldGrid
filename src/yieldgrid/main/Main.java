package yieldgrid.main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import yieldgrid.database.DBConnection;
import yieldgrid.ui.LoginFrame;

/*
 * PROJECT: YieldGrid
 * SUBTITLE: Application for Farm-to-Retail Inventory
 *
 * APPLICATION CONCEPT:
 * Application Entrypoint, Java Swing Concurrency (Event Dispatch Thread), Look-and-Feel, Database Initialization
 *
 * PURPOSE:
 * Main is the bootstrap class for YieldGrid. It initializes the database schema,
 * sets the native system Look-and-Feel for a clean presentation, and displays
 * the LoginFrame on the Swing Event Dispatch Thread (EDT).
 */

public class Main {

    // METHOD CONCEPT: Application Entry Point
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Starting YieldGrid — Farm-to-Retail Inventory System");
        System.out.println("==================================================");

        // JDBC & DATABASE INITIALIZATION:
        // Automatically creates required tables in the 'yieldgrid' database if not already present
        try {
            DBConnection.initializeDatabaseTables();
            System.out.println("[DB] Database verification completed.");
        } catch (Exception e) {
            System.err.println("[DB Warning] Database check note: " + e.getMessage());
            System.err.println("[DB Warning] Please verify MySQL is running on localhost:3306 and config.properties has your password.");
        }

        // JAVA SWING: Thread Safety via Event Dispatch Thread (EDT)
        // Swing GUI components must be constructed and updated on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Modernize look and feel to system default
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("Could not set System Look and Feel: " + e.getMessage());
                }

                // Launch Authentication Window
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
