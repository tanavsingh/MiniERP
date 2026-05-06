package com.minierp.controllers;

import com.minierp.services.StudentService;
import com.minierp.models.Student;
import java.util.List;

public class StudentController {
    private final StudentService service = new StudentService();

    public List<Student> getAllStudents() { return service.getAllStudents(); }
    public Student getById(int id) { return service.getById(id); }
    public boolean addStudent(Student s, String username, String password) {
        return service.addStudent(s, username, password);
    }
    public boolean updateStudent(Student s) { return service.updateStudent(s); }
    public boolean deleteStudent(int id) { return service.deleteStudent(id); }
    public int getTotalCount() { return service.getTotalCount(); }
}
