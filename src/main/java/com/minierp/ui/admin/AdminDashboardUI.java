package com.minierp.ui.admin;

import com.minierp.ui.components.*;
import com.minierp.ui.LoginUI;
import com.minierp.services.AuthService;
import javax.swing.*;
import java.awt.*;

public class AdminDashboardUI extends JFrame {
    private final AuthService auth = AuthService.getInstance();
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel = new JPanel(cardLayout);
    private Sidebar sidebar;

    public AdminDashboardUI() {
        setTitle("MiniERP — Admin Panel");
        setSize(1200, 750); // Slightly larger for better table view
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        // 1. Sidebar Setup
        sidebar = new Sidebar("Admin", "SYSTEM ADMINISTRATOR");
        sidebar.addSectionLabel("Management");
        sidebar.addNavItem("👨‍🏫", "Manage Teachers", "teachers");
        sidebar.addNavItem("📊", "System Stats", "stats");

        sidebar.setOnNavigate(key -> {
            if ("logout".equals(key)) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    auth.logout();
                    dispose();
                    new LoginUI().setVisible(true);
                }
            } else {
                cardLayout.show(contentPanel, key);
                sidebar.setSelected(key);
            }
        });

        // 2. Content Panel Setup (Replacing placeholders with actual functional classes)
        // Note: You must have created the TeacherManagementUI.java file I gave you earlier
        contentPanel.add(new TeacherManagementUI(), "teachers");

        // Stats Placeholder (You can create a StatsPanel.java later)
        JPanel statsPlaceholder = new JPanel(new GridBagLayout());
        statsPlaceholder.setBackground(UITheme.BG_DARK);
        statsPlaceholder.add(new JLabel("📊 System Statistics Panel Coming Soon"), new GridBagConstraints());
        contentPanel.add(statsPlaceholder, "stats");

        // 3. Main Layout
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Default View
        cardLayout.show(contentPanel, "teachers");
        sidebar.setSelected("teachers");
    }
}
