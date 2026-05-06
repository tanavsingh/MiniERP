package com.minierp.ui.faculty;

import com.minierp.ui.components.UITheme;
import com.minierp.services.NotificationService;
import com.minierp.services.AuthService;
import com.minierp.models.Notification;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class NotificationPanel extends JPanel {
    private final NotificationService service = new NotificationService();
    private JTable table;
    private DefaultTableModel model;

    private static final String[] COLS = {"ID", "Title", "Type", "Target", "Message", "Date", "Read"};

    public NotificationPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topBar.setOpaque(false);

        JButton sendBtn = UITheme.makeAccentButton("+ Send Notification");
        JButton markReadBtn = UITheme.makeSuccessButton("✓ Mark Read");
        JButton delBtn = UITheme.makeDangerButton("🗑 Delete");
        JButton refreshBtn = UITheme.makeButton("↻ Refresh", UITheme.BG_CARD);

        sendBtn.addActionListener(e -> showSendDialog());
        markReadBtn.addActionListener(e -> markRead());
        delBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadData());

        topBar.add(sendBtn);
        topBar.add(markReadBtn);
        topBar.add(delBtn);
        topBar.add(refreshBtn);

        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UITheme.makeTable(model);
        table.getColumnModel().getColumn(4).setPreferredWidth(300);

        add(topBar, BorderLayout.NORTH);
        add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        List<Notification> list = service.getAll();
        for (Notification n : list) {
            model.addRow(new Object[]{n.getId(), n.getTitle(), n.getType(), n.getTargetRole(),
                n.getMessage(), n.getCreatedAt(), n.isRead() ? "Yes" : "No"});
        }
    }

    private void markRead() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        service.markRead(id);
        loadData();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete notification?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            service.delete(id);
            loadData();
        }
    }

    private void showSendDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Send Notification", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 360);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JTextField titleField = UITheme.makeTextField();
        JTextArea msgArea = new JTextArea(4, 30);
        msgArea.setBackground(UITheme.BG_CARD); msgArea.setForeground(UITheme.TEXT_PRIMARY); msgArea.setFont(UITheme.FONT_BODY);
        JComboBox<String> typeCombo = UITheme.makeCombo(new String[]{"INFO", "WARNING", "URGENT"});
        JComboBox<String> targetCombo = UITheme.makeCombo(new String[]{"ALL", "STUDENT", "FACULTY"});

        panel.add(UITheme.makeLabel("Title*", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(titleField);
        panel.add(UITheme.makeLabel("Message*", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(new JScrollPane(msgArea));
        panel.add(UITheme.makeLabel("Type", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(typeCombo);
        panel.add(UITheme.makeLabel("Target", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        panel.add(targetCombo);

        JButton sendBtn = UITheme.makeAccentButton("Send");
        sendBtn.addActionListener(e -> {
            if (titleField.getText().isBlank() || msgArea.getText().isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Title and message are required.");
                return;
            }
            Notification n = new Notification();
            n.setTitle(titleField.getText().trim());
            n.setMessage(msgArea.getText().trim());
            n.setType((String) typeCombo.getSelectedItem());
            n.setTargetRole((String) targetCombo.getSelectedItem());
            n.setSentBy(AuthService.getInstance().getCurrentUserId());
            if (service.send(n)) {
                JOptionPane.showMessageDialog(dialog, "Notification sent!");
                dialog.dispose();
                loadData();
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(UITheme.BG_DARK);
        footer.add(UITheme.makeButton("Cancel", UITheme.BG_CARD));
        footer.add(sendBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
