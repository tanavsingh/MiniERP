package com.minierp.ui.faculty;

import com.minierp.ui.components.UITheme;
import com.minierp.controllers.LostFoundController;
import com.minierp.models.*;
import com.minierp.services.AuthService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LostFoundManagementUI extends JPanel {
    private final LostFoundController ctrl = new LostFoundController();
    private JTabbedPane tabs;

    // Claims table
    private JTable claimsTable;
    private DefaultTableModel claimsModel;
    private static final String[] CLAIM_COLS = {"ID", "Item", "Claimed By", "Description", "Status", "Date"};

    // AI Matches table
    private JTable matchTable;
    private DefaultTableModel matchModel;
    private static final String[] MATCH_COLS = {"Lost Item", "Found Item", "Confidence", "Score"};

    public LostFoundManagementUI() {
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

        tabs.addTab("Claim Requests", buildClaimsPanel());
        tabs.addTab("AI Matches", buildAIMatchPanel());
        tabs.addTab("All Lost Items", buildAllLostPanel());
        tabs.addTab("All Found Items", buildAllFoundPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildClaimsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topBar.setOpaque(false);
        JButton approveBtn = UITheme.makeSuccessButton("Approve");
        JButton rejectBtn  = UITheme.makeDangerButton("Reject");
        JButton refreshBtn = UITheme.makeButton("Refresh", UITheme.BG_CARD);

        approveBtn.addActionListener(e -> processSelected(true));
        rejectBtn.addActionListener(e -> processSelected(false));
        refreshBtn.addActionListener(e -> loadClaims());

        topBar.add(approveBtn); topBar.add(rejectBtn); topBar.add(refreshBtn);

        claimsModel = new DefaultTableModel(CLAIM_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        claimsTable = UITheme.makeTable(claimsModel);
        loadClaims();

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(UITheme.makeScrollPane(claimsTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadClaims() {
        claimsModel.setRowCount(0);
        for (ClaimRequest cr : ctrl.getPendingClaims()) {
            claimsModel.addRow(new Object[]{cr.getId(), cr.getItemName(), cr.getClaimerName(),
                cr.getClaimDescription(), cr.getStatus(), cr.getCreatedAt()});
        }
    }

    private void processSelected(boolean approve) {
        int row = claimsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a claim first."); return; }
        int claimId = (int) claimsModel.getValueAt(row, 0);
        int reviewerId = AuthService.getInstance().getCurrentFaculty() != null ? AuthService.getInstance().getCurrentFaculty().getId() : 1;
        if (approve) {
            ctrl.approveClaim(claimId, reviewerId, 0, true);
            JOptionPane.showMessageDialog(this, "Claim approved!");
        } else {
            ctrl.rejectClaim(claimId, reviewerId);
            JOptionPane.showMessageDialog(this, "Claim rejected.");
        }
        loadClaims();
    }

    private JPanel buildAIMatchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JButton findBtn = UITheme.makeButton("Run AI Matching", UITheme.ACCENT2);
        findBtn.addActionListener(e -> runAIMatch());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);
        topBar.add(UITheme.makeLabel("AI uses text similarity to match lost items with found items.", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        topBar.add(findBtn);

        matchModel = new DefaultTableModel(MATCH_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        matchTable = UITheme.makeTable(matchModel);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(UITheme.makeScrollPane(matchTable), BorderLayout.CENTER);
        return panel;
    }

    private void runAIMatch() {
        matchModel.setRowCount(0);
        List<Map<String, Object>> matches = ctrl.findAIMatches();
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No strong matches found between lost and found items.");
            return;
        }
        for (Map<String, Object> m : matches) {
            LostItem lost = (LostItem) m.get("lost_item");
            FoundItem found = (FoundItem) m.get("found_item");
            double score = (double) m.get("score");
            matchModel.addRow(new Object[]{
                lost.getItemName() + " (" + lost.getCategory() + ")",
                found.getItemName() + " (" + found.getCategory() + ")",
                m.get("confidence"),
                String.format("%.0f%%", score * 100)
            });
        }
    }

    private JPanel buildAllLostPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        String[] cols = {"ID", "Item", "Category", "Date Lost", "Location", "Reported By", "Status"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (LostItem item : ctrl.getAllLost()) {
            m.addRow(new Object[]{item.getId(), item.getItemName(), item.getCategory(),
                item.getLostDate(), item.getLostLocation(), item.getReporterName(), item.getStatus()});
        }
        panel.add(UITheme.makeScrollPane(UITheme.makeTable(m)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAllFoundPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        String[] cols = {"ID", "Item", "Category", "Date Found", "Location", "Reported By", "Status"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (FoundItem item : ctrl.getAllFound()) {
            m.addRow(new Object[]{item.getId(), item.getItemName(), item.getCategory(),
                item.getFoundDate(), item.getFoundLocation(), item.getReporterName(), item.getStatus()});
        }
        panel.add(UITheme.makeScrollPane(UITheme.makeTable(m)), BorderLayout.CENTER);
        return panel;
    }
}
