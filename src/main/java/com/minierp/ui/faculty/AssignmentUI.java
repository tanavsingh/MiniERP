package com.minierp.ui.faculty;

import com.minierp.ui.components.UITheme;
import com.minierp.services.AssignmentService;
import com.minierp.services.AuthService;
import com.minierp.models.Assignment;
import com.minierp.database.QueryExecutor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AssignmentUI extends JPanel {
    private final AssignmentService service = new AssignmentService();
    private JTable table;
    private DefaultTableModel model;

    private static final String[] COLS = {"ID", "Title", "Subject", "Due Date", "Max Marks", "Submissions"};

    public AssignmentUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JButton addBtn   = UITheme.makeAccentButton("+ New Assignment");
        JButton viewBtn  = UITheme.makeSuccessButton("📄 Submissions");
        JButton delBtn   = UITheme.makeDangerButton("🗑 Delete");
        JButton refreshBtn = UITheme.makeButton("↻ Refresh", UITheme.BG_CARD);

        addBtn.addActionListener(e -> showAssignmentDialog(null));
        viewBtn.addActionListener(e -> viewSubmissions());
        delBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadData());

        topBar.add(addBtn);
        topBar.add(viewBtn);
        topBar.add(delBtn);
        topBar.add(refreshBtn);

        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UITheme.makeTable(model);
        JScrollPane scroll = UITheme.makeScrollPane(table);

        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        int facultyId = getFacultyId();
        List<Assignment> assignments = facultyId > 0 ? service.getByFaculty(facultyId) : service.getAllAssignments();
        for (Assignment a : assignments) {
            List<Map<String, Object>> subs = service.getSubmissions(a.getId());
            model.addRow(new Object[]{a.getId(), a.getTitle(), a.getSubjectName(), a.getDueDate(), a.getMaxMarks(), subs.size() + " submitted"});
        }
    }

    private int getFacultyId() {
        var f = AuthService.getInstance().getCurrentFaculty();
        return f != null ? f.getId() : 0;
    }

    private void viewSubmissions() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an assignment first."); return; }
        int id = (int) model.getValueAt(row, 0);
        String title = (String) model.getValueAt(row, 1);
        showSubmissionsDialog(id, title);
    }

    private void showSubmissionsDialog(int assignmentId, String title) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Submissions - " + title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(700, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);

        String[] cols = {"Student ID", "Name", "Status", "Marks", "Feedback", "Submitted At"};
        DefaultTableModel subModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3 || c == 4; }
        };
        List<Map<String, Object>> subs = service.getSubmissions(assignmentId);
        for (Map<String, Object> sub : subs) {
            subModel.addRow(new Object[]{sub.get("sid"), sub.get("student_name"),
                sub.get("status"), sub.get("marks_obtained"), sub.get("feedback"), sub.get("submitted_at")});
        }
        JTable subTable = UITheme.makeTable(subModel);
        JScrollPane sp = UITheme.makeScrollPane(subTable);

        JButton gradeBtn = UITheme.makeAccentButton("Grade Selected");
        gradeBtn.addActionListener(e -> {
            int selRow = subTable.getSelectedRow();
            if (selRow < 0) { JOptionPane.showMessageDialog(dialog, "Select a submission."); return; }
            // Grade dialog
            JTextField marksField = UITheme.makeTextField();
            JTextArea feedbackArea = new JTextArea(4, 30);
            feedbackArea.setBackground(UITheme.BG_CARD);
            feedbackArea.setForeground(UITheme.TEXT_PRIMARY);
            feedbackArea.setFont(UITheme.FONT_BODY);

            JPanel gPanel = new JPanel(new BorderLayout(0, 8));
            gPanel.setBackground(UITheme.BG_DARK);
            gPanel.add(UITheme.makeLabel("Marks:", UITheme.FONT_BODY, UITheme.TEXT_MUTED), BorderLayout.NORTH);
            gPanel.add(marksField, BorderLayout.CENTER);

            int res = JOptionPane.showConfirmDialog(dialog, gPanel, "Enter Marks", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                // Get submission id from the list
                Map<String, Object> sub = subs.get(selRow);
                int subId = sub.get("id") != null ? ((Number)sub.get("id")).intValue() : 0;
                double marks = Double.parseDouble(marksField.getText().trim());
                service.gradeSubmission(subId, marks, "Graded");
                subModel.setValueAt(marks, selRow, 3);
                subModel.setValueAt("GRADED", selRow, 2);
            }
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(sp, BorderLayout.CENTER);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(UITheme.BG_DARK);
        footer.add(gradeBtn);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an assignment to delete."); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this assignment?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            service.deleteAssignment(id);
            loadData();
        }
    }

    private void showAssignmentDialog(Assignment existing) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            "New Assignment", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        List<Map<String, Object>> subjects = QueryExecutor.executeQuery("SELECT id, name FROM subjects");
        String[] subOptions = subjects.stream().map(r -> r.get("id") + ": " + r.get("name")).toArray(String[]::new);
        JComboBox<String> subCombo = UITheme.makeCombo(subOptions.length > 0 ? subOptions : new String[]{"No subjects"});

        JTextField titleField = UITheme.makeTextField();
        JTextArea descArea = new JTextArea(3, 30);
        descArea.setBackground(UITheme.BG_CARD); descArea.setForeground(UITheme.TEXT_PRIMARY); descArea.setFont(UITheme.FONT_BODY);
        JTextField dueDateField = UITheme.makeTextField();
        dueDateField.setText(com.minierp.utils.DateUtils.today());
        JTextField maxMarksField = UITheme.makeTextField();
        maxMarksField.setText("100");

        panel.add(UITheme.makeLabel("Subject*", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(subCombo);
        panel.add(UITheme.makeLabel("Title*", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(titleField);
        panel.add(UITheme.makeLabel("Description", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(new JScrollPane(descArea));
        panel.add(UITheme.makeLabel("Due Date (YYYY-MM-DD)", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(dueDateField);
        panel.add(UITheme.makeLabel("Max Marks", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(maxMarksField);

        JButton saveBtn = UITheme.makeAccentButton("Save Assignment");
        saveBtn.addActionListener(e -> {
            String selSub = (String) subCombo.getSelectedItem();
            if (selSub == null || titleField.getText().isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Title and Subject are required.");
                return;
            }
            int subId = Integer.parseInt(selSub.split(":")[0].trim());
            Assignment a = new Assignment();
            a.setSubjectId(subId);
            a.setFacultyId(getFacultyId() > 0 ? getFacultyId() : 1);
            a.setTitle(titleField.getText().trim());
            a.setDescription(descArea.getText().trim());
            a.setDueDate(dueDateField.getText().trim());
            try { a.setMaxMarks(Integer.parseInt(maxMarksField.getText().trim())); } catch (NumberFormatException ex) { a.setMaxMarks(100); }
            if (service.createAssignment(a)) {
                JOptionPane.showMessageDialog(dialog, "Assignment created!");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to create assignment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(UITheme.BG_DARK);
        footer.add(UITheme.makeButton("Cancel", UITheme.BG_CARD));
        footer.add(saveBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
