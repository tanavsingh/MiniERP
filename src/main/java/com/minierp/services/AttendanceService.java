package com.minierp.services;

import com.minierp.dao.AttendanceDAO;
import com.minierp.models.Attendance;
import com.minierp.config.Constants;
import java.util.*;

public class AttendanceService {
    private final AttendanceDAO dao = new AttendanceDAO();

    public List<Attendance> getBySubjectAndDate(int subjectId, String date) {
        return dao.findBySubjectAndDate(subjectId, date);
    }

    public List<Attendance> getByStudent(int studentId) {
        return dao.findByStudent(studentId);
    }

    public void markAttendance(List<Attendance> records) {
        dao.insertBatch(records);
    }

    public Map<String, double[]> getStudentAttendanceSummary(int studentId) {
        return dao.getAttendanceSummaryByStudent(studentId);
    }

    public List<Map<String, Object>> getLowAttendanceStudents() {
        return dao.getLowAttendanceStudents(Constants.MIN_ATTENDANCE_PERCENT);
    }

    public boolean isAttendanceLow(int studentId) {
        Map<String, double[]> summary = getStudentAttendanceSummary(studentId);
        for (double[] vals : summary.values()) {
            if (vals[2] < Constants.MIN_ATTENDANCE_PERCENT) return true;
        }
        return false;
    }
}
