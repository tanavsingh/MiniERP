package com.minierp.ui;

import com.minierp.controllers.LoginController;
import com.minierp.config.AppConfig;
import com.minierp.ui.components.UITheme;
import com.minierp.ui.faculty.FacultyDashboardUI;
import com.minierp.ui.student.StudentDashboardUI;
import com.minierp.services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginUI extends JFrame {
    private final LoginController controller = new LoginController();
    private JTextField userField;
    private JPasswordField passField;
    private JLabel statusLabel;

    public LoginUI() {
        setTitle(AppConfig.APP_NAME + " - Login");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_DARK);

        // Left Panel - Branding
        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_CARD, 0, getHeight(), UITheme.ACCENT2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        left.setLayout(new GridBagLayout());
        left.setPreferredSize(new Dimension(380, 0));

        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        JLabel emoji = new JLabel("🎓");
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        emoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Mini ERP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("College Management System");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(new Color(0xCCCCCC));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("<html><center>Manage students, faculty, exams,<br>attendance, and more — all in one place.</center></html>");
        desc.setFont(UITheme.FONT_SMALL);
        desc.setForeground(new Color(0xAAAAAA));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setHorizontalAlignment(SwingConstants.CENTER);

        brandPanel.add(emoji);
        brandPanel.add(Box.createVerticalStrut(12));
        brandPanel.add(title);
        brandPanel.add(Box.createVerticalStrut(6));
        brandPanel.add(subtitle);
        brandPanel.add(Box.createVerticalStrut(18));
        brandPanel.add(desc);

        left.add(brandPanel);

        // Right Panel - Login Form
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UITheme.BG_DARK);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        form.setMaximumSize(new Dimension(380, 600));

        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(UITheme.FONT_TITLE);
        loginTitle.setForeground(UITheme.TEXT_PRIMARY);
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginSub = new JLabel("Sign in to your account");
        loginSub.setFont(UITheme.FONT_BODY);
        loginSub.setForeground(UITheme.TEXT_MUTED);
        loginSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel userLbl = makeFieldLabel("Username");
        userField = UITheme.makeTextField();
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        userField.setText("faculty1");

        // Password
        JLabel passLbl = makeFieldLabel("Password");
        passField = new JPasswordField();
        passField.setBackground(UITheme.BG_CARD);
        passField.setForeground(UITheme.TEXT_PRIMARY);
        passField.setCaretColor(UITheme.TEXT_PRIMARY);
        passField.setFont(UITheme.FONT_BODY);
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passField.setText("faculty123");

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Button
        JButton loginBtn = new JButton("Sign In  →");
        loginBtn.setBackground(UITheme.ACCENT);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Demo hint
        JLabel hint = new JLabel("<html><font color='#8A8A9A'>Demo — Faculty: faculty1/faculty123 &nbsp;|&nbsp; Student: student1/student123</font></html>");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginBtn.addActionListener(e -> doLogin());
        passField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        form.add(loginTitle);
        form.add(Box.createVerticalStrut(4));
        form.add(loginSub);
        form.add(Box.createVerticalStrut(36));
        form.add(userLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(userField);
        form.add(Box.createVerticalStrut(16));
        form.add(passLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(passField);
        form.add(Box.createVerticalStrut(10));
        form.add(statusLabel);
        form.add(Box.createVerticalStrut(16));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(20));
        form.add(hint);

        right.add(form);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    private JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY.deriveFont(Font.BOLD));
        lbl.setForeground(UITheme.TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void doLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        statusLabel.setText("Signing in...");
        statusLabel.setForeground(UITheme.TEXT_MUTED);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return controller.login(user, pass);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        dispose(); // Close login window
                        String role = AuthService.getInstance().getCurrentRole();

                        // --- UPDATED LOGIC HERE ---
                        if ("ADMIN".equals(role)) {
                            // Open the Admin Dashboard
                            new com.minierp.ui.admin.AdminDashboardUI().setVisible(true);
                        } else if ("FACULTY".equals(role)) {
                            // Open the Faculty Dashboard
                            new com.minierp.ui.faculty.FacultyDashboardUI().setVisible(true);
                        } else {
                            // Open the Student Dashboard
                            new com.minierp.ui.student.StudentDashboardUI().setVisible(true);
                        }
                        // ---------------------------

                    } else {
                        statusLabel.setText("Invalid username or password.");
                        statusLabel.setForeground(UITheme.DANGER);
                        passField.setText("");
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}