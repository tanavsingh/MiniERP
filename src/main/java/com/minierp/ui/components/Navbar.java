package com.minierp.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class Navbar extends JPanel {
    private final JLabel titleLabel;
    private final JLabel notifLabel;

    public Navbar(String pageTitle, Supplier<Integer> unreadCount) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PANEL);
        setPreferredSize(new Dimension(0, 58));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)));

        titleLabel = new JLabel(pageTitle);
        titleLabel.setFont(UITheme.FONT_HEADING);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        notifLabel = new JLabel("🔔");
        notifLabel.setFont(UITheme.FONT_BODY);
        notifLabel.setForeground(UITheme.TEXT_MUTED);
        notifLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel clockLabel = new JLabel();
        clockLabel.setFont(UITheme.FONT_SMALL);
        clockLabel.setForeground(UITheme.TEXT_MUTED);

        Timer timer = new Timer(1000, e -> {
            clockLabel.setText(new java.util.Date().toString().substring(0, 19));
            if (unreadCount != null) {
                int cnt = unreadCount.get();
                notifLabel.setText(cnt > 0 ? "🔔 " + cnt : "🔔");
                notifLabel.setForeground(cnt > 0 ? UITheme.WARNING : UITheme.TEXT_MUTED);
            }
        });
        timer.start();

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(clockLabel);
        right.add(notifLabel);

        add(titleLabel, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    public void setTitle(String title) { titleLabel.setText(title); }
}
