package com.minierp.ui.student;

import com.minierp.ui.components.UITheme;
import com.minierp.services.ResultService;
import com.minierp.services.AuthService;
import com.minierp.models.Result;
import com.minierp.models.Student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ResultViewUI extends JPanel {
    private final ResultService service = new ResultService();

    public ResultViewUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        Student student = AuthService.getInstance().getCurrentStudent();
        if (student == null) { add(UITheme.makeLabel("Not logged in.", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); return; }

        JLabel title = UITheme.makeLabel("My Academic Results", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);

        String[] cols = {"Exam", "Subject", "Type", "Marks", "Max Marks", "Percentage", "Grade"};
        List<Result> results = service.getByStudent(student.getId());
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Result r : results) {
            double pct = r.getMaxMarks() > 0 ? (r.getMarksObtained() / r.getMaxMarks()) * 100 : 0;
            model.addRow(new Object[]{r.getExamTitle(), r.getSubjectName(), "",
                r.getMarksObtained(), r.getMaxMarks(), String.format("%.1f%%", pct), r.getGrade()});
        }

        JTable table = UITheme.makeTable(model);
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                String grade = v != null ? v.toString() : "";
                setForeground(grade.startsWith("A") ? UITheme.SUCCESS :
                    "B".equals(grade) ? UITheme.WARNING :
                    "F".equals(grade) ? UITheme.DANGER : UITheme.TEXT_PRIMARY);
                return this;
            }
        });

        // CGPA display
        JLabel cgpaLabel = UITheme.makeLabel("Current CGPA: " + student.getCgpa(), UITheme.FONT_HEADING, UITheme.SUCCESS);
        cgpaLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        add(title, BorderLayout.NORTH);
        add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
        add(cgpaLabel, BorderLayout.SOUTH);
    }
}
