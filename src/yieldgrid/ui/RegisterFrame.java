package yieldgrid.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import yieldgrid.exception.AuthenticationException;
import yieldgrid.exception.ValidationException;
import yieldgrid.service.AuthenticationService;

/*
 * COURSE CONCEPT:
 * Java Swing, Event Handling, Layout Managers, Exception Handling
 *
 * PURPOSE:
 * RegisterFrame allows new Farmers and Customers to sign up for YieldGrid.
 * It collects credentials, applies client validation, and triggers registration.
 */

public class RegisterFrame extends JFrame {

    private JTextField txtName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JTextField txtContact;
    private JButton btnSubmit;
    private JButton btnBackToLogin;
    private final AuthenticationService authService;
    private final LoginFrame parentLoginFrame;

    public RegisterFrame(LoginFrame parentLoginFrame) {
        this.parentLoginFrame = parentLoginFrame;
        this.authService = new AuthenticationService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("YieldGrid — Register New Account");
        setSize(500, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.COLOR_BG);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_PRIMARY_DARK);
        headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("CREATE AN ACCOUNT", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Join YieldGrid as a Farmer or Customer", SwingConstants.CENTER);
        lblSubtitle.setFont(UITheme.FONT_REGULAR);
        lblSubtitle.setForeground(new Color(220, 237, 200));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Card
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblName = new JLabel("Full Name / Farm Name:");
        lblName.setFont(UITheme.FONT_BOLD);
        card.add(lblName, gbc);

        gbc.gridx = 1;
        txtName = new JTextField(16);
        card.add(txtName, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BOLD);
        card.add(lblUser, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(16);
        card.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BOLD);
        card.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(16);
        card.add(txtPassword, gbc);

        // Role Selection
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblRole = new JLabel("Account Role:");
        lblRole.setFont(UITheme.FONT_BOLD);
        card.add(lblRole, gbc);

        gbc.gridx = 1;
        String[] roles = {"FARMER", "CUSTOMER"};
        cmbRole = new JComboBox<>(roles);
        cmbRole.setFont(UITheme.FONT_REGULAR);
        card.add(cmbRole, gbc);

        // Contact Info / Location
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblContact = new JLabel("Location / Contact Phone:");
        lblContact.setFont(UITheme.FONT_BOLD);
        card.add(lblContact, gbc);

        gbc.gridx = 1;
        txtContact = new JTextField(16);
        card.add(txtContact, gbc);

        // Submit Button
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        btnSubmit = UITheme.createPrimaryButton("Create Account");
        card.add(btnSubmit, gbc);

        // Back to Login Button
        gbc.gridy = 6;
        btnBackToLogin = new JButton("Already have an account? Sign in");
        btnBackToLogin.setFont(UITheme.FONT_REGULAR);
        btnBackToLogin.setForeground(UITheme.COLOR_ACCENT);
        btnBackToLogin.setContentAreaFilled(false);
        btnBackToLogin.setBorderPainted(false);
        btnBackToLogin.setFocusPainted(false);
        card.add(btnBackToLogin, gbc);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UITheme.COLOR_BG);
        centerWrapper.add(card);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        add(mainPanel);

        attachHandlers();
    }

    private void attachHandlers() {
        // EVENT HANDLING:
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        btnBackToLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentLoginFrame.setVisible(true);
                dispose();
            }
        });
    }

    private void handleRegistration() {
        String name = txtName.getText();
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();
        String contact = txtContact.getText();

        try {
            authService.register(name, username, password, role, contact);
            JOptionPane.showMessageDialog(this,
                    "Account registered successfully!\nYou may now log in with your credentials.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            parentLoginFrame.setVisible(true);
            dispose();

        } catch (ValidationException | AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
