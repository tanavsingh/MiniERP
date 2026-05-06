package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.ClaimRequest;
import java.util.*;

public class ClaimRequestDAO {

    public List<ClaimRequest> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT cr.*, s.name AS claimer_name, " +
            "COALESCE(l.item_name, f.item_name) AS item_name " +
            "FROM claim_requests cr LEFT JOIN students s ON cr.claimed_by = s.id " +
            "LEFT JOIN lost_items l ON cr.lost_item_id = l.id " +
            "LEFT JOIN found_items f ON cr.found_item_id = f.id " +
            "ORDER BY cr.created_at DESC");
        return mapList(rows);
    }

    public List<ClaimRequest> findPending() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT cr.*, s.name AS claimer_name, " +
            "COALESCE(l.item_name, f.item_name) AS item_name " +
            "FROM claim_requests cr LEFT JOIN students s ON cr.claimed_by = s.id " +
            "LEFT JOIN lost_items l ON cr.lost_item_id = l.id " +
            "LEFT JOIN found_items f ON cr.found_item_id = f.id " +
            "WHERE cr.status='PENDING' ORDER BY cr.created_at DESC");
        return mapList(rows);
    }

    public long insert(ClaimRequest cr) {
        return QueryExecutor.executeInsert(
            "INSERT INTO claim_requests (lost_item_id, found_item_id, claimed_by, claim_description, proof_description) VALUES (?,?,?,?,?)",
            cr.getLostItemId() > 0 ? cr.getLostItemId() : null,
            cr.getFoundItemId() > 0 ? cr.getFoundItemId() : null,
            cr.getClaimedBy(), cr.getClaimDescription(), cr.getProofDescription());
    }

    public int updateStatus(int id, String status, int reviewedBy) {
        return QueryExecutor.executeUpdate(
            "UPDATE claim_requests SET status=?, reviewed_by=?, reviewed_at=CURRENT_TIMESTAMP WHERE id=?",
            status, reviewedBy, id);
    }

    private List<ClaimRequest> mapList(List<Map<String, Object>> rows) {
        List<ClaimRequest> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            ClaimRequest cr = new ClaimRequest();
            cr.setId(toInt(row.get("id")));
            cr.setLostItemId(toInt(row.get("lost_item_id")));
            cr.setFoundItemId(toInt(row.get("found_item_id")));
            cr.setClaimedBy(toInt(row.get("claimed_by")));
            cr.setReviewedBy(toInt(row.get("reviewed_by")));
            cr.setClaimDescription(str(row.get("claim_description")));
            cr.setProofDescription(str(row.get("proof_description")));
            cr.setStatus(str(row.get("status")));
            cr.setCreatedAt(str(row.get("created_at")));
            cr.setClaimerName(str(row.get("claimer_name")));
            cr.setItemName(str(row.get("item_name")));
            list.add(cr);
        }
        return list;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
