package yieldgrid.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import yieldgrid.exception.ValidationException;
import yieldgrid.model.Customer;
import yieldgrid.model.Farmer;
import yieldgrid.model.Order;
import yieldgrid.model.OrderItem;
import yieldgrid.model.OrderStatus;
import yieldgrid.model.User;
import yieldgrid.service.OrderService;

/*
 * COURSE CONCEPT:
 * Java Swing, Java Enum (OrderStatus), Polymorphism, Object Composition (Order HAS-A OrderItem),
 * Event Handling, Database Retrieval
 *
 * PURPOSE:
 * OrdersPanel serves both Farmers and Customers:
 * - When viewed by a Farmer: Shows incoming orders, line items, and provides status update controls.
 * - When viewed by a Customer: Provides order history and fulfillment tracking.
 */

public class OrdersPanel extends JPanel {

    private final User currentUser;
    private final boolean isFarmer;
    private final OrderService orderService;

    private JTable ordersTable;
    private DefaultTableModel ordersModel;
    private JTable itemsTable;
    private DefaultTableModel itemsModel;

    private JComboBox<OrderStatus> cmbStatus;
    private JButton btnUpdateStatus;
    private JButton btnRefresh;

    private ArrayList<Order> loadedOrders;
    private Order selectedOrder = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public OrdersPanel(User currentUser) {
        this.currentUser = currentUser;
        this.isFarmer = currentUser instanceof Farmer;
        this.orderService = new OrderService();
        this.loadedOrders = new ArrayList<>();
        initializeUI();
        loadOrders();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // HEADER & CONTROLS:
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.COLOR_BG);

        String title = isFarmer ? "Incoming Customer Orders & Status Management" : "My Order History & Real-Time Tracking";
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        topPanel.add(lblTitle, BorderLayout.WEST);

        btnRefresh = new JButton("Refresh Orders");
        btnRefresh.setFont(UITheme.FONT_REGULAR);
        topPanel.add(btnRefresh, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // SPLIT PANE: Top Table (Orders) and Bottom Table (Order Line Items)
        // 1. Orders Table:
        String otherPartyCol = isFarmer ? "Customer" : "Farmer / Producer";
        String[] orderCols = {"Order ID", otherPartyCol, "Order Date", "Total (Rs.)", "Order Status"};
        ordersModel = new DefaultTableModel(orderCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        ordersTable = new JTable(ordersModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.formatTable(ordersTable);
        JScrollPane orderScroll = new JScrollPane(ordersTable);
        orderScroll.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));

        // 2. Order Items Table (Composition Detail View):
        String[] itemCols = {"Line Item ID", "Crop Name", "Quantity", "Unit Price (Rs.)", "Subtotal (Rs.)"};
        itemsModel = new DefaultTableModel(itemCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        itemsTable = new JTable(itemsModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.formatTable(itemsTable);
        JScrollPane itemScroll = new JScrollPane(itemsTable);
        itemScroll.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));

        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
        itemPanel.setBackground(UITheme.COLOR_BG);
        JLabel lblItems = new JLabel("Order Line Items (Composition Details):");
        lblItems.setFont(UITheme.FONT_BOLD);
        lblItems.setForeground(UITheme.COLOR_TEXT_DARK);
        itemPanel.add(lblItems, BorderLayout.NORTH);
        itemPanel.add(itemScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, orderScroll, itemPanel);
        splitPane.setResizeWeight(0.55);
        splitPane.setDividerSize(6);
        add(splitPane, BorderLayout.CENTER);

        // SOUTH: Status Update Panel (Only active for Farmer role)
        if (isFarmer) {
            JPanel farmerActionCard = UITheme.createCardPanel();
            farmerActionCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

            JLabel lblUpdate = new JLabel("Change Order Status:");
            lblUpdate.setFont(UITheme.FONT_BOLD);
            lblUpdate.setForeground(UITheme.COLOR_TEXT_DARK);
            farmerActionCard.add(lblUpdate);

            // JAVA ENUM CONCEPT:
            // JComboBox populated directly from OrderStatus.values()
            cmbStatus = new JComboBox<>(OrderStatus.values());
            cmbStatus.setFont(UITheme.FONT_REGULAR);
            cmbStatus.setPreferredSize(new Dimension(160, 30));
            farmerActionCard.add(cmbStatus);

            btnUpdateStatus = UITheme.createAccentButton("Update Status");
            btnUpdateStatus.setEnabled(false);
            farmerActionCard.add(btnUpdateStatus);

            JLabel lblHint = new JLabel("<html><font color='#666666'>* Marking COMPLETED credits your revenue.</font></html>");
            farmerActionCard.add(lblHint);

            add(farmerActionCard, BorderLayout.SOUTH);
        }

        attachHandlers();
    }

    private void attachHandlers() {
        ordersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && ordersTable.getSelectedRow() != -1) {
                    displaySelectedOrderItems();
                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadOrders();
            }
        });

        if (isFarmer && btnUpdateStatus != null) {
            btnUpdateStatus.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleStatusUpdate();
                }
            });
        }
    }

    // DATABASE RETRIEVAL & POLYMORPHISM:
    public void loadOrders() {
        try {
            if (isFarmer) {
                loadedOrders = orderService.getOrdersForFarmer(currentUser.getUserId());
            } else {
                loadedOrders = orderService.getOrdersForCustomer(currentUser.getUserId());
            }

            ordersModel.setRowCount(0);
            itemsModel.setRowCount(0);

            for (Order order : loadedOrders) {
                String otherParty = isFarmer ? order.getCustomerName() : order.getFarmerName();
                String dateStr = order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "N/A";

                Object[] row = {
                    order.getOrderId(),
                    otherParty,
                    dateStr,
                    String.format("%.2f", order.getTotalAmount()),
                    order.getStatus().name() // ENUM to String
                };
                ordersModel.addRow(row);
            }

            if (btnUpdateStatus != null) {
                btnUpdateStatus.setEnabled(false);
            }
            selectedOrder = null;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load orders: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // OBJECT COMPOSITION: Displaying individual OrderItem objects belonging to the selected Order
    private void displaySelectedOrderItems() {
        int row = ordersTable.getSelectedRow();
        if (row >= 0 && row < loadedOrders.size()) {
            selectedOrder = loadedOrders.get(row);
            itemsModel.setRowCount(0);

            // JAVA COLLECTIONS: ArrayList<OrderItem>
            for (OrderItem item : selectedOrder.getItems()) {
                Object[] itemRow = {
                    item.getOrderItemId(),
                    item.getCropName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getUnitPrice()),
                    String.format("%.2f", item.getSubtotal())
                };
                itemsModel.addRow(itemRow);
            }

            if (isFarmer && cmbStatus != null) {
                cmbStatus.setSelectedItem(selectedOrder.getStatus());
                btnUpdateStatus.setEnabled(true);
            }
        }
    }

    // ENUM & CRUD UPDATE:
    private void handleStatusUpdate() {
        if (selectedOrder == null) {
            return;
        }

        OrderStatus newStatus = (OrderStatus) cmbStatus.getSelectedItem();
        if (newStatus == null) {
            return;
        }

        try {
            boolean updated = orderService.updateOrderStatus(selectedOrder.getOrderId(), newStatus, currentUser.getUserId());
            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "Order #" + selectedOrder.getOrderId() + " status updated to: " + newStatus,
                        "Status Updated",
                        JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order status.", "Update Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (ValidationException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
