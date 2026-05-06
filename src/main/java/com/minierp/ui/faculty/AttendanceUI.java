package com.minierp.ui.faculty;

import com.minierp.ui.components.UITheme;
import com.minierp.services.AttendanceService;
import com.minierp.services.AuthService;
import com.minierp.ai.AttendanceAnalyzerAI;
import com.minierp.models.*;
import com.minierp.database.QueryExecutor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AttendanceUI extends JPanel {
    private final AttendanceService service = new AttendanceService();
    private final AttendanceAnalyzerAI ai = new AttendanceAnalyzerAI();
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> subjectCombo;
    private JTextField dateField;

    private final String[] COLS = {"Student ID", "Name", "Status", "Remarks"};

    public AttendanceUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        // Controls panel
        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        ctrlPanel.setOpaque(false);

        // Subject dropdown
        List<Map<String, Object>> subjects = QueryExecutor.executeQuery("SELECT id, name FROM subjects");
        String[] subNames = subjects.stream()
            .map(r -> r.get("id") + ": " + r.get("name"))
            .toArray(String[]::new);
        subjectCombo = UITheme.makeCombo(subNames.length > 0 ? subNames : new String[]{"No subjects"});
        subjectCombo.setPreferredSize(new Dimension(260, 36));

        dateField = UITheme.makeTextField();
        dateField.setPreferredSize(new Dimension(140, 36));
        dateField.setText(com.minierp.utils.DateUtils.today());

        JButton loadBtn = UITheme.makeAccentButton("Load Students");
        JButton markAllPresent = UITheme.makeSuccessButton("All Present");
        JButton markAllAbsent  = UITheme.makeButton("All Absent", UITheme.DANGER);
        JButton saveBtn  = UITheme.makeButton("💾 Save", UITheme.BG_CARD);
        JButton aiBtn    = UITheme.makeButton("🤖 AI Report", UITheme.ACCENT2);

        loadBtn.addActionListener(e -> loadStudents());
        markAllPresent.addActionListener(e -> setAllStatus("PRESENT"));
        markAllAbsent.addActionListener(e -> setAllStatus("ABSENT"));
        saveBtn.addActionListener(e -> saveAttendance());
        aiBtn.addActionListener(e -> showAIReport());

        ctrlPanel.add(UITheme.makeLabel("Subject:", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        ctrlPanel.add(subjectCombo);
        ctrlPanel.add(UITheme.makeLabel("Date:", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        ctrlPanel.add(dateField);
        ctrlPanel.add(loadBtn);
        ctrlPanel.add(markAllPresent);
        ctrlPanel.add(markAllAbsent);
        ctrlPanel.add(saveBtn);
        ctrlPanel.add(aiBtn);

        // Table with attendance status radio-like combo
        String[] statuses = {"PRESENT", "ABSENT", "LATE"};
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2 || c == 3; }
        };
        table = UITheme.makeTable(model);
        // Status column with combobox editor
        JComboBox<String> statusEditor = new JComboBox<>(statuses);
        statusEditor.setBackground(UITheme.BG_CARD);
        statusEditor.setForeground(UITheme.TEXT_PRIMARY);
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusEditor));

        JScrollPane scroll = UITheme.makeScrollPane(table);

        // Summary panel  
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setOpaque(false);
        JLabel summary = new JLabel("Load students to begin marking attendance.");
        summary.setFont(UITheme.FONT_SMALL);
        summary.setForeground(UITheme.TEXT_MUTED);
        summaryPanel.add(summary);

        add(ctrlPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private void loadStudents() {
        model.setRowCount(0);
        String selected = (String) subjectCombo.getSelectedItem();
        if (selected == null || selected.startsWith("No")) return;
        int subjectId = Integer.parseInt(selected.split(":")[0].trim());
        String date = dateField.getText().trim();

        // Get all students in the course this subject belongs to
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT s.id, s.student_id, s.name FROM students s ORDER BY s.name");
        List<Attendance> existing = service.getBySubjectAndDate(subjectId, date);
        Map<Integer, String> existingMap = new HashMap<>();
        for (Attendance a : existing) existingMap.put(a.getStudentId(), a.getStatus());

        for (Map<String, Object> row : rows) {
            int sid = ((Number) row.get("id")).intValue();
            String status = existingMap.getOrDefault(sid, "PRESENT");
            model.addRow(new Object[]{row.get("student_id"), row.get("name"), status, ""});
        }
    }

    private void setAllStatus(String status) {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        for (int i = 0; i < model.getRowCount(); i++) model.setValueAt(status, i, 2);
    }

    private void saveAttendance() {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        if (model.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "No students loaded."); return; }

        String selected = (String) subjectCombo.getSelectedItem();
        if (selected == null) return;
        int subjectId = Integer.parseInt(selected.split(":")[0].trim());
        String date = dateField.getText().trim();
        int facultyId = AuthService.getInstance().getCurrentFaculty() != null ?
            AuthService.getInstance().getCurrentFaculty().getId() : 1;

        List<Map<String, Object>> allStudents = QueryExecutor.executeQuery(
            "SELECT id, student_id FROM students ORDER BY name");

        List<Attendance> records = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String sid = (String) model.getValueAt(i, 0);
            String status = (String) model.getValueAt(i, 2);
            String remarks = model.getValueAt(i, 3) != null ? (String) model.getValueAt(i, 3) : "";

            for (Map<String, Object> row : allStudents) {
                if (sid.equals(row.get("student_id").toString())) {
                    Attendance a = new Attendance();
                    a.setStudentId(((Number) row.get("id")).intValue());
                    a.setSubjectId(subjectId);
                    a.setDate(date);
                    a.setStatus(status != null ? status : "PRESENT");
                    a.setMarkedBy(facultyId);
                    a.setRemarks(remarks);
                    records.add(a);
                    break;
                }
            }
        }
        service.markAttendance(records);
        JOptionPane.showMessageDialog(this, "Attendance saved for " + records.size() + " students!");
    }

    private void showAIReport() {
        List<Map<String, Object>> lowList = service.getLowAttendanceStudents();
        StringBuilder sb = new StringBuilder("<html><body style='font-family:Segoe UI;padding:10px'>");
        sb.append("<h2 style='color:#E94560'>AI Attendance Analysis</h2>");
        if (lowList.isEmpty()) {
            sb.append("<p>All students have attendance above threshold.</p>");
        } else {
            sb.append("<p><b>Students below 75% attendance:</b></p>");
            sb.append("<table border=1 cellpadding=5 style='border-collapse:collapse'>");
            sb.append("<tr><th>Student</th><th>Subject</th><th>Present</th><th>Total</th><th>%</th></tr>");
            for (Map<String, Object> row : lowList) {
                double pct = row.get("percentage") != null ? ((Number)row.get("percentage")).doubleValue() : 0;
                String color = pct < 50 ? "#FF4757" : "#FFC300";
                sb.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style='color:%s'>%.1f%%</td></tr>",
                    row.get("name"), row.get("subject_name"), row.get("present"), row.get("total"), color, pct));
            }
            sb.append("</table>");
        }
        sb.append("</body></html>");

        JEditorPane editor = new JEditorPane("text/html", sb.toString());
        editor.setEditable(false);
        editor.setBackground(UITheme.BG_DARK);
        JScrollPane sp = new JScrollPane(editor);
        sp.setPreferredSize(new Dimension(700, 450));
        JOptionPane.showMessageDialog(this, sp, "AI Attendance Report", JOptionPane.INFORMATION_MESSAGE);
    }
}
