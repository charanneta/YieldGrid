package yieldgrid.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import yieldgrid.model.Farmer;
import yieldgrid.model.Order;
import yieldgrid.service.RevenueService;

/*
 * COURSE CONCEPT:
 * Methods / Reusability, Arithmetic Operations, Database Retrieval, Collections (ArrayList), Java Swing
 *
 * PURPOSE:
 * RevenuePanel displays sales metrics for Farmers. It calculates total revenue
 * earned from COMPLETED orders using arithmetic summation and lists the qualifying
 * completed order records retrieved via JDBC.
 */

public class RevenuePanel extends JPanel {

    private final Farmer farmer;
    private final RevenueService revenueService;

    private JLabel lblTotalRevenue;
    private JLabel lblCompletedCount;
    private JTable completedOrdersTable;
    private DefaultTableModel tableModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public RevenuePanel(Farmer farmer) {
        this.farmer = farmer;
        this.revenueService = new RevenueService();
        initializeUI();
        loadRevenueMetrics();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TOP: Metrics Cards (Total Revenue & Completed Orders)
        JPanel metricsPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        metricsPanel.setBackground(UITheme.COLOR_BG);

        // Metric Card 1: Total Revenue
        JPanel revCard = UITheme.createCardPanel();
        revCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblRevTitle = new JLabel("TOTAL REALIZED REVENUE", SwingConstants.CENTER);
        lblRevTitle.setFont(UITheme.FONT_SUBTITLE);
        lblRevTitle.setForeground(UITheme.COLOR_TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 0;
        revCard.add(lblRevTitle, gbc);

        // ARITHMETIC METRIC DISPLAY:
        lblTotalRevenue = new JLabel("Rs. 0.00", SwingConstants.CENTER);
        lblTotalRevenue.setFont(UITheme.FONT_TITLE);
        lblTotalRevenue.setForeground(UITheme.COLOR_PRIMARY_DARK);
        gbc.gridy = 1;
        revCard.add(lblTotalRevenue, gbc);

        JLabel lblRevSub = new JLabel("Sum of all COMPLETED orders", SwingConstants.CENTER);
        lblRevSub.setFont(UITheme.FONT_SMALL);
        lblRevSub.setForeground(UITheme.COLOR_TEXT_MUTED);
        gbc.gridy = 2;
        revCard.add(lblRevSub, gbc);

        metricsPanel.add(revCard);

        // Metric Card 2: Completed Orders Count
        JPanel countCard = UITheme.createCardPanel();
        countCard.setLayout(new GridBagLayout());

        JLabel lblCountTitle = new JLabel("FULFILLED ORDERS", SwingConstants.CENTER);
        lblCountTitle.setFont(UITheme.FONT_SUBTITLE);
        lblCountTitle.setForeground(UITheme.COLOR_TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 0;
        countCard.add(lblCountTitle, gbc);

        lblCompletedCount = new JLabel("0", SwingConstants.CENTER);
        lblCompletedCount.setFont(UITheme.FONT_TITLE);
        lblCompletedCount.setForeground(UITheme.COLOR_ACCENT);
        gbc.gridy = 1;
        countCard.add(lblCompletedCount, gbc);

        JLabel lblCountSub = new JLabel("Orders successfully delivered", SwingConstants.CENTER);
        lblCountSub.setFont(UITheme.FONT_SMALL);
        lblCountSub.setForeground(UITheme.COLOR_TEXT_MUTED);
        gbc.gridy = 2;
        countCard.add(lblCountSub, gbc);

        metricsPanel.add(countCard);

        add(metricsPanel, BorderLayout.NORTH);

        // CENTER: Completed Orders Breakdown Table
        JPanel tableContainer = new JPanel(new BorderLayout(8, 8));
        tableContainer.setBackground(UITheme.COLOR_BG);

        JPanel tableHeaderBar = new JPanel(new BorderLayout());
        tableHeaderBar.setBackground(UITheme.COLOR_BG);

        JLabel lblTable = new JLabel("Completed Orders Breakdown (Revenue Ledger)");
        lblTable.setFont(UITheme.FONT_SUBTITLE);
        lblTable.setForeground(UITheme.COLOR_PRIMARY_DARK);
        tableHeaderBar.add(lblTable, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Refresh Ledger");
        btnRefresh.setFont(UITheme.FONT_REGULAR);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRevenueMetrics();
            }
        });
        tableHeaderBar.add(btnRefresh, BorderLayout.EAST);

        tableContainer.add(tableHeaderBar, BorderLayout.NORTH);

        String[] columns = {"Order ID", "Customer Name", "Delivered Date", "Order Amount (Rs.)", "Fulfillment Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        completedOrdersTable = new JTable(tableModel);
        UITheme.formatTable(completedOrdersTable);

        JScrollPane scrollPane = new JScrollPane(completedOrdersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);
    }

    // ARITHMETIC OPERATIONS & DATABASE RETRIEVAL:
    public void loadRevenueMetrics() {
        try {
            // DATABASE RETRIEVAL:
            double revenue = revenueService.calculateFarmerRevenue(farmer.getUserId());
            int completedCount = revenueService.getCompletedOrdersCount(farmer.getUserId());
            ArrayList<Order> completedOrders = revenueService.getCompletedOrders(farmer.getUserId());

            // ARITHMETIC METRIC FORMATTING:
            lblTotalRevenue.setText("Rs. " + String.format("%.2f", revenue));
            lblCompletedCount.setText(String.valueOf(completedCount));

            // JAVA COLLECTIONS: ArrayList
            tableModel.setRowCount(0);
            for (Order o : completedOrders) {
                String dateStr = o.getOrderDate() != null ? dateFormat.format(o.getOrderDate()) : "N/A";
                Object[] row = {
                    o.getOrderId(),
                    o.getCustomerName(),
                    dateStr,
                    String.format("%.2f", o.getTotalAmount()),
                    o.getStatus().name()
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load revenue data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
