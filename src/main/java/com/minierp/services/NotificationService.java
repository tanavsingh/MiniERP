package com.minierp.services;

import com.minierp.dao.NotificationDAO;
import com.minierp.models.Notification;
import java.util.List;

public class NotificationService {
    private final NotificationDAO dao = new NotificationDAO();

    public List<Notification> getAll() { return dao.findAll(); }
    public List<Notification> getByRole(String role) { return dao.findByRole(role); }
    public boolean send(Notification n) { return dao.insert(n) > 0; }
    public void markRead(int id) { dao.markRead(id); }
    public int getUnreadCount(String role) { return dao.countUnread(role); }
    public boolean delete(int id) { return dao.delete(id) > 0; }
}
