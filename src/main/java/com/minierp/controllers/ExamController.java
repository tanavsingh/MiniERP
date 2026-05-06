package com.minierp.controllers;

import com.minierp.services.ExamService;
import com.minierp.services.ResultService;
import com.minierp.ai.ExamSchedulerAI;
import com.minierp.models.Exam;
import com.minierp.models.Result;
import java.util.List;

public class ExamController {
    private final ExamService examService = new ExamService();
    private final ResultService resultService = new ResultService();
    private final ExamSchedulerAI ai = new ExamSchedulerAI();

    public List<Exam> getAll() { return examService.getAllExams(); }
    public List<Exam> getUpcoming() { return examService.getUpcomingExams(); }
    public boolean create(Exam e) { return examService.createExam(e); }
    public boolean update(Exam e) { return examService.updateExam(e); }
    public boolean delete(int id) { return examService.deleteExam(id); }

    public List<Exam> generateSchedule(int courseId, int sem, String type, String startDate, int facultyId) {
        return ai.generateSchedule(courseId, sem, type, startDate, facultyId);
    }

    public void saveGeneratedExams(List<Exam> exams) {
        for (Exam e : exams) examService.createExam(e);
    }

    public List<Result> getResultsByExam(int examId) { return resultService.getByExam(examId); }
    public boolean addResult(Result r) { return resultService.addResult(r); }
    public int getExamCount() { return examService.getTotalCount(); }
}
