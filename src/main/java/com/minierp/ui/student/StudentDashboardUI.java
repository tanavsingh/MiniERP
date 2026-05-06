package com.minierp.ui.student;

import com.minierp.ui.components.*;
import com.minierp.ui.LoginUI;
import com.minierp.services.*;
import com.minierp.models.*;
import com.minierp.config.AppConfig;

import javax.swing.*;
import java.awt.*;

public class StudentDashboardUI extends JFrame {
    private final AuthService auth = AuthService.getInstance();
    private final NotificationService notifService = new NotificationService();
    private final AttendanceService attendanceService = new AttendanceService();

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Navbar navbar;
    private Sidebar sidebar;

    public StudentDashboardUI() {
        setTitle(AppConfig.APP_NAME + " — Student Dashboard");
        setSize(AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));
        initUI();
        navigateTo("dashboard");
    }

    private void initUI() {
        Student student = auth.getCurrentStudent();
        String name = student != null ? student.getName() : auth.getCurrentUsername();

        sidebar = new Sidebar(name, "Student");
        sidebar.addSectionLabel("Overview");
        sidebar.addNavItem("📊", "Dashboard", "dashboard");
        sidebar.addSectionLabel("Academic");
        sidebar.addNavItem("✅", "My Attendance", "attendance");
        sidebar.addNavItem("📝", "Assignments", "assignments");
        sidebar.addNavItem("🗓", "Timetable", "timetable");
        sidebar.addNavItem("📋", "Exam Schedule", "exams");
        sidebar.addNavItem("🏆", "My Results", "results");
        sidebar.addSectionLabel("Campus");
        sidebar.addNavItem("🔔", "Notifications", "notifications");
        sidebar.addNavItem("🔍", "Lost & Found", "lostfound");
        sidebar.setOnNavigate(this::navigateTo);

        navbar = new Navbar("Student Dashboard",
            () -> notifService.getUnreadCount("STUDENT"));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.BG_DARK);

        contentPanel.add(buildDashboard(), "dashboard");
        contentPanel.add(new AttendanceViewUI(), "attendance");
        contentPanel.add(new AssignmentViewUI(), "assignments");
        contentPanel.add(new TimetableViewUI(), "timetable");
        contentPanel.add(new ExamViewUI(), "exams");
        contentPanel.add(new ResultViewUI(), "results");
        contentPanel.add(new LostFoundUI(), "lostfound");
        contentPanel.add(buildNotifPanel(), "notifications");

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG_DARK);
        main.add(navbar, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(main, BorderLayout.CENTER);
    }

    private void navigateTo(String key) {
        if ("logout".equals(key)) {
            int confirm = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                auth.logout();
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            }
            return;
        }
        sidebar.setSelected(key);
        navbar.setTitle(getTitleFor(key));
        cardLayout.show(contentPanel, key);
    }

    private String getTitleFor(String key) {
        return switch (key) {
            case "dashboard"     -> "My Dashboard";
            case "attendance"    -> "My Attendance";
            case "assignments"   -> "My Assignments";
            case "timetable"     -> "Class Timetable";
            case "exams"         -> "Exam Schedule";
            case "results"       -> "My Results";
            case "lostfound"     -> "Lost & Found";
            case "notifications" -> "Notifications";
            default              -> "Dashboard";
        };
    }

    private JPanel buildDashboard() {
        Student student = auth.getCurrentStudent();
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Welcome message
        String welcomeText = student != null ? "Welcome, " + student.getName() + "! | " + student.getCourse() + " Sem " + student.getSemester() : "Welcome!";
        JLabel welcome = UITheme.makeLabel(welcomeText, UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);

        // Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsPanel.setOpaque(false);

        int studentId = student != null ? student.getId() : 0;
        var attendanceSummary = attendanceService.getStudentAttendanceSummary(studentId);
        double avgAttendance = attendanceSummary.values().stream()
            .mapToDouble(v -> v[2]).average().orElse(0);

        String cgpa = student != null ? String.valueOf(student.getCgpa()) : "N/A";
        boolean lowAttendance = attendanceService.isAttendanceLow(studentId);

        cardsPanel.add(new DashboardCard("CGPA", cgpa, "Current", UITheme.SUCCESS));
        cardsPanel.add(new DashboardCard("Avg Attendance", String.format("%.0f%%", avgAttendance),
            lowAttendance ? "WARNING: Below 75%" : "Good standing", lowAttendance ? UITheme.DANGER : UITheme.WARNING));
        cardsPanel.add(new DashboardCard("Semester", student != null ? String.valueOf(student.getSemester()) : "N/A", "Current", UITheme.ACCENT));
        cardsPanel.add(new DashboardCard("Notifications", String.valueOf(notifService.getUnreadCount("STUDENT")), "Unread", UITheme.ACCENT2));

        // Quick links
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        quickPanel.setOpaque(false);

        JButton viewTTBtn = UITheme.makeButton("View Timetable", UITheme.BG_CARD);
        JButton viewExamBtn = UITheme.makeButton("Exam Schedule", UITheme.BG_CARD);
        JButton reportLostBtn = UITheme.makeAccentButton("Report Lost Item");
        JButton aiStudyBtn = UITheme.makeButton("AI Study Tips", UITheme.ACCENT2);

        viewTTBtn.addActionListener(e -> navigateTo("timetable"));
        viewExamBtn.addActionListener(e -> navigateTo("exams"));
        reportLostBtn.addActionListener(e -> navigateTo("lostfound"));
        aiStudyBtn.addActionListener(e -> showAIStudyTips(studentId));

        quickPanel.add(viewTTBtn);
        quickPanel.add(viewExamBtn);
        quickPanel.add(reportLostBtn);
        quickPanel.add(aiStudyBtn);

        // Attendance warning
        JPanel warningPanel = new JPanel(new BorderLayout());
        warningPanel.setOpaque(false);
        if (lowAttendance) {
            JLabel warning = new JLabel("⚠️  WARNING: Your attendance in one or more subjects is below 75%. You may be barred from exams!");
            warning.setFont(UITheme.FONT_BODY.deriveFont(Font.BOLD));
            warning.setForeground(UITheme.DANGER);
            warning.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.DANGER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            warning.setOpaque(true);
            warning.setBackground(new Color(0x2D1010));
            warningPanel.add(warning);
        }

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(cardsPanel);
        center.add(Box.createVerticalStrut(16));
        if (lowAttendance) { center.add(warningPanel); center.add(Box.createVerticalStrut(16)); }
        center.add(UITheme.makeLabel("Quick Actions", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY));
        center.add(Box.createVerticalStrut(8));
        center.add(quickPanel);

        panel.add(welcome, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildNotifPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        var notifs = notifService.getByRole("STUDENT");
        String[] cols = {"Title", "Type", "Message", "Date", "Read"};
        Object[][] data = new Object[notifs.size()][5];
        for (int i = 0; i < notifs.size(); i++) {
            var n = notifs.get(i);
            data[i] = new Object[]{n.getTitle(), n.getType(), n.getMessage(), n.getCreatedAt(), n.isRead() ? "Yes" : "No"};
        }
        var model = new javax.swing.table.DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = UITheme.makeTable(model);
        table.getColumnModel().getColumn(2).setPreferredWidth(350);
        panel.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void showAIStudyTips(int studentId) {
        var ai = new com.minierp.ai.StudyRecommendationAI();
        var tips = ai.getRecommendations(studentId);
        JTextArea area = new JTextArea(String.join("\n", tips));
        area.setEditable(false);
        area.setBackground(UITheme.BG_DARK);
        area.setForeground(UITheme.TEXT_PRIMARY);
        area.setFont(UITheme.FONT_BODY);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane sp = UITheme.makeScrollPane(area);
        sp.setPreferredSize(new Dimension(560, 400));
        JOptionPane.showMessageDialog(this, sp, "AI Study Recommendations", JOptionPane.INFORMATION_MESSAGE);
    }
}
