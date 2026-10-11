package yieldgrid.service;

import java.sql.SQLException;
import yieldgrid.dao.UserDAO;
import yieldgrid.exception.AuthenticationException;
import yieldgrid.exception.ValidationException;
import yieldgrid.model.Customer;
import yieldgrid.model.Farmer;
import yieldgrid.model.User;

/*
 * COURSE CONCEPT:
 * Business Validation, Exception Handling, Polymorphism, String Handling
 *
 * PURPOSE:
 * AuthenticationService enforces business rules for user registration and login,
 * ensuring clean separation between the GUI layer and direct database operations.
 */

public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    // Constructor injection if needed for testing
    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // BUSINESS LOGIC & VALIDATION:
    // Throws ValidationException when input is malformed, and AuthenticationException on invalid credentials
    public User login(String username, String password) throws ValidationException, AuthenticationException {
        // STRING HANDLING & VALIDATION:
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty.");
        }

        try {
            User user = userDAO.authenticate(username.trim(), password.trim());
            if (user == null) {
                // EXCEPTION HANDLING: Custom AuthenticationException thrown on credential mismatch
                throw new AuthenticationException("Invalid username or password. Please verify your credentials.");
            }
            // Set session
            SessionManager.setCurrentUser(user);
            return user;
        } catch (SQLException e) {
            throw new AuthenticationException("Database communication error during authentication: " + e.getMessage(), e);
        }
    }

    // BUSINESS LOGIC & VALIDATION:
    // Registers a new Farmer or Customer after strict validation checks
    public User register(String name, String username, String password, String role, String contactInfo)
            throws ValidationException, AuthenticationException {

        // STRING HANDLING & INPUT VALIDATION:
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Full name is required.");
        }
        if (username == null || username.trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long.");
        }
        if (password == null || password.trim().length() < 4) {
            throw new ValidationException("Password must be at least 4 characters long.");
        }
        if (role == null || (!role.equalsIgnoreCase("FARMER") && !role.equalsIgnoreCase("CUSTOMER"))) {
            throw new ValidationException("Role must be specified as either FARMER or CUSTOMER.");
        }

        try {
            if (userDAO.isUsernameTaken(username.trim())) {
                throw new ValidationException("The username '" + username.trim() + "' is already registered. Please choose another.");
            }

            // OOP CONCEPT: Polymorphism / Subclass instantiation
            User newUser;
            if ("FARMER".equalsIgnoreCase(role.trim())) {
                newUser = new Farmer(name.trim(), username.trim(), password.trim(), contactInfo != null ? contactInfo.trim() : "");
            } else {
                newUser = new Customer(name.trim(), username.trim(), password.trim(), contactInfo != null ? contactInfo.trim() : "");
            }

            boolean success = userDAO.registerUser(newUser);
            if (!success) {
                throw new AuthenticationException("Failed to save user account to database.");
            }

            return newUser;
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during registration: " + e.getMessage(), e);
        }
    }
}
