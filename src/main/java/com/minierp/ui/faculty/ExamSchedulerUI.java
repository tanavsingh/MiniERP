package com.minierp.ui.faculty;

import com.minierp.ui.components.UITheme;
import com.minierp.controllers.ExamController;
import com.minierp.services.AuthService;
import com.minierp.models.Exam;
import com.minierp.database.QueryExecutor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ExamSchedulerUI extends JPanel {
    private final ExamController ctrl = new ExamController();
    private JTable table;
    private DefaultTableModel model;
    private static final String[] COLS = {"ID", "Title", "Subject", "Type", "Date", "Time", "Venue", "Max Marks"};

    public ExamSchedulerUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topBar.setOpaque(false);

        JButton addBtn  = UITheme.makeAccentButton("+ Schedule Exam");
        JButton aiBtn   = UITheme.makeButton("AI Scheduler", UITheme.ACCENT2);
        JButton delBtn  = UITheme.makeDangerButton("Delete");
        JButton refreshBtn = UITheme.makeButton("Refresh", UITheme.BG_CARD);

        addBtn.addActionListener(e -> showExamDialog());
        aiBtn.addActionListener(e -> generateWithAI());
        delBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadData());

        topBar.add(addBtn); topBar.add(aiBtn); topBar.add(delBtn); topBar.add(refreshBtn);

        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UITheme.makeTable(model);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scroll = UITheme.makeScrollPane(table);

        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        for (Exam e : ctrl.getAll()) {
            model.addRow(new Object[]{e.getId(), e.getTitle(), e.getSubjectName(),
                e.getExamType(), e.getExamDate(), e.getStartTime()+"-"+e.getEndTime(), e.getVenue(), e.getMaxMarks()});
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete exam?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            ctrl.delete(id); loadData();
        }
    }

    private void generateWithAI() {
        List<Map<String, Object>> courses = QueryExecutor.executeQuery("SELECT id, name FROM courses");
        String[] courseOpts = courses.stream().map(r -> r.get("id") + ": " + r.get("name")).toArray(String[]::new);

        JComboBox<String> courseCombo = UITheme.makeCombo(courseOpts.length > 0 ? courseOpts : new String[]{"CSE: Computer Science"});
        JTextField semField = UITheme.makeTextField(); semField.setText("3");
        JComboBox<String> typeCombo = UITheme.makeCombo(new String[]{"MID_TERM", "FINAL", "QUIZ"});
        JTextField dateField = UITheme.makeTextField(); dateField.setText(com.minierp.utils.DateUtils.today());

        JPanel p = new JPanel(new GridLayout(0, 1, 0, 6));
        p.setBackground(UITheme.BG_DARK);
        p.add(UITheme.makeLabel("Course:", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); p.add(courseCombo);
        p.add(UITheme.makeLabel("Semester:", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); p.add(semField);
        p.add(UITheme.makeLabel("Exam Type:", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); p.add(typeCombo);
        p.add(UITheme.makeLabel("Start Date (YYYY-MM-DD):", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); p.add(dateField);

        if (JOptionPane.showConfirmDialog(this, p, "AI Exam Scheduler", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        int courseId = Integer.parseInt(((String)courseCombo.getSelectedItem()).split(":")[0].trim());
        int sem = Integer.parseInt(semField.getText().trim());
        String type = (String) typeCombo.getSelectedItem();
        String startDate = dateField.getText().trim();
        int facultyId = AuthService.getInstance().getCurrentFaculty() != null ? AuthService.getInstance().getCurrentFaculty().getId() : 1;

        List<Exam> generated = ctrl.generateSchedule(courseId, sem, type, startDate, facultyId);
        if (generated.isEmpty()) { JOptionPane.showMessageDialog(this, "No subjects found."); return; }

        if (JOptionPane.showConfirmDialog(this, "AI generated " + generated.size() + " exams. Save?", "AI Schedule", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            ctrl.saveGeneratedExams(generated);
            loadData();
            JOptionPane.showMessageDialog(this, "Exam schedule saved!");
        }
    }

    private void showExamDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Schedule Exam", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 480);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        List<Map<String, Object>> subjects = QueryExecutor.executeQuery("SELECT id, name FROM subjects");
        String[] subOpts = subjects.stream().map(r -> r.get("id") + ": " + r.get("name")).toArray(String[]::new);

        JTextField titleField = UITheme.makeTextField();
        JComboBox<String> subCombo = UITheme.makeCombo(subOpts.length > 0 ? subOpts : new String[]{"N/A"});
        JComboBox<String> typeCombo = UITheme.makeCombo(new String[]{"MID_TERM", "FINAL", "QUIZ", "PRACTICAL"});
        JTextField dateField = UITheme.makeTextField(); dateField.setText(com.minierp.utils.DateUtils.today());
        JTextField startField = UITheme.makeTextField(); startField.setText("10:00");
        JTextField endField   = UITheme.makeTextField(); endField.setText("12:00");
        JTextField venueField = UITheme.makeTextField(); venueField.setText("Hall A");
        JTextField marksField = UITheme.makeTextField(); marksField.setText("100");

        panel.add(UITheme.makeLabel("Title*", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(titleField);
        panel.add(UITheme.makeLabel("Subject", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(subCombo);
        panel.add(UITheme.makeLabel("Exam Type", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(typeCombo);
        panel.add(UITheme.makeLabel("Date (YYYY-MM-DD)", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(dateField);
        panel.add(UITheme.makeLabel("Start Time", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(startField);
        panel.add(UITheme.makeLabel("End Time", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(endField);
        panel.add(UITheme.makeLabel("Venue", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(venueField);
        panel.add(UITheme.makeLabel("Max Marks", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(marksField);

        JButton saveBtn = UITheme.makeAccentButton("Save Exam");
        saveBtn.addActionListener(e -> {
            if (titleField.getText().isBlank()) { JOptionPane.showMessageDialog(dialog, "Title required."); return; }
            Exam ex = new Exam();
            String ss = (String) subCombo.getSelectedItem();
            ex.setSubjectId(ss != null && !ss.equals("N/A") ? Integer.parseInt(ss.split(":")[0].trim()) : 0);
            ex.setTitle(titleField.getText().trim());
            ex.setExamType((String) typeCombo.getSelectedItem());
            ex.setExamDate(dateField.getText().trim());
            ex.setStartTime(startField.getText().trim());
            ex.setEndTime(endField.getText().trim());
            ex.setVenue(venueField.getText().trim());
            try { ex.setMaxMarks(Integer.parseInt(marksField.getText().trim())); } catch (Exception ee) { ex.setMaxMarks(100); }
            ex.setCreatedBy(AuthService.getInstance().getCurrentFaculty() != null ? AuthService.getInstance().getCurrentFaculty().getId() : 1);
            ctrl.create(ex);
            dialog.dispose();
            loadData();
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(UITheme.BG_DARK);
        footer.add(UITheme.makeButton("Cancel", UITheme.BG_CARD));
        footer.add(saveBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
