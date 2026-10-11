package yieldgrid.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import yieldgrid.interfaces.DashboardActions;
import yieldgrid.model.Farmer;
import yieldgrid.service.SessionManager;

/*
 * COURSE CONCEPT:
 * Interfaces (implements DashboardActions), Polymorphism, Java Swing, Event Handling, Layout Managers
 *
 * PURPOSE:
 * FarmerDashboard provides the primary workspace for registered Farmers.
 * It implements DashboardActions to guarantee standard lifecycle methods (refresh, profile, logout)
 * and organizes crop management, order fulfillment, and revenue metrics into organized tabs.
 */

// OOP CONCEPT: Interface Implementation
// Implements DashboardActions contract to support standardized dashboard actions
public class FarmerDashboard extends JFrame implements DashboardActions {

    private final Farmer farmer;

    private JTabbedPane tabbedPane;
    private CropManagementPanel cropManagementPanel;
    private OrdersPanel ordersPanel;
    private RevenuePanel revenuePanel;
    private ProfilePanel profilePanel;

    public FarmerDashboard(Farmer farmer) {
        this.farmer = farmer;
        initializeUI();
    }

    private void initializeUI() {
        // OOP CONCEPT: Polymorphism
        // Calls polymorphic getDashboardTitle() overridden in Farmer class
        setTitle(farmer.getDashboardTitle());
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.COLOR_BG);

        // TOP HEADER BAR:
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(UITheme.COLOR_PRIMARY_DARK);
        headerBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("YieldGrid — Farmer Operations Center");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        headerBar.add(lblTitle, BorderLayout.WEST);

        JPanel userSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userSection.setBackground(UITheme.COLOR_PRIMARY_DARK);

        JLabel lblUser = new JLabel("Logged in as: " + farmer.getName());
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(new Color(220, 237, 200));
        userSection.add(lblUser);

        JButton btnProfile = new JButton("Profile");
        btnProfile.setFont(UITheme.FONT_REGULAR);
        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProfile();
            }
        });
        userSection.add(btnProfile);

        JButton btnLogout = UITheme.createDangerButton("Logout");
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        userSection.add(btnLogout);

        headerBar.add(userSection, BorderLayout.EAST);
        mainPanel.add(headerBar, BorderLayout.NORTH);

        // TABBED PANE FOR REUSABLE MODULES:
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(UITheme.COLOR_CARD_BG);

        cropManagementPanel = new CropManagementPanel(farmer);
        ordersPanel = new OrdersPanel(farmer);
        revenuePanel = new RevenuePanel(farmer);
        profilePanel = new ProfilePanel(farmer);

        tabbedPane.addTab("Manage Crops", cropManagementPanel);
        tabbedPane.addTab("Incoming Orders", ordersPanel);
        tabbedPane.addTab("Revenue & Sales", revenuePanel);
        tabbedPane.addTab("My Profile", profilePanel);

        // EVENT HANDLING: Refresh panel data whenever the active tab changes
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refreshData();
            }
        });

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    // OOP CONCEPT: Interface Method Implementation
    @Override
    public void refreshData() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == 0 && cropManagementPanel != null) {
            cropManagementPanel.loadFarmerCrops();
        } else if (selectedIndex == 1 && ordersPanel != null) {
            ordersPanel.loadOrders();
        } else if (selectedIndex == 2 && revenuePanel != null) {
            revenuePanel.loadRevenueMetrics();
        }
    }

    // OOP CONCEPT: Interface Method Implementation
    @Override
    public void showProfile() {
        tabbedPane.setSelectedComponent(profilePanel);
    }

    // OOP CONCEPT: Interface Method Implementation
    @Override
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of YieldGrid?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }
}
