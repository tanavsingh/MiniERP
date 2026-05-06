package com.minierp.ui.student;

import com.minierp.ui.components.UITheme;
import com.minierp.controllers.ExamController;
import com.minierp.models.Exam;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ExamViewUI extends JPanel {
    private final ExamController ctrl = new ExamController();

    public ExamViewUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JLabel title = UITheme.makeLabel("Upcoming Exam Schedule", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);

        String[] cols = {"#", "Exam Title", "Subject", "Type", "Date", "Time", "Venue", "Max Marks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Exam> exams = ctrl.getAll();
        int i = 1;
        for (Exam e : exams) {
            model.addRow(new Object[]{i++, e.getTitle(), e.getSubjectName(), e.getExamType(),
                e.getExamDate(), e.getStartTime() + " - " + e.getEndTime(), e.getVenue(), e.getMaxMarks()});
        }

        JTable table = UITheme.makeTable(model);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        JLabel tip = new JLabel("Tip: No electronic devices allowed in exam hall. Bring your college ID card.");
        tip.setFont(UITheme.FONT_SMALL);
        tip.setForeground(UITheme.WARNING);
        tip.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        add(title, BorderLayout.NORTH);
        add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
        add(tip, BorderLayout.SOUTH);
    }
}
