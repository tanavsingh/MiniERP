package com.minierp.ui.student;

import com.minierp.ui.components.UITheme;
import com.minierp.services.AssignmentService;
import com.minierp.services.AuthService;
import com.minierp.models.Assignment;
import com.minierp.models.Student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AssignmentViewUI extends JPanel {
    private final AssignmentService service = new AssignmentService();
    private JTable table;
    private DefaultTableModel model;

    private static final String[] COLS = {"Title", "Subject", "Due Date", "Max Marks", "Status", "Marks Obtained", "Feedback"};

    public AssignmentViewUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topBar.setOpaque(false);
        JButton submitBtn = UITheme.makeAccentButton("Submit Assignment");
        JButton refreshBtn = UITheme.makeButton("Refresh", UITheme.BG_CARD);
        submitBtn.addActionListener(e -> submitSelected());
        refreshBtn.addActionListener(e -> loadData());
        topBar.add(submitBtn); topBar.add(refreshBtn);

        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UITheme.makeTable(model);
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String status = v != null ? v.toString() : "";
                setForeground("GRADED".equals(status) ? UITheme.SUCCESS :
                    "SUBMITTED".equals(status) ? UITheme.WARNING : UITheme.TEXT_MUTED);
                return this;
            }
        });

        add(topBar, BorderLayout.NORTH);
        add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        Student student = AuthService.getInstance().getCurrentStudent();
        if (student == null) return;
        List<Assignment> list = service.getByStudentWithStatus(student.getId());
        for (Assignment a : list) {
            model.addRow(new Object[]{a.getTitle(), a.getSubjectName(), a.getDueDate(),
                a.getMaxMarks(), a.getStatus().isBlank() ? "PENDING" : a.getStatus(),
                a.getMarksObtained() > 0 ? a.getMarksObtained() : "-", a.getFeedback()});
        }
    }

    private void submitSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an assignment to submit."); return; }
        String status = (String) model.getValueAt(row, 4);
        if ("SUBMITTED".equals(status) || "GRADED".equals(status)) {
            JOptionPane.showMessageDialog(this, "Already submitted.");
            return;
        }

        Student student = AuthService.getInstance().getCurrentStudent();
        if (student == null) return;
        List<Assignment> list = service.getByStudentWithStatus(student.getId());
        if (row >= list.size()) return;
        Assignment a = list.get(row);

        JTextArea textArea = new JTextArea(8, 40);
        textArea.setBackground(UITheme.BG_CARD);
        textArea.setForeground(UITheme.TEXT_PRIMARY);
        textArea.setFont(UITheme.FONT_BODY);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(textArea);
        sp.setPreferredSize(new Dimension(500, 180));

        int res = JOptionPane.showConfirmDialog(this, sp, "Submit: " + a.getTitle(), JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            service.submitAssignment(a.getId(), student.getId(), textArea.getText());
            JOptionPane.showMessageDialog(this, "Assignment submitted successfully!");
            loadData();
        }
    }
}
