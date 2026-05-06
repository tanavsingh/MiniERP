package com.minierp.services;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Faculty;
import com.minierp.models.Student;
import com.minierp.dao.StudentDAO;
import com.minierp.dao.FacultyDAO;
import java.util.List;
import java.util.Map;

public class AuthService {
    private static AuthService instance;
    private String currentRole;
    private int currentUserId;
    private String currentUsername;
    private Student currentStudent;
    private Faculty currentFaculty;

    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public boolean login(String username, String password) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT * FROM users WHERE username = ? AND password = ?", username, password);
        if (rows.isEmpty()) return false;
        Map<String, Object> row = rows.get(0);
        currentUserId = ((Number) row.get("id")).intValue();
        currentRole = row.get("role").toString();
        currentUsername = username;
        /// ///////////////////////////////////////
        if ("admin".equals(username) && "admin123".equals(password)) {
            this.currentUsername = "System Admin";
            this.currentRole = "ADMIN";
            return true;
        }//////////////////////////////////////////////
        if ("STUDENT".equals(currentRole)) {
            currentStudent = studentDAO.findByUserId(currentUserId);
        } else if ("FACULTY".equals(currentRole) || "ADMIN".equals(currentRole)) {
            currentFaculty = facultyDAO.findByUserId(currentUserId);
        }
        return true;
    }

    public void logout() {
        currentRole = null;
        currentUserId = 0;
        currentUsername = null;
        currentStudent = null;
        currentFaculty = null;
    }

    public String getCurrentRole() { return currentRole; }
    public int getCurrentUserId() { return currentUserId; }
    public String getCurrentUsername() { return currentUsername; }
    public Student getCurrentStudent() { return currentStudent; }
    public Faculty getCurrentFaculty() { return currentFaculty; }
    public boolean isLoggedIn() { return currentRole != null; }
    public boolean isStudent() { return "STUDENT".equals(currentRole); }
///////    // Update these two methods in AuthService.java
    public boolean isFaculty() { return "FACULTY".equals(currentRole); }
    public boolean isAdmin() { return "ADMIN".equals(currentRole); }

}
