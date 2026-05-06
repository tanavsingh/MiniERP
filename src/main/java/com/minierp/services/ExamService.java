package com.minierp.services;

import com.minierp.dao.ExamDAO;
import com.minierp.models.Exam;
import java.util.List;

public class ExamService {
    private final ExamDAO dao = new ExamDAO();

    public List<Exam> getAllExams() { return dao.findAll(); }
    public Exam getById(int id) { return dao.findById(id); }
    public List<Exam> getUpcomingExams() { return dao.findUpcoming(); }

    public boolean createExam(Exam e) { return dao.insert(e) > 0; }
    public boolean updateExam(Exam e) { return dao.update(e) > 0; }
    public boolean deleteExam(int id) { return dao.delete(id) > 0; }
    public int getTotalCount() { return dao.count(); }
}
