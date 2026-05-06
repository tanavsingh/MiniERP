package com.minierp.ui.admin;

import com.minierp.ui.components.UITheme;
import com.minierp.dao.FacultyDAO;
import com.minierp.models.Faculty;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherManagementUI extends JPanel {
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private JTable table;
    private DefaultTableModel model;

    public TeacherManagementUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Header
        JLabel title = new JLabel("Faculty Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JButton addBtn = UITheme.makeAccentButton("+ Add New Faculty");
        addBtn.addActionListener(e -> showAddFacultyDialog());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(addBtn, BorderLayout.EAST);

        // Table
        String[] columns = {"ID", "Name", "Department", "Email", "Role"};
        model = new DefaultTableModel(columns, 0);
        table = UITheme.makeTable(model);

        loadFacultyData(); // Load existing teachers from DB

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadFacultyData() {
        model.setRowCount(0);
        List<Faculty> faculties = facultyDAO.findAll(); // Fetches from your DB
        for (Faculty f : faculties) {
            model.addRow(new Object[]{f.getId(), f.getName(), f.getDepartment(), f.getEmail(), "FACULTY"});
        }
    }

    private void showAddFacultyDialog() {
        JTextField nameF = UITheme.makeTextField();
        JTextField deptF = UITheme.makeTextField();
        JTextField emailF = UITheme.makeTextField();
        JPasswordField passF = new JPasswordField(); // Password for the new teacher

        Object[] fields = {
                "Full Name:", nameF,
                "Department:", deptF,
                "Email Address:", emailF,
                "Login Password:", passF
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Register New Faculty", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Faculty f = new Faculty();
            f.setName(nameF.getText());
            f.setDepartment(deptF.getText());
            f.setEmail(emailF.getText());
            f.setPassword(new String(passF.getPassword()));

            if (facultyDAO.save(f)) { // Saves to erp.db
                JOptionPane.showMessageDialog(this, "Faculty Added Successfully!");
                loadFacultyData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not save faculty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
