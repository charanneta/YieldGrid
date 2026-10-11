package yieldgrid.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import yieldgrid.exception.AuthenticationException;
import yieldgrid.exception.ValidationException;
import yieldgrid.model.Customer;
import yieldgrid.model.Farmer;
import yieldgrid.model.User;
import yieldgrid.service.AuthenticationService;

/*
 * COURSE CONCEPT:
 * Java Swing, Event Handling (ActionListener), Layout Managers (BorderLayout, GridBagLayout),
 * Polymorphism, Exception Handling
 *
 * PURPOSE:
 * LoginFrame is the entry GUI window for YieldGrid. It captures user credentials,
 * calls the AuthenticationService, and navigates polymorphically to either the
 * FarmerDashboard or CustomerDashboard based on the authenticated User type.
 */

// JAVA SWING:
// JFrame provides the top-level application window for authentication
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private final AuthenticationService authService;

    public LoginFrame() {
        this.authService = new AuthenticationService();
        initializeUI();
    }

    private void initializeUI() {
        // SWING WINDOW CONFIGURATION:
        setTitle("YieldGrid — Farm-to-Retail Inventory");
        setSize(460, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // LAYOUT MANAGER: BorderLayout for top-level division
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.COLOR_BG);

        // HEADER PANEL:
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_PRIMARY_DARK);
        headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("YIELDGRID", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Application for Farm-to-Retail Inventory", SwingConstants.CENTER);
        lblSubtitle.setFont(UITheme.FONT_REGULAR);
        lblSubtitle.setForeground(new Color(220, 237, 200));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // FORM CARD (Center):
        // LAYOUT MANAGER: GridBagLayout for precise form component alignment
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHeader = new JLabel("Sign In to Your Account", SwingConstants.CENTER);
        lblHeader.setFont(UITheme.FONT_SUBTITLE);
        lblHeader.setForeground(UITheme.COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formCard.add(lblHeader, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(UITheme.COLOR_TEXT_DARK);
        formCard.add(lblUser, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        txtUsername.setFont(UITheme.FONT_REGULAR);
        formCard.add(txtUsername, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BOLD);
        lblPass.setForeground(UITheme.COLOR_TEXT_DARK);
        formCard.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(UITheme.FONT_REGULAR);
        formCard.add(txtPassword, gbc);

        // Buttons
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        btnLogin = UITheme.createPrimaryButton("Login");
        formCard.add(btnLogin, gbc);

        gbc.gridy = 4;
        btnRegister = new JButton("New user? Register here");
        btnRegister.setFont(UITheme.FONT_REGULAR);
        btnRegister.setForeground(UITheme.COLOR_ACCENT);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        formCard.add(btnRegister, gbc);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UITheme.COLOR_BG);
        centerWrapper.add(formCard);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        add(mainPanel);

        // EVENT HANDLING:
        // Connect buttons to actions using ActionListener
        attachEventHandlers();
    }

    // EVENT HANDLING & EXCEPTION HANDLING:
    private void attachEventHandlers() {
        // OOP CONCEPT: Event Handling via ActionListener
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Trigger login when user presses ENTER inside password field
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Navigate to Registration screen
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame regFrame = new RegisterFrame(LoginFrame.this);
                regFrame.setVisible(true);
                LoginFrame.this.setVisible(false);
            }
        });
    }

    // METHOD CONCEPT / CONDITIONAL LOGIC / POLYMORPHISM:
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            // Service handles business validation and DB lookup
            User user = authService.login(username, password);

            // CONDITIONAL LOGIC & POLYMORPHISM:
            // The runtime type of the User instance dictates which specific dashboard to display
            if (user instanceof Farmer) {
                FarmerDashboard farmerDashboard = new FarmerDashboard((Farmer) user);
                farmerDashboard.setVisible(true);
            } else if (user instanceof Customer) {
                CustomerDashboard customerDashboard = new CustomerDashboard((Customer) user);
                customerDashboard.setVisible(true);
            }

            // Close current login frame
            dispose();

        } catch (ValidationException | AuthenticationException ex) {
            // EXCEPTION HANDLING:
            // Intercepts domain exceptions and displays clear feedback via JOptionPane
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
