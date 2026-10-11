package yieldgrid.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * COURSE CONCEPT:
 * Java Swing, AWT Color, Font, Component Styling & Reusability
 *
 * PURPOSE:
 * UITheme encapsulates the visual styling and aesthetic palette for YieldGrid.
 * Implements the required restrained agricultural visual identity:
 * - Cream / warm off-white background
 * - Muted dark green primary accents
 * - Dark earthy brown text
 * - Warm gold / amber accent highlights
 */

public class UITheme {

    // AWT COLOR PALETTE:
    public static final Color COLOR_PRIMARY = new Color(46, 125, 50);       // Forest Green
    public static final Color COLOR_PRIMARY_DARK = new Color(27, 94, 32);  // Deep Foliage Green
    public static final Color COLOR_ACCENT = new Color(188, 108, 37);      // Earthy Amber / Gold
    public static final Color COLOR_BG = new Color(250, 247, 238);         // Warm Cream Background
    public static final Color COLOR_CARD_BG = new Color(255, 255, 255);    // Clean Card White
    public static final Color COLOR_TEXT_DARK = new Color(50, 35, 25);     // Dark Earth Brown Text
    public static final Color COLOR_TEXT_MUTED = new Color(110, 100, 90);  // Subtle Gray-Brown
    public static final Color COLOR_BORDER = new Color(225, 220, 210);     // Light Tan Border
    public static final Color COLOR_SUCCESS = new Color(46, 125, 50);
    public static final Color COLOR_DANGER = new Color(198, 40, 40);

    // FONTS:
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    // HELPER: Style primary action button
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    // HELPER: Style accent button
    public static JButton createAccentButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(COLOR_ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    // HELPER: Style danger / cancel button
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(COLOR_DANGER);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    // HELPER: Create standard padded card panel
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        return card;
    }

    // HELPER: Style JTable header and row appearance
    public static void formatTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(28);
        table.setGridColor(COLOR_BORDER);
        table.setSelectionBackground(new Color(220, 237, 200));
        table.setSelectionForeground(COLOR_TEXT_DARK);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(new Color(238, 234, 222));
        table.getTableHeader().setForeground(COLOR_TEXT_DARK);
        table.getTableHeader().setPreferredSize(new Dimension(100, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    }
}
