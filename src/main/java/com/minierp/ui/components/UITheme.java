package com.minierp.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UITheme {
    
    public static final Color BG_DARK = new Color(0x0F172A);
    public static final Color BG_PANEL = new Color(0x1F2937);
    public static final Color BG_CARD = new Color(0x1E293B);
    public static final Color TABLE_HEADER = new Color(0x111827);
    public static final Color ROW_ALT = new Color(0x273449);
    public static final Color ACCENT = new Color(0x3B82F6);
    public static final Color ACCENT2 = new Color(0x60A5FA);
    public static final Color SELECTION_BG = new Color(0x2563EB);
    public static final Color SUCCESS = new Color(0x10B981);
    public static final Color WARNING = new Color(0xF59E0B);
    public static final Color DANGER = new Color(0xEF4444);
    public static final Color TEXT_PRIMARY = Color.WHITE;  // Black visibility fix - pure white for max contrast
    public static final Color TEXT_MUTED = new Color(0xD1D5DB);
    public static final Color BORDER = new Color(51, 65, 85, 128);


    public static final Color BG_SIDEBAR = BG_DARK; // Uses your dark blue for the sidebar
    public static final Color HOVER = new Color(0x334155); // A nice grey-blue for mouse hover


    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);

    public static JButton makeButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));

        Color hoverColor = brighter(bgColor, 1.15f);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    public static JButton makeAccentButton(String text) {
        return makeButton(text, ACCENT);
    }

    public static JButton makeSuccessButton(String text) {
        return makeButton(text, SUCCESS);
    }

    public static JButton makeDangerButton(String text) {
        return makeButton(text, DANGER);
    }

    public static JButton makeAccent2Button(String text) {
        return makeButton(text, ACCENT2);
    }

    public static JTextField makeTextField() {
        JTextField field = new JTextField();
        field.setBackground(BG_CARD);
        field.setForeground(TEXT_PRIMARY);
        field.setFont(FONT_BODY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return field;
    }

    public static JComboBox makeCombo(Object[] items) {
        JComboBox combo = new JComboBox<>(items);
        combo.setBackground(BG_CARD);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(FONT_BODY);
        return combo;
    }

    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    public static JScrollPane makeScrollPane(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(BG_DARK);
        return scroll;
    }

    public static JTable makeTable(TableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(42);
        table.setFont(FONT_BODY);
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setGridColor(Color.WHITE);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(new Color(230, 242, 255)); // Professional blue-white tint
        table.setSelectionForeground(Color.BLACK);
        
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(FONT_BODY.deriveFont(Font.BOLD, 14));
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.BLACK);
                if (isSelected) {
                    c.setBackground(new Color(230, 242, 255)); // Subtle blue tint for "fill here" focus
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
        
        return table;
    }

    private static Color brighter(Color c, float factor) {
        int r = Math.min(255, (int) (c.getRed() * factor));
        int g = Math.min(255, (int) (c.getGreen() * factor));
        int b = Math.min(255, (int) (c.getBlue() * factor));
        return new Color(r, g, b);
    }
}

