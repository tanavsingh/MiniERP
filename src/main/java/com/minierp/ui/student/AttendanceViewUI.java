package com.minierp.ui.student;

import com.minierp.ui.components.UITheme;
import com.minierp.services.AttendanceService;
import com.minierp.services.AuthService;
import com.minierp.models.Student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class AttendanceViewUI extends JPanel {
    private final AttendanceService service = new AttendanceService();

    public AttendanceViewUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        Student student = AuthService.getInstance().getCurrentStudent();
        if (student == null) {
            add(UITheme.makeLabel("Not logged in as student.", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
            return;
        }

        Map<String, double[]> summary = service.getStudentAttendanceSummary(student.getId());

        // Summary cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, summary.size(), 12, 0));
        cardsPanel.setOpaque(false);
        for (Map.Entry<String, double[]> entry : summary.entrySet()) {
            double pct = entry.getValue()[2];
            Color color = pct >= 75 ? UITheme.SUCCESS : UITheme.DANGER;
            cardsPanel.add(new com.minierp.ui.components.DashboardCard(
                entry.getKey(),
                String.format("%.0f%%", pct),
                String.format("%.0f / %.0f classes", entry.getValue()[0], entry.getValue()[1]),
                color));
        }

        // Detail table
        String[] cols = {"Subject", "Date", "Status", "Remarks"};
        var records = service.getByStudent(student.getId());
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (var a : records) {
            model.addRow(new Object[]{a.getSubjectName(), a.getDate(), a.getStatus(), a.getRemarks()});
        }

        JTable table = UITheme.makeTable(model);
        // Color status column
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                String status = v != null ? v.toString() : "";
                setBackground(sel ? UITheme.BG_CARD :
                    "PRESENT".equals(status) ? new Color(0x0D2D1A) :
                    "ABSENT".equals(status) ? new Color(0x2D0D0D) : UITheme.BG_PANEL);
                setForeground("PRESENT".equals(status) ? UITheme.SUCCESS :
                    "ABSENT".equals(status) ? UITheme.DANGER : UITheme.WARNING);
                return this;
            }
        });

        JLabel summaryTitle = UITheme.makeLabel("Attendance Summary by Subject", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        JLabel detailTitle  = UITheme.makeLabel("Detailed Attendance Records", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(summaryTitle, BorderLayout.NORTH);
        top.add(cardsPanel.getComponentCount() > 0 ? cardsPanel :
            UITheme.makeLabel("No attendance records yet.", UITheme.FONT_BODY, UITheme.TEXT_MUTED), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(detailTitle, BorderLayout.NORTH);
        center.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }
}
