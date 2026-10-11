package yieldgrid.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import yieldgrid.exception.InsufficientStockException;
import yieldgrid.exception.ValidationException;
import yieldgrid.model.CartItem;
import yieldgrid.model.Customer;
import yieldgrid.model.Order;
import yieldgrid.service.OrderService;
import yieldgrid.service.SessionManager;

/*
 * COURSE CONCEPT:
 * Collections (ArrayList), Object Interaction, Arithmetic Operations, Event Handling, Exception Handling
 *
 * PURPOSE:
 * CartPanel manages the customer's active shopping cart session.
 * It calculates the cart total through arithmetic operations on CartItem objects,
 * allows dynamic item removal, and triggers transactional order placement.
 */

public class CartPanel extends JPanel {

    private final Customer customer;
    private final OrderService orderService;
    private final Runnable orderPlacedCallback;

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel lblCartTotal;
    private JLabel lblItemCount;
    private JButton btnRemove;
    private JButton btnClear;
    private JButton btnPlaceOrder;

    public CartPanel(Customer customer, Runnable orderPlacedCallback) {
        this.customer = customer;
        this.orderPlacedCallback = orderPlacedCallback;
        this.orderService = new OrderService();
        initializeUI();
        refreshCartDisplay();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // HEADER
        JLabel lblTitle = new JLabel("Shopping Cart — Direct Farm Orders");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        add(lblTitle, BorderLayout.NORTH);

        // CENTER: Cart JTable
        String[] columns = {"Crop ID", "Crop Name", "Farmer", "Quantity", "Unit Price (Rs.)", "Subtotal (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        cartTable = new JTable(tableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.formatTable(cartTable);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        add(scrollPane, BorderLayout.CENTER);

        // SOUTH: Cart Actions and Checkout Card
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(UITheme.COLOR_BG);

        // Left Action Buttons (Remove, Clear)
        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        leftActions.setBackground(UITheme.COLOR_BG);

        btnRemove = UITheme.createDangerButton("Remove Selected");
        btnClear = new JButton("Clear Cart");
        btnClear.setFont(UITheme.FONT_REGULAR);

        leftActions.add(btnRemove);
        leftActions.add(btnClear);
        southPanel.add(leftActions, BorderLayout.WEST);

        // Right Summary Card (Total & Place Order)
        JPanel summaryCard = UITheme.createCardPanel();
        summaryCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 10, 4, 10);
        gbc.anchor = GridBagConstraints.EAST;

        lblItemCount = new JLabel("Items in Cart: 0");
        lblItemCount.setFont(UITheme.FONT_REGULAR);
        lblItemCount.setForeground(UITheme.COLOR_TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 0;
        summaryCard.add(lblItemCount, gbc);

        // ARITHMETIC DISPLAY:
        lblCartTotal = new JLabel("Total: Rs. 0.00");
        lblCartTotal.setFont(UITheme.FONT_SUBTITLE);
        lblCartTotal.setForeground(UITheme.COLOR_PRIMARY_DARK);
        gbc.gridx = 0; gbc.gridy = 1;
        summaryCard.add(lblCartTotal, gbc);

        btnPlaceOrder = UITheme.createPrimaryButton("Place Order Now");
        btnPlaceOrder.setPreferredSize(new Dimension(170, 36));
        gbc.gridx = 0; gbc.gridy = 2;
        summaryCard.add(btnPlaceOrder, gbc);

        southPanel.add(summaryCard, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        attachHandlers();
    }

    private void attachHandlers() {
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRemoveItem();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SessionManager.clearCart();
                refreshCartDisplay();
            }
        });

        btnPlaceOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePlaceOrder();
            }
        });
    }

    // COLLECTIONS (ArrayList) & ARITHMETIC OPERATIONS:
    public void refreshCartDisplay() {
        ArrayList<CartItem> cart = SessionManager.getActiveCart();
        tableModel.setRowCount(0);

        double total = 0.0;
        int totalUnits = 0;

        // ARRAYLIST ITERATION & OBJECT INTERACTION:
        for (CartItem item : cart) {
            double subtotal = item.getSubtotal(); // Arithmetic: price * quantity
            total += subtotal;
            totalUnits += item.getQuantity();

            Object[] row = {
                item.getCrop().getCropId(),
                item.getCrop().getCropName(),
                item.getCrop().getFarmerName() != null ? item.getCrop().getFarmerName() : ("Farmer #" + item.getCrop().getFarmerId()),
                item.getQuantity(),
                String.format("%.2f", item.getCrop().getPrice()),
                String.format("%.2f", subtotal)
            };
            tableModel.addRow(row);
        }

        // Update labels
        lblItemCount.setText("Items in Cart: " + cart.size() + " (" + totalUnits + " units)");
        lblCartTotal.setText("Total: Rs. " + String.format("%.2f", total));

        boolean hasItems = !cart.isEmpty();
        btnRemove.setEnabled(hasItems);
        btnClear.setEnabled(hasItems);
        btnPlaceOrder.setEnabled(hasItems);
    }

    private void handleRemoveItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the cart to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cropId = (Integer) tableModel.getValueAt(selectedRow, 0);
        orderService.removeFromCart(cropId);
        refreshCartDisplay();
    }

    // ORDER PLACEMENT & TRANSACTION HANDLING:
    private void handlePlaceOrder() {
        ArrayList<CartItem> cart = SessionManager.getActiveCart();
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Add crops before placing an order.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm order placement for " + lblCartTotal.getText() + "?",
                "Confirm Order Placement",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Service executes atomic transaction in MySQL, debiting stock
            List<Order> placedOrders = orderService.placeOrdersFromCart(customer.getUserId());

            StringBuilder sb = new StringBuilder();
            sb.append("Order Placed Successfully!\n\n");
            sb.append("Order Details:\n");
            for (Order o : placedOrders) {
                sb.append("• Order #").append(o.getOrderId())
                  .append(" (Status: ").append(o.getStatus()).append(")")
                  .append(" - Total: Rs. ").append(String.format("%.2f", o.getTotalAmount())).append("\n");
            }
            sb.append("\nYou can track its fulfillment status in the 'My Orders' tab.");

            JOptionPane.showMessageDialog(this, sb.toString(), "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);

            refreshCartDisplay();

            if (orderPlacedCallback != null) {
                orderPlacedCallback.run(); // Refreshes orders list in dashboard
            }

        } catch (ValidationException | InsufficientStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Order Could Not Be Placed", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error during order placement: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
