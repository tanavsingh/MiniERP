package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.LostItem;
import java.util.*;

public class LostItemDAO {

    public List<LostItem> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT l.*, s.name AS reporter_name FROM lost_items l " +
            "LEFT JOIN students s ON l.reported_by = s.id ORDER BY l.created_at DESC");
        return mapList(rows);
    }

    public LostItem findById(int id) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT l.*, s.name AS reporter_name FROM lost_items l " +
            "LEFT JOIN students s ON l.reported_by = s.id WHERE l.id=?", id);
        return rows.isEmpty() ? null : map(rows.get(0));
    }

    public List<LostItem> findActive() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT l.*, s.name AS reporter_name FROM lost_items l " +
            "LEFT JOIN students s ON l.reported_by = s.id WHERE l.status='LOST' ORDER BY l.created_at DESC");
        return mapList(rows);
    }

    public long insert(LostItem item) {
        return QueryExecutor.executeInsert(
            "INSERT INTO lost_items (reported_by, item_name, description, category, lost_date, lost_location, contact_info) VALUES (?,?,?,?,?,?,?)",
            item.getReportedBy(), item.getItemName(), item.getDescription(),
            item.getCategory(), item.getLostDate(), item.getLostLocation(), item.getContactInfo());
    }

    public int updateStatus(int id, String status) {
        return QueryExecutor.executeUpdate("UPDATE lost_items SET status=? WHERE id=?", status, id);
    }

    public int delete(int id) {
        return QueryExecutor.executeUpdate("DELETE FROM lost_items WHERE id=?", id);
    }

    private List<LostItem> mapList(List<Map<String, Object>> rows) {
        List<LostItem> list = new ArrayList<>();
        for (Map<String, Object> row : rows) list.add(map(row));
        return list;
    }

    private LostItem map(Map<String, Object> row) {
        LostItem item = new LostItem();
        item.setId(toInt(row.get("id")));
        item.setReportedBy(toInt(row.get("reported_by")));
        item.setItemName(str(row.get("item_name")));
        item.setDescription(str(row.get("description")));
        item.setCategory(str(row.get("category")));
        item.setLostDate(str(row.get("lost_date")));
        item.setLostLocation(str(row.get("lost_location")));
        item.setContactInfo(str(row.get("contact_info")));
        item.setStatus(str(row.get("status")));
        item.setCreatedAt(str(row.get("created_at")));
        item.setReporterName(str(row.get("reporter_name")));
        return item;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
