package yieldgrid.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import yieldgrid.exception.InsufficientStockException;
import yieldgrid.exception.ValidationException;
import yieldgrid.model.Crop;
import yieldgrid.model.Customer;
import yieldgrid.service.CropService;
import yieldgrid.service.OrderService;

/*
 * COURSE CONCEPT:
 * Java Swing, Collections (ArrayList), String Handling, Object Interaction, Event Handling
 *
 * PURPOSE:
 * CropBrowsePanel allows Customers to browse available farm produce directly from farmers,
 * filter crops by name/description using String operations, and add crops to their shopping cart.
 */

public class CropBrowsePanel extends JPanel {

    private final Customer customer;
    private final CropService cropService;
    private final OrderService orderService;
    private final Runnable cartUpdateCallback; // Callback to notify dashboard when cart changes

    private JTable catalogTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JSpinner spinQuantity;
    private JButton btnAddToCart;
    private JButton btnRefresh;

    private ArrayList<Crop> allAvailableCrops;
    private ArrayList<Crop> filteredCrops;

    public CropBrowsePanel(Customer customer, Runnable cartUpdateCallback) {
        this.customer = customer;
        this.cartUpdateCallback = cartUpdateCallback;
        this.cropService = new CropService();
        this.orderService = new OrderService();
        this.allAvailableCrops = new ArrayList<>();
        this.filteredCrops = new ArrayList<>();
        initializeUI();
        loadAvailableCrops();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TOP: Search Bar & Refresh
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBackground(UITheme.COLOR_BG);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setBackground(UITheme.COLOR_BG);

        JLabel lblSearch = new JLabel("Search Produce:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        lblSearch.setForeground(UITheme.COLOR_TEXT_DARK);
        searchPanel.add(lblSearch);

        // STRING HANDLING: Search input field
        txtSearch = new JTextField(18);
        txtSearch.setFont(UITheme.FONT_REGULAR);
        searchPanel.add(txtSearch);

        JButton btnClearSearch = new JButton("Clear");
        btnClearSearch.setFont(UITheme.FONT_REGULAR);
        searchPanel.add(btnClearSearch);

        topBar.add(searchPanel, BorderLayout.WEST);

        btnRefresh = new JButton("Refresh Marketplace");
        btnRefresh.setFont(UITheme.FONT_REGULAR);
        topBar.add(btnRefresh, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // CENTER: JTable of Available Crops
        String[] columns = {"ID", "Crop", "Farmer / Farm", "Description", "Stock Available", "Price (Rs./unit)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        catalogTable = new JTable(tableModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.formatTable(catalogTable);

        JScrollPane scrollPane = new JScrollPane(catalogTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        add(scrollPane, BorderLayout.CENTER);

        // BOTTOM: Quantity Selector & Add to Cart
        JPanel bottomPanel = UITheme.createCardPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 8));

        JLabel lblQty = new JLabel("Select Quantity:");
        lblQty.setFont(UITheme.FONT_BOLD);
        lblQty.setForeground(UITheme.COLOR_TEXT_DARK);
        bottomPanel.add(lblQty);

        // JSpinner allows intuitive numerical quantity selection
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinQuantity.setPreferredSize(new Dimension(70, 30));
        spinQuantity.setFont(UITheme.FONT_BOLD);
        bottomPanel.add(spinQuantity);

        btnAddToCart = UITheme.createPrimaryButton("Add to Shopping Cart");
        bottomPanel.add(btnAddToCart);

        add(bottomPanel, BorderLayout.SOUTH);

        attachHandlers(btnClearSearch);
    }

    private void attachHandlers(JButton btnClearSearch) {
        // STRING HANDLING: Real-time search filter on key release
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
            }
        });

        btnClearSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                applySearchFilter();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAvailableCrops();
            }
        });

        // OBJECT INTERACTION & EVENT HANDLING: Add to Cart button
        btnAddToCart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddToCart();
            }
        });
    }

    // DATABASE RETRIEVAL & JAVA COLLECTIONS (ArrayList):
    public void loadAvailableCrops() {
        try {
            allAvailableCrops = cropService.getAllAvailableCrops();
            applySearchFilter();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load crops: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // STRING HANDLING & CONDITIONAL FILTERING:
    // Filters the collection of crops using String manipulation (toLowerCase & contains)
    private void applySearchFilter() {
        String query = txtSearch.getText().trim().toLowerCase();
        filteredCrops.clear();
        tableModel.setRowCount(0);

        for (Crop crop : allAvailableCrops) {
            // STRING OPERATIONS:
            boolean matchesName = crop.getCropName().toLowerCase().contains(query);
            boolean matchesDesc = crop.getDescription() != null && crop.getDescription().toLowerCase().contains(query);
            boolean matchesFarmer = crop.getFarmerName() != null && crop.getFarmerName().toLowerCase().contains(query);

            if (query.isEmpty() || matchesName || matchesDesc || matchesFarmer) {
                filteredCrops.add(crop);
                Object[] row = {
                    crop.getCropId(),
                    crop.getCropName(),
                    crop.getFarmerName(),
                    crop.getDescription(),
                    crop.getQuantity(),
                    String.format("%.2f", crop.getPrice())
                };
                tableModel.addRow(row);
            }
        }
    }

    // OBJECT INTERACTION & EXCEPTION HANDLING:
    private void handleAddToCart() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= filteredCrops.size()) {
            JOptionPane.showMessageDialog(this, "Please select a crop from the marketplace table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Crop selectedCrop = filteredCrops.get(selectedRow);
        int quantity = (Integer) spinQuantity.getValue();

        try {
            // Service updates the in-memory cart with validation
            orderService.addToCart(selectedCrop, quantity);

            JOptionPane.showMessageDialog(this,
                    "Added " + quantity + " units of '" + selectedCrop.getCropName() + "' to your cart!",
                    "Cart Updated",
                    JOptionPane.INFORMATION_MESSAGE);

            if (cartUpdateCallback != null) {
                cartUpdateCallback.run(); // Updates cart badge / total in customer dashboard
            }

        } catch (ValidationException | InsufficientStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Add to Cart", JOptionPane.ERROR_MESSAGE);
        }
    }
}
