package yieldgrid.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/*
 * COURSE CONCEPT:
 * JDBC (Java Database Connectivity), Static Utility, Properties / File I/O, Exception Handling
 *
 * PURPOSE:
 * DBConnection manages the database connection lifecycle for YieldGrid.
 * It dynamically reads database configuration (URL, username, and password)
 * from 'config.properties' without hardcoding credentials into source code.
 */

public class DBConnection {

    // JDBC CONCEPT:
    // Credentials and connection parameters read dynamically from external config.properties
    private static String url = "jdbc:mysql://localhost:3306/yieldgrid";
    private static String username = "root";
    private static String password = "";
    private static boolean driverLoaded = false;
    private static boolean configLoaded = false;

    // STATIC INITIALIZER / METHOD:
    // Loads MySQL Connector/J driver and configuration properties when class is first referenced
    static {
        loadConfiguration();
        loadDriver();
    }

    // METHOD CONCEPT / FILE HANDLING:
    // Reads config.properties to externalize credentials
    private static void loadConfiguration() {
        Properties props = new Properties();
        File configFile = new File("config.properties");

        if (configFile.exists()) {
            try (InputStream in = new FileInputStream(configFile)) {
                props.load(in);
                url = props.getProperty("db.url", url);
                username = props.getProperty("db.username", username);
                password = props.getProperty("db.password", password);
                configLoaded = true;
            } catch (IOException e) {
                System.err.println("Warning: Could not read config.properties: " + e.getMessage());
            }
        } else {
            System.err.println("Notice: config.properties not found in working directory. Using default connection parameters.");
        }
    }

    // JDBC CONCEPT: Driver Loading
    private static void loadDriver() {
        try {
            // Load MySQL Connector/J JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found in classpath. Ensure mysql-connector-j jar is present.");
        }
    }

    // JDBC CONCEPT: Connection Factory
    // Establishes and returns a fresh Connection object for executing SQL statements
    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            loadDriver();
        }
        // Always refresh config in case the user updated config.properties while running
        loadConfiguration();
        return DriverManager.getConnection(url, username, password);
    }

    // JDBC & SQL CONCEPT: Table Initialization
    // Ensures tables required by YieldGrid exist in the 'yieldgrid' database
    public static void initializeDatabaseTables() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password VARCHAR(255) NOT NULL, "
                + "role VARCHAR(20) NOT NULL, "
                + "contact_info VARCHAR(150), "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";

        String createCropsTable = "CREATE TABLE IF NOT EXISTS crops ("
                + "crop_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "farmer_id INT NOT NULL, "
                + "crop_name VARCHAR(100) NOT NULL, "
                + "description TEXT, "
                + "quantity INT NOT NULL, "
                + "price DECIMAL(10, 2) NOT NULL, "
                + "available BOOLEAN NOT NULL DEFAULT TRUE, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (farmer_id) REFERENCES users(user_id) ON DELETE CASCADE"
                + ");";

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders ("
                + "order_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "customer_id INT NOT NULL, "
                + "farmer_id INT NOT NULL, "
                + "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "total_amount DECIMAL(10, 2) NOT NULL, "
                + "status VARCHAR(20) NOT NULL DEFAULT 'PENDING', "
                + "FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (farmer_id) REFERENCES users(user_id) ON DELETE CASCADE"
                + ");";

        String createOrderItemsTable = "CREATE TABLE IF NOT EXISTS order_items ("
                + "order_item_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "order_id INT NOT NULL, "
                + "crop_id INT NOT NULL, "
                + "quantity INT NOT NULL, "
                + "unit_price DECIMAL(10, 2) NOT NULL, "
                + "subtotal DECIMAL(10, 2) NOT NULL, "
                + "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (crop_id) REFERENCES crops(crop_id) ON DELETE RESTRICT"
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createCropsTable);
            stmt.execute(createOrdersTable);
            stmt.execute(createOrderItemsTable);
            insertSeedDataIfEmpty(conn);
        }
    }

    // SQL / CRUD CONCEPT: Seed Data
    // Inserts default sample farmers, customers, and crops if database is currently empty
    private static void insertSeedDataIfEmpty(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Check if users exist
            var rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM users;");
            if (rs.next() && rs.getInt("total") == 0) {
                // Seed Farmers & Customers
                String seedSql = "INSERT INTO users (user_id, name, username, password, role, contact_info) VALUES "
                        + "(1, 'Ramesh Kumar (Green Valley Farms)', 'farmer1', ?, 'FARMER', 'Guntur, AP - Ph: 9876543210'), "
                        + "(2, 'Suresh Patel (Sunshine Organic Farm)', 'farmer2', ?, 'FARMER', 'Krishna District - Ph: 9876543211'), "
                        + "(3, 'Anita Sharma', 'customer1', ?, 'CUSTOMER', 'Flat 402, Lotus Apts, Vijayawada'), "
                        + "(4, 'Vikram Reddy', 'customer2', ?, 'CUSTOMER', 'Plot 18, Green Meadows, Guntur');";
                try (var seedStatement = conn.prepareStatement(seedSql)) {
                    seedStatement.setString(1, "120000:zKhlgeAthkSRrXsm2MIIMg==:cIxtJQeJTvn2eI6b0DVc6FXZMKCQv3+VyFgE5Rk1pZo=");
                    seedStatement.setString(2, "120000:zKhlgeAthkSRrXsm2MIIMg==:cIxtJQeJTvn2eI6b0DVc6FXZMKCQv3+VyFgE5Rk1pZo=");
                    seedStatement.setString(3, "120000:pzxOjGEVoGelRcG/IZRiLA==:ctJXkPMWpBDuLUX9o6AblfgoA0iMH3ymUc5qc6zIFJY=");
                    seedStatement.setString(4, "120000:pzxOjGEVoGelRcG/IZRiLA==:ctJXkPMWpBDuLUX9o6AblfgoA0iMH3ymUc5qc6zIFJY=");
                    seedStatement.executeUpdate();
                }

                // Seed Crops
                stmt.executeUpdate("INSERT INTO crops (crop_id, farmer_id, crop_name, description, quantity, price, available) VALUES "
                        + "(1, 1, 'Organic Tomato', 'Freshly harvested farm-fresh red ripe tomatoes (per kg)', 150, 35.00, TRUE), "
                        + "(2, 1, 'Russet Potato', 'Cleaned grade-A earthy potatoes suitable for all dishes (per kg)', 200, 28.00, TRUE), "
                        + "(3, 1, 'Red Onion', 'Crisp pungent red onions grown with minimal pesticides (per kg)', 120, 42.00, TRUE), "
                        + "(4, 2, 'Fresh Carrot', 'Sweet and crunchy orange carrots rich in vitamin A (per kg)', 80, 50.00, TRUE), "
                        + "(5, 2, 'Organic Spinach', 'Tender green spinach leaves harvested every morning (per bunch)', 90, 20.00, TRUE), "
                        + "(6, 2, 'Guntur Green Chilli', 'Spicy aromatic green chillies freshly plucked (per kg)', 60, 65.00, TRUE);");
            }
        }
    }

    // Helper method to test connectivity
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
