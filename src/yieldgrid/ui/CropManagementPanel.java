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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import yieldgrid.exception.ValidationException;
import yieldgrid.model.Crop;
import yieldgrid.model.Farmer;
import yieldgrid.service.CropService;

/*
 * COURSE CONCEPT:
 * Java Swing, SQL CRUD Operations, Event Handling, Collections (ArrayList),
 * Layout Managers, Exception Handling
 *
 * PURPOSE:
 * CropManagementPanel provides the inventory management interface for Farmers.
 * It demonstrates full CRUD operations:
 * - CREATE: Adding new crops to database
 * - READ: Displaying listed crops in a JTable via ArrayList
 * - UPDATE: Modifying price, quantity, and availability
 * - DELETE: Removing crop listings from MySQL
 */

public class CropManagementPanel extends JPanel {

    private final Farmer farmer;
    private final CropService cropService;

    // Swing Components
    private JTable cropTable;
    private DefaultTableModel tableModel;
    private JTextField txtCropName;
    private JTextField txtDescription;
    private JTextField txtQuantity;
    private JTextField txtPrice;
    private JCheckBox chkAvailable;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // In-memory collection of farmer's crops
    private ArrayList<Crop> currentCropList;
    private Integer selectedCropId = null;

    public CropManagementPanel(Farmer farmer) {
        this.farmer = farmer;
        this.cropService = new CropService();
        this.currentCropList = new ArrayList<>();
        initializeUI();
        loadFarmerCrops();
    }

    private void initializeUI() {
        // LAYOUT MANAGER: BorderLayout divides the panel into Form (Top/West) and Table (Center)
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // LEFT: Crop Input Form Card
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(360, 400));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblFormTitle = new JLabel("Crop Details");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formCard.add(lblFormTitle, gbc);

        gbc.gridwidth = 1;

        // Crop Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblName = new JLabel("Crop Name:");
        lblName.setFont(UITheme.FONT_BOLD);
        formCard.add(lblName, gbc);

