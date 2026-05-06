package com.minierp.services;

import com.minierp.dao.AssignmentDAO;
import com.minierp.models.Assignment;
import java.util.*;

public class AssignmentService {
    private final AssignmentDAO dao = new AssignmentDAO();

    public List<Assignment> getAllAssignments() { return dao.findAll(); }
    public List<Assignment> getByFaculty(int facultyId) { return dao.findByFaculty(facultyId); }
    public List<Assignment> getByStudentWithStatus(int studentId) { return dao.findByStudentWithStatus(studentId); }

    public boolean createAssignment(Assignment a) { return dao.insert(a) > 0; }
    public boolean updateAssignment(Assignment a) { return dao.update(a) > 0; }
    public boolean deleteAssignment(int id) { return dao.delete(id) > 0; }

    public boolean submitAssignment(int assignmentId, int studentId, String text) {
        return dao.submitAssignment(assignmentId, studentId, text) > 0;
    }

    public boolean gradeSubmission(int submissionId, double marks, String feedback) {
        return dao.gradeSubmission(submissionId, marks, feedback) > 0;
    }

    public List<Map<String, Object>> getSubmissions(int assignmentId) {
        return dao.getSubmissions(assignmentId);
    }
}
