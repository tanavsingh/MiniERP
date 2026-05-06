package com.minierp.ai;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Exam;
import com.minierp.config.Constants;
import java.time.LocalDate;
import java.util.*;

public class ExamSchedulerAI {

    public List<Exam> generateSchedule(int courseId, int semester, String examType, String startDate, int facultyId) {
        List<Map<String, Object>> subjects = QueryExecutor.executeQuery(
            "SELECT * FROM subjects WHERE course_id = ? AND semester = ?", courseId, semester);

        List<Exam> exams = new ArrayList<>();
        LocalDate date = LocalDate.parse(startDate);
        String[] venues = {"Hall A", "Hall B", "Hall C", "Exam Room 1", "Exam Room 2"};

        int i = 0;
        for (Map<String, Object> sub : subjects) {
            // Skip weekends
            while (date.getDayOfWeek().getValue() >= 6) date = date.plusDays(1);

            Exam e = new Exam();
            e.setSubjectId(((Number) sub.get("id")).intValue());
            e.setSubjectName(sub.get("name") != null ? sub.get("name").toString() : "");
            e.setTitle(sub.get("name") + " - " + examType.replace("_", " "));
            e.setExamType(examType);
            e.setExamDate(date.toString());
            e.setStartTime("10:00");
            e.setEndTime("12:00");
            e.setVenue(venues[i % venues.length]);
            e.setMaxMarks(Constants.EXAM_MID.equals(examType) ? 50 : 100);
            e.setInstructions("No electronic devices allowed. Bring college ID.");
            e.setCreatedBy(facultyId);
            exams.add(e);
            date = date.plusDays(1);
            i++;
        }
        return exams;
    }
}