        gbc.gridx = 1;
        txtCropName = new JTextField(14);
        formCard.add(txtCropName, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setFont(UITheme.FONT_BOLD);
        formCard.add(lblDesc, gbc);

        gbc.gridx = 1;
        txtDescription = new JTextField(14);
        formCard.add(txtDescription, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblQty = new JLabel("Stock Quantity:");
        lblQty.setFont(UITheme.FONT_BOLD);
        formCard.add(lblQty, gbc);

        gbc.gridx = 1;
        txtQuantity = new JTextField(14);
        formCard.add(txtQuantity, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblPrice = new JLabel("Price (Rs./unit):");
        lblPrice.setFont(UITheme.FONT_BOLD);
        formCard.add(lblPrice, gbc);

        gbc.gridx = 1;
        txtPrice = new JTextField(14);
        formCard.add(txtPrice, gbc);

        // Available checkbox
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblAvail = new JLabel("Active Listing:");
        lblAvail.setFont(UITheme.FONT_BOLD);
        formCard.add(lblAvail, gbc);

        gbc.gridx = 1;
        chkAvailable = new JCheckBox("Available for sale", true);
        chkAvailable.setBackground(UITheme.COLOR_CARD_BG);
        chkAvailable.setFont(UITheme.FONT_REGULAR);
        formCard.add(chkAvailable, gbc);

        // Form action buttons panel
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        btnPanel.setBackground(UITheme.COLOR_CARD_BG);

        // CRUD: CREATE button
        btnAdd = UITheme.createPrimaryButton("Add Crop");
        // CRUD: UPDATE button
        btnUpdate = UITheme.createAccentButton("Update");
        btnUpdate.setEnabled(false);
        // CRUD: DELETE button
        btnDelete = UITheme.createDangerButton("Delete");
        btnDelete.setEnabled(false);
        // Reset button
        btnClear = new JButton("Clear");
        btnClear.setFont(UITheme.FONT_REGULAR);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        formCard.add(btnPanel, gbc);

        add(formCard, BorderLayout.WEST);

        // CENTER: JTable displaying Farmer's listed crops
        // JAVA SWING: JTable & DefaultTableModel
        String[] columnNames = {"ID", "Crop Name", "Description", "Stock Qty", "Price (Rs.)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells read-only in table view
            }
        };

        cropTable = new JTable(tableModel);
        cropTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.formatTable(cropTable);

        // JScrollPane provides scroll bars when inventory grows
        JScrollPane scrollPane = new JScrollPane(cropTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(UITheme.COLOR_BG);

        JLabel lblTableTitle = new JLabel("My Listed Produce Inventory");
        lblTableTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTableTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        tableWrapper.add(lblTableTitle, BorderLayout.NORTH);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        add(tableWrapper, BorderLayout.CENTER);

        attachHandlers();
    }

    // EVENT HANDLING: Connects buttons and table selection events to methods
    private void attachHandlers() {
        // Table row selection handler
        cropTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && cropTable.getSelectedRow() != -1) {
                    populateFormFromSelectedRow();
                }
            }
        });

        // CRUD: CREATE
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateCrop();
            }
        });

        // CRUD: UPDATE
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateCrop();
            }
        });

        // CRUD: DELETE
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteCrop();
            }
        });

        // Form Clear
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }

    // CRUD: READ & JAVA COLLECTIONS (ArrayList)
    public void loadFarmerCrops() {
        try {
            // DATABASE RETRIEVAL: Queries MySQL via CropService & CropDAO
            currentCropList = cropService.getCropsByFarmer(farmer.getUserId());
            tableModel.setRowCount(0);

            // ARRAYLIST ITERATION:
            for (Crop crop : currentCropList) {
                Object[] rowData = {
                    crop.getCropId(),
                    crop.getCropName(),
                    crop.getDescription(),
                    crop.getQuantity(),
                    String.format("%.2f", crop.getPrice()),
                    crop.isAvailable() ? "Available" : "Out of Stock"
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching crops: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CRUD: CREATE OPERATION
    private void handleCreateCrop() {
        try {
            String name = txtCropName.getText();
            String desc = txtDescription.getText();
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            double price = Double.parseDouble(txtPrice.getText().trim());
            boolean avail = chkAvailable.isSelected();

            // Service handles business validation and DB insertion
            cropService.addCrop(farmer.getUserId(), name, desc, qty, price, avail);

            JOptionPane.showMessageDialog(this, "Crop '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadFarmerCrops();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantity must be an integer and Price must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CRUD: UPDATE OPERATION
    private void handleUpdateCrop() {
        if (selectedCropId == null) {
            return;
        }

        try {
            String name = txtCropName.getText();
            String desc = txtDescription.getText();
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            double price = Double.parseDouble(txtPrice.getText().trim());
            boolean avail = chkAvailable.isSelected();

            Crop cropToUpdate = new Crop(selectedCropId, farmer.getUserId(), name, desc, qty, price, avail);
            boolean updated = cropService.updateCrop(cropToUpdate, farmer.getUserId());

            if (updated) {
                JOptionPane.showMessageDialog(this, "Crop updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadFarmerCrops();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update crop.", "Update Failed", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be valid numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CRUD: DELETE OPERATION
    private void handleDeleteCrop() {
        if (selectedCropId == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this crop listing?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = cropService.deleteCrop(selectedCropId, farmer.getUserId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Crop deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadFarmerCrops();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete crop record.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (ValidationException | SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = cropTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentCropList.size()) {
            Crop crop = currentCropList.get(selectedRow);
            selectedCropId = crop.getCropId();
            txtCropName.setText(crop.getCropName());
            txtDescription.setText(crop.getDescription());
            txtQuantity.setText(String.valueOf(crop.getQuantity()));
            txtPrice.setText(String.valueOf(crop.getPrice()));
            chkAvailable.setSelected(crop.isAvailable());

            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    private void clearForm() {
        selectedCropId = null;
        txtCropName.setText("");
        txtDescription.setText("");
        txtQuantity.setText("");
        txtPrice.setText("");
        chkAvailable.setSelected(true);
        cropTable.clearSelection();

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}
