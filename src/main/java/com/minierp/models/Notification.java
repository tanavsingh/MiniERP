package com.minierp.models;

public class Notification {
    private int id, sentBy, targetId;
    private String title, message, type, targetRole, createdAt;
    private boolean isRead;

    public Notification() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSentBy() { return sentBy; }
    public void setSentBy(int sentBy) { this.sentBy = sentBy; }
    public int getTargetId() { return targetId; }
    public void setTargetId(int targetId) { this.targetId = targetId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
