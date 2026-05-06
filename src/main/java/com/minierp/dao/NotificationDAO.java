package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Notification;
import java.util.*;

public class NotificationDAO {

    public List<Notification> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT * FROM notifications ORDER BY created_at DESC");
        return mapList(rows);
    }

    public List<Notification> findByRole(String role) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT * FROM notifications WHERE target_role = ? OR target_role = 'ALL' ORDER BY created_at DESC", role);
        return mapList(rows);
    }

    public long insert(Notification n) {
        return QueryExecutor.executeInsert(
            "INSERT INTO notifications (title, message, type, target_role, target_id, sent_by) VALUES (?,?,?,?,?,?)",
            n.getTitle(), n.getMessage(), n.getType(), n.getTargetRole(), n.getTargetId(), n.getSentBy());
    }

    public int markRead(int id) {
        return QueryExecutor.executeUpdate("UPDATE notifications SET is_read = 1 WHERE id = ?", id);
    }

    public int delete(int id) {
        return QueryExecutor.executeUpdate("DELETE FROM notifications WHERE id=?", id);
    }

    public int countUnread(String role) {
        Object r = QueryExecutor.executeScalar(
            "SELECT COUNT(*) FROM notifications WHERE (target_role = ? OR target_role = 'ALL') AND is_read = 0", role);
        return r == null ? 0 : ((Number) r).intValue();
    }

    private List<Notification> mapList(List<Map<String, Object>> rows) {
        List<Notification> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Notification n = new Notification();
            n.setId(toInt(row.get("id")));
            n.setSentBy(toInt(row.get("sent_by")));
            n.setTargetId(toInt(row.get("target_id")));
            n.setTitle(str(row.get("title")));
            n.setMessage(str(row.get("message")));
            n.setType(str(row.get("type")));
            n.setTargetRole(str(row.get("target_role")));
            n.setCreatedAt(str(row.get("created_at")));
            Object isRead = row.get("is_read");
            n.setRead(isRead != null && ((Number)isRead).intValue() == 1);
            list.add(n);
        }
        return list;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
