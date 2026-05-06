package com.minierp.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class Sidebar extends JPanel {
    private final List<JButton> navButtons = new ArrayList<>();
    private Consumer<String> onNavigate;
    private String selectedKey = "";

    public Sidebar(String username, String role) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_SIDEBAR);
        setPreferredSize(new Dimension(220, 0));

        // Logo / Header
        JPanel header = new JPanel();
        header.setBackground(UITheme.BG_SIDEBAR);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));

        JLabel logo = new JLabel("🎓 MiniERP");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(UITheme.ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLbl = new JLabel(username);
        userLbl.setFont(UITheme.FONT_BODY.deriveFont(Font.BOLD));
        userLbl.setForeground(UITheme.TEXT_PRIMARY);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLbl = new JLabel(role);
        roleLbl.setFont(UITheme.FONT_SMALL);
        roleLbl.setForeground(UITheme.TEXT_MUTED);
        roleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(12));
        header.add(userLbl);
        header.add(roleLbl);

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);

        // Nav area
        JPanel nav = new JPanel();
        nav.setBackground(UITheme.BG_SIDEBAR);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Footer logout button
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.BG_SIDEBAR);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 12, 16, 12));

        JButton logoutBtn = makeNavButton("⬅  Logout", "logout");
        logoutBtn.setForeground(UITheme.DANGER);
        footer.add(logoutBtn, BorderLayout.CENTER);

        JScrollPane navScroll = new JScrollPane(nav);
        navScroll.setBorder(null);
        navScroll.setBackground(UITheme.BG_SIDEBAR);
        navScroll.getViewport().setBackground(UITheme.BG_SIDEBAR);
        navScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(header, BorderLayout.NORTH);
        add(navScroll, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        this.putClientProperty("navPanel", nav);
    }

    public void addNavItem(String icon, String label, String key) {
        JPanel navPanel = (JPanel) getClientProperty("navPanel");
        JButton btn = makeNavButton(icon + "  " + label, key);
        navButtons.add(btn);
        navPanel.add(btn);
        navPanel.add(Box.createVerticalStrut(2));
    }

    public void addSectionLabel(String text) {
        JPanel navPanel = (JPanel) getClientProperty("navPanel");
        JLabel lbl = new JLabel("  " + text.toUpperCase());
        lbl.setFont(UITheme.FONT_SMALL.deriveFont(Font.BOLD));
        lbl.setForeground(UITheme.TEXT_MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(14, 12, 4, 0));
        navPanel.add(lbl);
    }

    private JButton makeNavButton(String text, String key) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(UITheme.TEXT_MUTED);
        btn.setBackground(UITheme.BG_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setPreferredSize(new Dimension(220, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 8));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!key.equals(selectedKey)) btn.setBackground(UITheme.HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!key.equals(selectedKey)) btn.setBackground(UITheme.BG_SIDEBAR);
            }
        });

        btn.addActionListener(e -> {
            setSelected(key);
            if (onNavigate != null) onNavigate.accept(key);
        });
        return btn;
    }

    public void setSelected(String key) {
        selectedKey = key;
        for (JButton b : navButtons) {
            String bKey = (String) b.getClientProperty("key");
            if (key.equals(bKey)) {
                b.setBackground(UITheme.BG_CARD);
                b.setForeground(UITheme.TEXT_PRIMARY);
            } else {
                b.setBackground(UITheme.BG_SIDEBAR);
                b.setForeground(UITheme.TEXT_MUTED);
            }
        }
    }

    public void setOnNavigate(Consumer<String> handler) { this.onNavigate = handler; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(UITheme.BORDER);
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
    }
}
