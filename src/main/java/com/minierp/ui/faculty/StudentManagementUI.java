package com.minierp.ui.faculty;

import com.minierp.ui.components.UITheme;
import com.minierp.controllers.StudentController;
import com.minierp.models.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentManagementUI extends JPanel {
    private final StudentController ctrl = new StudentController();
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    private static final String[] COLS = {"ID", "Student ID", "Name", "Email", "Phone", "Course", "Semester", "CGPA"};

    public StudentManagementUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        searchField = UITheme.makeTextField();
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Search students...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton addBtn   = UITheme.makeAccentButton("+ Add Student");
        JButton editBtn  = UITheme.makeSuccessButton("✏ Edit");
        JButton delBtn   = UITheme.makeDangerButton("🗑 Delete");
        JButton refreshBtn = UITheme.makeButton("↻ Refresh", UITheme.BG_CARD);

        addBtn.addActionListener(e -> showStudentDialog(null));
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadData());

        btnPanel.add(searchField);
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(refreshBtn);

        topBar.add(btnPanel, BorderLayout.EAST);

        // Table
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UITheme.makeTable(model);
        JScrollPane scroll = UITheme.makeScrollPane(table);

        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Status bar
        JLabel status = new JLabel(" ");
        status.setFont(UITheme.FONT_SMALL);
        status.setForeground(UITheme.TEXT_MUTED);
        add(status, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);
        List<Student> students = ctrl.getAllStudents();
        for (Student s : students) {
            model.addRow(new Object[]{s.getId(), s.getStudentId(), s.getName(),
                s.getEmail(), s.getPhone(), s.getCourse(), s.getSemester(), s.getCgpa()});
        }
    }

    private void filterTable() {
        String query = searchField.getText().toLowerCase();
        model.setRowCount(0);
        for (Student s : ctrl.getAllStudents()) {
            if (s.getName().toLowerCase().contains(query) ||
                s.getStudentId().toLowerCase().contains(query) ||
                s.getEmail().toLowerCase().contains(query)) {
                model.addRow(new Object[]{s.getId(), s.getStudentId(), s.getName(),
                    s.getEmail(), s.getPhone(), s.getCourse(), s.getSemester(), s.getCgpa()});
            }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a student to edit."); return; }
        int id = (int) model.getValueAt(row, 0);
        Student s = ctrl.getById(id);
        if (s != null) showStudentDialog(s);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a student to delete."); return; }
        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete student: " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            ctrl.deleteStudent(id);
            loadData();
            JOptionPane.showMessageDialog(this, "Student deleted successfully.");
        }
    }

    private void showStudentDialog(Student existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Student" : "Add New Student", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 560);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        JTextField[] fields = new JTextField[8];
        String[] labels = {"Student ID*", "Full Name*", "Email*", "Phone", "Course*", "Semester*", "CGPA", "Address"};
        String[] values = isEdit ? new String[]{
            existing.getStudentId(), existing.getName(), existing.getEmail(),
            existing.getPhone(), existing.getCourse(), String.valueOf(existing.getSemester()),
            String.valueOf(existing.getCgpa()), existing.getAddress()
        } : new String[8];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i * 2; gbc.weightx = 0;
            panel.add(UITheme.makeLabel(labels[i], UITheme.FONT_BODY, UITheme.TEXT_MUTED), gbc);
            gbc.gridx = 0; gbc.gridy = i * 2 + 1; gbc.weightx = 1;
            fields[i] = UITheme.makeTextField();
            if (values[i] != null) fields[i].setText(values[i]);
            panel.add(fields[i], gbc);
        }

        JTextField userField = UITheme.makeTextField();
        JTextField passField = UITheme.makeTextField();
        if (!isEdit) {
            gbc.gridx = 0; gbc.gridy = labels.length * 2; gbc.weightx = 0;
            panel.add(UITheme.makeLabel("Username*", UITheme.FONT_BODY, UITheme.TEXT_MUTED), gbc);
            gbc.gridy++;
            panel.add(userField, gbc);
            gbc.gridy++;
            panel.add(UITheme.makeLabel("Password*", UITheme.FONT_BODY, UITheme.TEXT_MUTED), gbc);
            gbc.gridy++;
            panel.add(passField, gbc);
        }

        JButton saveBtn = UITheme.makeAccentButton(isEdit ? "Update" : "Save");
        JButton cancelBtn = UITheme.makeButton("Cancel", UITheme.BG_CARD);

        saveBtn.addActionListener(e -> {
            try {
                Student s = isEdit ? existing : new Student();
                s.setStudentId(fields[0].getText().trim());
                s.setName(fields[1].getText().trim());
                s.setEmail(fields[2].getText().trim());
                s.setPhone(fields[3].getText().trim());
                s.setCourse(fields[4].getText().trim());
                s.setSemester(Integer.parseInt(fields[5].getText().trim().isEmpty() ? "1" : fields[5].getText().trim()));
                s.setCgpa(Double.parseDouble(fields[6].getText().trim().isEmpty() ? "0" : fields[6].getText().trim()));
                s.setAddress(fields[7].getText().trim());

                boolean ok = isEdit ? ctrl.updateStudent(s)
                    : ctrl.addStudent(s, userField.getText().trim(), passField.getText().trim());
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Student " + (isEdit ? "updated" : "added") + " successfully!");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Operation failed. Check input data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format in Semester or CGPA.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
