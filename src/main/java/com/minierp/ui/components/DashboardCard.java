package com.minierp.ui.components;

import javax.swing.*;
import java.awt.*;

public class DashboardCard extends JPanel {
    public DashboardCard(String title, String value, String subtitle, Color accentColor) {
        setLayout(new BorderLayout(0, 6));
        setBackground(UITheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        setPreferredSize(new Dimension(200, 130));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL.deriveFont(Font.BOLD));
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLbl.setForeground(accentColor);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.add(titleLbl);

        add(top, BorderLayout.NORTH);
        add(valueLbl, BorderLayout.CENTER);
        add(subLbl, BorderLayout.SOUTH);
    }

    public void setValue(String v) {
        for (Component c : getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getFont().getSize() == 32) {
                    lbl.setText(v);
                    break;
                }
            }
        }
    }
}
