package com.minierp.ui.student;

import com.minierp.ui.components.UITheme;
import com.minierp.controllers.TimetableController;
import com.minierp.models.Timetable;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TimetableViewUI extends JPanel {
    private final TimetableController ctrl = new TimetableController();
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public TimetableViewUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JLabel title = UITheme.makeLabel("Weekly Class Timetable", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);

        List<Timetable> all = ctrl.getAll();
        String[] cols = {"Day", "Time", "Subject", "Faculty", "Room"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        all.sort((a, b) -> {
            int ai = Arrays.asList(DAYS).indexOf(a.getDayOfWeek());
            int bi = Arrays.asList(DAYS).indexOf(b.getDayOfWeek());
            return ai != bi ? ai - bi : a.getStartTime().compareTo(b.getStartTime());
        });
        for (Timetable t : all) {
            model.addRow(new Object[]{t.getDayOfWeek(), t.getStartTime() + " - " + t.getEndTime(),
                t.getSubjectName(), t.getFacultyName(), t.getRoom()});
        }

        JTable table = UITheme.makeTable(model);
        // Alternate day row coloring
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (sel) { setBackground(UITheme.BG_CARD); setForeground(UITheme.TEXT_PRIMARY); }
                else { setBackground(r % 2 == 0 ? UITheme.BG_PANEL : UITheme.ROW_ALT); setForeground(UITheme.TEXT_PRIMARY); }
                return this;
            }
        });

        add(title, BorderLayout.NORTH);
        add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
    }
}
