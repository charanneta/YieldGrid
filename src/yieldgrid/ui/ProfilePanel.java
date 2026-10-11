package yieldgrid.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import yieldgrid.model.User;

/*
 * COURSE CONCEPT:
 * Java Swing, Encapsulation, Polymorphism, Layout Managers
 *
 * PURPOSE:
 * ProfilePanel displays authenticated user information. It reads encapsulated
 * fields through public getters and calls polymorphic methods defined on the User base class.
 */

public class ProfilePanel extends JPanel {

    // OOP CONCEPT: Polymorphism
    // Accepts any User (Farmer or Customer) through base class reference
    private final User user;

    public ProfilePanel(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("User Account Profile", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        // User ID
        addProfileRow(card, gbc, 1, "Account ID:", String.valueOf(user.getUserId()));
        // Name
        addProfileRow(card, gbc, 2, "Full Name / Organization:", user.getName());
        // Username
        addProfileRow(card, gbc, 3, "Username:", user.getUsername());
        // Role
        addProfileRow(card, gbc, 4, "System Role:", user.getRole());
        // Role Description (Polymorphic call)
        addProfileRow(card, gbc, 5, "Role Details:", user.getRoleDescription());
        // Contact
        addProfileRow(card, gbc, 6, "Contact / Location:", user.getContactInfo());

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UITheme.COLOR_BG);
        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void addProfileRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.COLOR_TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel val = new JLabel(value != null && !value.isEmpty() ? value : "Not Specified");
        val.setFont(UITheme.FONT_REGULAR);
        val.setForeground(UITheme.COLOR_TEXT_MUTED);
        panel.add(val, gbc);
    }
}
