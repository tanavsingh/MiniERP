package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.FoundItem;
import java.util.*;

public class FoundItemDAO {

    public List<FoundItem> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT f.*, s.name AS reporter_name FROM found_items f " +
            "LEFT JOIN students s ON f.reported_by = s.id ORDER BY f.created_at DESC");
        return mapList(rows);
    }

    public FoundItem findById(int id) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT f.*, s.name AS reporter_name FROM found_items f " +
            "LEFT JOIN students s ON f.reported_by = s.id WHERE f.id=?", id);
        return rows.isEmpty() ? null : map(rows.get(0));
    }

    public List<FoundItem> findActive() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT f.*, s.name AS reporter_name FROM found_items f " +
            "LEFT JOIN students s ON f.reported_by = s.id WHERE f.status='FOUND' ORDER BY f.created_at DESC");
        return mapList(rows);
    }

    public long insert(FoundItem item) {
        return QueryExecutor.executeInsert(
            "INSERT INTO found_items (reported_by, item_name, description, category, found_date, found_location, contact_info) VALUES (?,?,?,?,?,?,?)",
            item.getReportedBy(), item.getItemName(), item.getDescription(),
            item.getCategory(), item.getFoundDate(), item.getFoundLocation(), item.getContactInfo());
    }

    public int updateStatus(int id, String status) {
        return QueryExecutor.executeUpdate("UPDATE found_items SET status=? WHERE id=?", status, id);
    }

    public int delete(int id) {
        return QueryExecutor.executeUpdate("DELETE FROM found_items WHERE id=?", id);
    }

    private List<FoundItem> mapList(List<Map<String, Object>> rows) {
        List<FoundItem> list = new ArrayList<>();
        for (Map<String, Object> row : rows) list.add(map(row));
        return list;
    }

    private FoundItem map(Map<String, Object> row) {
        FoundItem item = new FoundItem();
        item.setId(toInt(row.get("id")));
        item.setReportedBy(toInt(row.get("reported_by")));
        item.setItemName(str(row.get("item_name")));
        item.setDescription(str(row.get("description")));
        item.setCategory(str(row.get("category")));
        item.setFoundDate(str(row.get("found_date")));
        item.setFoundLocation(str(row.get("found_location")));
        item.setContactInfo(str(row.get("contact_info")));
        item.setStatus(str(row.get("status")));
        item.setCreatedAt(str(row.get("created_at")));
        item.setReporterName(str(row.get("reporter_name")));
        return item;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
