package com.minierp.ui.student;

import com.minierp.ui.components.UITheme;
import com.minierp.controllers.LostFoundController;
import com.minierp.services.AuthService;
import com.minierp.models.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class LostFoundUI extends JPanel {
    private final LostFoundController ctrl = new LostFoundController();
    private JTabbedPane tabs;

    public LostFoundUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);

        tabs.addTab("Lost Items Board", buildItemsPanel(true));
        tabs.addTab("Found Items Board", buildItemsPanel(false));
        tabs.addTab("Report Lost Item", buildReportPanel(true));
        tabs.addTab("Report Found Item", buildReportPanel(false));
        tabs.addTab("Claim an Item", buildClaimPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildItemsPanel(boolean isLost) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        String[] cols = {"Item", "Category", "Date", "Location", "Contact", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        List<?> items = isLost ? ctrl.getAllLost() : ctrl.getAllFound();
        for (Object obj : items) {
            if (isLost) {
                LostItem item = (LostItem) obj;
                model.addRow(new Object[]{item.getItemName(), item.getCategory(),
                    isLost ? item.getLostDate() : "", item.getLostLocation(), item.getContactInfo(), item.getStatus()});
            } else {
                FoundItem item = (FoundItem) obj;
                model.addRow(new Object[]{item.getItemName(), item.getCategory(),
                    item.getFoundDate(), item.getFoundLocation(), item.getContactInfo(), item.getStatus()});
            }
        }
        JTable table = UITheme.makeTable(model);
        panel.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReportPanel(boolean isLost) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.setBackground(UITheme.BG_DARK);
        form.setMaximumSize(new Dimension(500, 600));

        JTextField nameField = UITheme.makeTextField();
        JTextField catField  = UITheme.makeTextField(); catField.setText("Electronics/Bag/Documents/Accessories/Other");
        JTextArea descArea   = new JTextArea(3, 40);
        descArea.setBackground(UITheme.BG_CARD); descArea.setForeground(UITheme.TEXT_PRIMARY); descArea.setFont(UITheme.FONT_BODY);
        JTextField dateField = UITheme.makeTextField(); dateField.setText(com.minierp.utils.DateUtils.today());
        JTextField locField  = UITheme.makeTextField();
        JTextField contField = UITheme.makeTextField();

        String dateLabel = isLost ? "Date Lost" : "Date Found";
        String locLabel  = isLost ? "Location Lost" : "Location Found";

        form.add(UITheme.makeLabel("Item Name*", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(nameField);
        form.add(UITheme.makeLabel("Category", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(catField);
        form.add(UITheme.makeLabel("Description", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(new JScrollPane(descArea));
        form.add(UITheme.makeLabel(dateLabel + " (YYYY-MM-DD)", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(dateField);
        form.add(UITheme.makeLabel(locLabel, UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(locField);
        form.add(UITheme.makeLabel("Contact Info*", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(contField);

        JButton submitBtn = UITheme.makeAccentButton(isLost ? "Report Lost Item" : "Report Found Item");
        submitBtn.addActionListener(e -> {
            Student student = AuthService.getInstance().getCurrentStudent();
            if (student == null || nameField.getText().isBlank()) {
                JOptionPane.showMessageDialog(panel, "Item name and login required.");
                return;
            }
            if (isLost) {
                LostItem item = new LostItem();
                item.setReportedBy(student.getId());
                item.setItemName(nameField.getText().trim());
                item.setCategory(catField.getText().trim());
                item.setDescription(descArea.getText().trim());
                item.setLostDate(dateField.getText().trim());
                item.setLostLocation(locField.getText().trim());
                item.setContactInfo(contField.getText().trim());
                ctrl.reportLost(item);
            } else {
                FoundItem item = new FoundItem();
                item.setReportedBy(student.getId());
                item.setItemName(nameField.getText().trim());
                item.setCategory(catField.getText().trim());
                item.setDescription(descArea.getText().trim());
                item.setFoundDate(dateField.getText().trim());
                item.setFoundLocation(locField.getText().trim());
                item.setContactInfo(contField.getText().trim());
                ctrl.reportFound(item);
            }
            JOptionPane.showMessageDialog(panel, "Item reported successfully!");
            nameField.setText(""); catField.setText(""); descArea.setText(""); locField.setText(""); contField.setText("");
        });

        JPanel scrollable = new JPanel(new BorderLayout(0, 12));
        scrollable.setBackground(UITheme.BG_DARK);
        scrollable.add(form, BorderLayout.NORTH);
        scrollable.add(submitBtn, BorderLayout.CENTER);

        panel.add(UITheme.makeScrollPane(scrollable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildClaimPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.setBackground(UITheme.BG_DARK);

        List<LostItem> lostItems = ctrl.getActiveLost();
        List<FoundItem> foundItems = ctrl.getActiveFound();
        String[] lostOpts = lostItems.stream().map(i -> i.getId() + ": " + i.getItemName()).toArray(String[]::new);
        String[] foundOpts = foundItems.stream().map(i -> i.getId() + ": " + i.getItemName()).toArray(String[]::new);

        JComboBox<String> lostCombo = UITheme.makeCombo(lostOpts.length > 0 ? lostOpts : new String[]{"No lost items"});
        JComboBox<String> foundCombo = UITheme.makeCombo(foundOpts.length > 0 ? foundOpts : new String[]{"No found items"});
        JComboBox<String> typeCombo = UITheme.makeCombo(new String[]{"Claim Lost Item", "Claim Found Item for Owner"});
        JTextArea descArea = new JTextArea(3, 40);
        descArea.setBackground(UITheme.BG_CARD); descArea.setForeground(UITheme.TEXT_PRIMARY); descArea.setFont(UITheme.FONT_BODY);
        JTextArea proofArea = new JTextArea(2, 40);
        proofArea.setBackground(UITheme.BG_CARD); proofArea.setForeground(UITheme.TEXT_PRIMARY); proofArea.setFont(UITheme.FONT_BODY);

        form.add(UITheme.makeLabel("Claim Type", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(typeCombo);
        form.add(UITheme.makeLabel("Lost Item (if claiming your lost item)", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(lostCombo);
        form.add(UITheme.makeLabel("Found Item (if claiming a found item)", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(foundCombo);
        form.add(UITheme.makeLabel("Claim Description*", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(new JScrollPane(descArea));
        form.add(UITheme.makeLabel("Proof / Evidence", UITheme.FONT_BODY, UITheme.TEXT_MUTED)); form.add(new JScrollPane(proofArea));

        JButton submitBtn = UITheme.makeAccentButton("Submit Claim");
        submitBtn.addActionListener(e -> {
            Student student = AuthService.getInstance().getCurrentStudent();
            if (student == null || descArea.getText().isBlank()) {
                JOptionPane.showMessageDialog(panel, "Description required.");
                return;
            }
            ClaimRequest cr = new ClaimRequest();
            cr.setClaimedBy(student.getId());
            cr.setClaimDescription(descArea.getText().trim());
            cr.setProofDescription(proofArea.getText().trim());

            String lostSel = (String) lostCombo.getSelectedItem();
            String foundSel = (String) foundCombo.getSelectedItem();
            if (lostSel != null && !lostSel.startsWith("No")) {
                cr.setLostItemId(Integer.parseInt(lostSel.split(":")[0].trim()));
            }
            if (foundSel != null && !foundSel.startsWith("No")) {
                cr.setFoundItemId(Integer.parseInt(foundSel.split(":")[0].trim()));
            }
            ctrl.submitClaim(cr);
            JOptionPane.showMessageDialog(panel, "Claim submitted! Faculty will review it.");
            descArea.setText(""); proofArea.setText("");
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(submitBtn, BorderLayout.CENTER);
        return panel;
    }
}
