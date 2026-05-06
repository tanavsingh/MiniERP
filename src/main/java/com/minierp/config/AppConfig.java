package com.minierp.config;

public class AppConfig {
    public static final String APP_NAME = "Mini ERP - College Management System";
    public static final String APP_VERSION = "1.0.0";
    public static final String DB_PATH = "resources/database/erp.db";
    public static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    // Window
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 800;
    public static final int SIDEBAR_WIDTH = 220;

    // Colors (dark theme)
    public static final String BG_DARK = "#1A1A2E";
    public static final String BG_PANEL = "#16213E";
    public static final String BG_CARD = "#0F3460";
    public static final String ACCENT = "#E94560";
    public static final String TEXT_PRIMARY = "#EAEAEA";
    public static final String TEXT_SECONDARY = "#A0A0B0";

    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_FACULTY = "FACULTY";
    public static final String ROLE_STUDENT = "STUDENT";

    private AppConfig() {}
}
