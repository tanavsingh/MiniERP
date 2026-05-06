package com.minierp.ui.faculty;

import com.minierp.ui.components.UITheme;
import com.minierp.controllers.TimetableController;
import com.minierp.models.Timetable;
import com.minierp.database.QueryExecutor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TimetableUI extends JPanel {
    private final TimetableController ctrl = new TimetableController();
    private JTable table;
    private DefaultTableModel model;
    private static final String[] COLS = {"Day", "Start", "End", "Subject", "Faculty", "Room"};

    public TimetableUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topBar.setOpaque(false);

        JButton addBtn     = UITheme.makeAccentButton("+ Add Entry");
        JButton aiBtn      = UITheme.makeButton("AI Generate", UITheme.ACCENT2);
        JButton delBtn     = UITheme.makeDangerButton("Delete");
        JButton refreshBtn = UITheme.makeButton("Refresh", UITheme.BG_CARD);

        addBtn.addActionListener(e -> showAddDialog());
        aiBtn.addActionListener(e -> generateWithAI());
        delBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadData());

        topBar.add(addBtn); topBar.add(aiBtn); topBar.add(delBtn); topBar.add(refreshBtn);

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
        List<Timetable> entries = ctrl.getAll();
        String[] dayOrder = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        Map<String, Integer> dayIdx = new HashMap<>();
        for (int i = 0; i < dayOrder.length; i++) dayIdx.put(dayOrder[i], i);
        entries.sort((a, b) -> {
            int d = dayIdx.getOrDefault(a.getDayOfWeek(), 9) - dayIdx.getOrDefault(b.getDayOfWeek(), 9);
            return d != 0 ? d : a.getStartTime().compareTo(b.getStartTime());
        });
        for (Timetable t : entries) {
            model.addRow(new Object[]{t.getDayOfWeek(), t.getStartTime(), t.getEndTime(),
                t.getSubjectName(), t.getFacultyName(), t.getRoom()});
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an entry to delete."); return; }
        List<Timetable> all = ctrl.getAll();
        if (row < all.size()) {
            ctrl.delete(all.get(row).getId());
            loadData();
        }
    }

    private void generateWithAI() {
        List<Map<String, Object>> courses = QueryExecutor.executeQuery("SELECT id, name FROM courses");
        if (courses.isEmpty()) { JOptionPane.showMessageDialog(this, "No courses in database."); return; }
        String[] courseOpts = courses.stream().map(r -> r.get("id") + ": " + r.get("name")).toArray(String[]::new);
        JComboBox<String> courseCombo = UITheme.makeCombo(courseOpts);
        JTextField semField = UITheme.makeTextField(); semField.setText("3");

        JPanel p = new JPanel(new GridLayout(0, 1, 0, 6));
        p.setBackground(UITheme.BG_DARK);
        p.add(UITheme.makeLabel("Course:", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); p.add(courseCombo);
        p.add(UITheme.makeLabel("Semester:", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); p.add(semField);

        int res = JOptionPane.showConfirmDialog(this, p, "AI Timetable Generator", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String sel = (String) courseCombo.getSelectedItem();
        int courseId = Integer.parseInt(sel.split(":")[0].trim());
        int sem = Integer.parseInt(semField.getText().trim());

        List<Timetable> generated = ctrl.generateWithAI(courseId, sem);
        if (generated.isEmpty()) { JOptionPane.showMessageDialog(this, "No subjects found for this course/semester."); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
            "AI generated " + generated.size() + " timetable entries. Save them?",
            "AI Timetable", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ctrl.saveGeneratedTimetable(generated);
            loadData();
            JOptionPane.showMessageDialog(this, "Timetable saved successfully!");
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add Timetable Entry", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        List<Map<String, Object>> subjects = QueryExecutor.executeQuery("SELECT id, name FROM subjects");
        String[] subOpts = subjects.stream().map(r -> r.get("id") + ": " + r.get("name")).toArray(String[]::new);

        JComboBox<String> dayCombo = UITheme.makeCombo(com.minierp.config.Constants.DAYS);
        JComboBox<String> subCombo = UITheme.makeCombo(subOpts.length > 0 ? subOpts : new String[]{"No subjects"});
        JTextField startField = UITheme.makeTextField(); startField.setText("09:00");
        JTextField endField   = UITheme.makeTextField(); endField.setText("10:00");
        JTextField roomField  = UITheme.makeTextField(); roomField.setText("Room 101");

        panel.add(UITheme.makeLabel("Day", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(dayCombo);
        panel.add(UITheme.makeLabel("Subject", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(subCombo);
        panel.add(UITheme.makeLabel("Start Time (HH:MM)", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(startField);
        panel.add(UITheme.makeLabel("End Time (HH:MM)", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(endField);
        panel.add(UITheme.makeLabel("Room", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); panel.add(roomField);

        JButton saveBtn = UITheme.makeAccentButton("Add");
        saveBtn.addActionListener(e -> {
            String selSub = (String) subCombo.getSelectedItem();
            if (selSub == null || selSub.equals("No subjects")) return;
            int subId = Integer.parseInt(selSub.split(":")[0].trim());
            Timetable t = new Timetable();
            t.setSubjectId(subId);
            t.setFacultyId(1);
            t.setDayOfWeek((String) dayCombo.getSelectedItem());
            t.setStartTime(startField.getText().trim());
            t.setEndTime(endField.getText().trim());
            t.setRoom(roomField.getText().trim());
            t.setSemester(3);
            t.setCourseId(1);
            ctrl.add(t);
            dialog.dispose();
            loadData();
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
