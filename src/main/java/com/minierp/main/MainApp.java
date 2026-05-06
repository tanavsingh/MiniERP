package com.minierp.main;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.minierp.config.AppConfig;
import com.minierp.database.DatabaseInitializer;
import com.minierp.ui.LoginUI;
import com.minierp.ui.components.UITheme;

import javax.swing.*;
import java.awt.Font;

public class MainApp {
    public static void main(String[] args) {
        try {
            FlatDarculaLaf.install();
            System.out.println("[Theme] FlatDarculaLaf modern dark theme installed.");
        } catch (Exception e) {
            System.err.println("[Theme] FlatLaf failed, using system L&F: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {}
        }

        applyGlobalTheme();

        try {
            DatabaseInitializer.initialize();
            System.out.println("[DB] Initialized.");
        } catch (Exception e) {
            System.err.println("[DB] Failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
            System.out.println("[App] Started with dark SaaS theme.");
        });
    }

    private static void applyGlobalTheme() {
        UIManager.put("Panel.background", UITheme.BG_DARK);
        UIManager.put("Panel.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("Table.background", UITheme.BG_PANEL);
        UIManager.put("Table.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("TableHeader.background", UITheme.TABLE_HEADER);
        UIManager.put("TableHeader.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("Button.background", UITheme.BG_CARD);
        UIManager.put("Button.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("TextField.background", UITheme.BG_CARD);
        UIManager.put("TextField.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("Label.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("ComboBox.background", UITheme.BG_CARD);
        UIManager.put("ComboBox.foreground", UITheme.TEXT_PRIMARY);

        Font segoe = UITheme.FONT_BODY;
        UIManager.put("Label.font", segoe);
        UIManager.put("Table.font", segoe);
        UIManager.put("Button.font", segoe);
        UIManager.put("TextField.font", segoe);
        UIManager.put("ComboBox.font", segoe);
        UIManager.put("TableHeader.font", segoe.deriveFont(Font.BOLD));
    }
}

