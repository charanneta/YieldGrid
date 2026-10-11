package yieldgrid.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import yieldgrid.database.DBConnection;
import yieldgrid.model.Customer;
import yieldgrid.model.Farmer;
import yieldgrid.model.User;
import yieldgrid.security.PasswordUtil;

/*
 * COURSE CONCEPT:
 * JDBC, PreparedStatement, ResultSet, SQL CRUD (Create & Read), Polymorphism, Exception Handling
 *
 * PURPOSE:
 * UserDAO (Data Access Object) handles all persistent database interactions
 * for User accounts, including registration, authentication, and user profile retrieval.
 */

public class UserDAO {

    // CRUD CONCEPT: CREATE (INSERT)
    // PreparedStatement: Uses parameterised queries to insert new user records securely
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, username, password, role, contact_info) VALUES (?, ?, ?, ?, ?);";

        // JDBC CONCEPT: Connection & try-with-resources
        // Ensures resources (Connection, PreparedStatement) are automatically closed
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // PreparedStatement Parameter Binding:
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, PasswordUtil.hashPassword(user.getPassword()));
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getContactInfo());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // DATABASE RETRIEVAL: Retrieve auto-generated primary key
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // CRUD CONCEPT: READ (SELECT)
    // Authenticates user credentials and returns polymorphic User subclass (Farmer or Customer)
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, name, username, password, role, contact_info FROM users WHERE username = ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            // ResultSet extracts data returned by the executed query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (!PasswordUtil.matches(password, storedPassword)) {
                        return null;
                    }
                    User user = mapRowToUser(rs);
                    if (!PasswordUtil.isHashed(storedPassword)) {
                        upgradePasswordHash(user.getUserId(), password);
                    }
                    return user;
                }
            }

        }
        return null;
    }

    private void upgradePasswordHash(int userId, String password) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, PasswordUtil.hashPassword(password));
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    // CRUD CONCEPT: READ (SELECT)
    // Checks if a username is already taken during registration
    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // CRUD CONCEPT: READ (SELECT)
    // Fetches a User by their unique ID
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT user_id, name, username, password, role, contact_info FROM users WHERE user_id = ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    // OOP CONCEPT: Polymorphism / Factory Mapping
    // Returns either a Farmer or Customer instance referenced through the base class User type.
    private User mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String name = rs.getString("name");
        String uname = rs.getString("username");
        String pass = rs.getString("password");
        String role = rs.getString("role");
        String contact = rs.getString("contact_info");

        // CONDITIONAL LOGIC & POLYMORPHISM:
        // Demonstrates assigning concrete subclass objects to a User superclass reference
        User user;
        if ("FARMER".equalsIgnoreCase(role)) {
            user = new Farmer(id, name, uname, pass, contact);
        } else {
            user = new Customer(id, name, uname, pass, contact);
        }
        return user;
    }
}
