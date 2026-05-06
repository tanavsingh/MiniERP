package com.minierp.services;

import com.minierp.dao.StudentDAO;
import com.minierp.models.Student;
import java.util.List;

public class StudentService {
    private final StudentDAO dao = new StudentDAO();

    public List<Student> getAllStudents() { return dao.findAll(); }
    public Student getById(int id) { return dao.findById(id); }
    public Student getByUserId(int userId) { return dao.findByUserId(userId); }

    public boolean addStudent(Student s, String username, String password) {
        if (s.getName().isBlank() || s.getEmail().isBlank()) return false;
        return dao.insert(s, username, password) > 0;
    }

    public boolean updateStudent(Student s) {
        return dao.update(s) > 0;
    }

    public boolean deleteStudent(int id) {
        return dao.delete(id) > 0;
    }

    public int getTotalCount() { return dao.count(); }

    public List<Student> getStudentsByCourse(String course, int semester) {
        return dao.findByCourse(course, semester);
    }
}
