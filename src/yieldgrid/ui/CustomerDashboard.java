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
import yieldgrid.model.Customer;
import yieldgrid.service.SessionManager;

/*
 * COURSE CONCEPT:
 * Interfaces (implements DashboardActions), Polymorphism, Java Swing, Event Handling, Inter-Component Coordination
 *
 * PURPOSE:
 * CustomerDashboard provides the consumer shopping experience in YieldGrid.
 * It coordinates crop browsing, cart state updates, and order tracking tabs,
 * implementing DashboardActions for uniform application navigation.
 */

// OOP CONCEPT: Interface Implementation
public class CustomerDashboard extends JFrame implements DashboardActions {

    private final Customer customer;

    private JTabbedPane tabbedPane;
    private CropBrowsePanel cropBrowsePanel;
    private CartPanel cartPanel;
    private OrdersPanel ordersPanel;
    private ProfilePanel profilePanel;
    private JButton btnCartQuick;

    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        initializeUI();
    }

    private void initializeUI() {
        // OOP CONCEPT: Polymorphism
        setTitle(customer.getDashboardTitle());
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.COLOR_BG);

        // TOP HEADER BAR:
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(UITheme.COLOR_PRIMARY_DARK);
        headerBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("YieldGrid — Direct Farm Marketplace");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        headerBar.add(lblTitle, BorderLayout.WEST);

        JPanel userSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userSection.setBackground(UITheme.COLOR_PRIMARY_DARK);

        JLabel lblUser = new JLabel("Hello, " + customer.getName());
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(new Color(220, 237, 200));
        userSection.add(lblUser);

        // Quick Cart Access Button with dynamic count
        btnCartQuick = UITheme.createAccentButton("Cart (0)");
        btnCartQuick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedComponent(cartPanel);
            }
        });
        userSection.add(btnCartQuick);

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

        // TABBED PANE:
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(UITheme.COLOR_CARD_BG);

        // Pass callbacks to coordinate inter-panel updates
        cropBrowsePanel = new CropBrowsePanel(customer, new Runnable() {
            @Override
            public void run() {
                updateCartBadge();
                cartPanel.refreshCartDisplay();
            }
        });

        cartPanel = new CartPanel(customer, new Runnable() {
            @Override
            public void run() {
                updateCartBadge();
                ordersPanel.loadOrders();
                tabbedPane.setSelectedComponent(ordersPanel); // Navigate to orders upon checkout
            }
        });

        ordersPanel = new OrdersPanel(customer);
        profilePanel = new ProfilePanel(customer);

        tabbedPane.addTab("Browse Produce", cropBrowsePanel);
        tabbedPane.addTab("My Shopping Cart", cartPanel);
        tabbedPane.addTab("My Orders & Tracking", ordersPanel);
        tabbedPane.addTab("My Profile", profilePanel);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refreshData();
            }
        });

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        updateCartBadge();
    }

    private void updateCartBadge() {
        int count = SessionManager.getActiveCart().size();
        btnCartQuick.setText("Cart (" + count + ")");
    }

    // OOP CONCEPT: Interface Method Implementation
    @Override
    public void refreshData() {
        int selected = tabbedPane.getSelectedIndex();
        if (selected == 0 && cropBrowsePanel != null) {
            cropBrowsePanel.loadAvailableCrops();
        } else if (selected == 1 && cartPanel != null) {
            cartPanel.refreshCartDisplay();
        } else if (selected == 2 && ordersPanel != null) {
            ordersPanel.loadOrders();
        }
        updateCartBadge();
    }

    @Override
    public void showProfile() {
        tabbedPane.setSelectedComponent(profilePanel);
    }

    @Override
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
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
