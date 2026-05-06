package com.minierp.services;

import com.minierp.dao.ResultDAO;
import com.minierp.models.Result;
import com.minierp.config.Constants;
import java.util.List;

public class ResultService {
    private final ResultDAO dao = new ResultDAO();

    public List<Result> getAllResults() { return dao.findAll(); }
    public List<Result> getByStudent(int studentId) { return dao.findByStudent(studentId); }
    public List<Result> getByExam(int examId) { return dao.findByExam(examId); }

    public boolean addResult(Result r) {
        r.setGrade(calculateGrade(r.getMarksObtained(), r.getMaxMarks()));
        return dao.insert(r) > 0;
    }

    public boolean updateResult(Result r) {
        r.setGrade(calculateGrade(r.getMarksObtained(), r.getMaxMarks()));
        return dao.update(r) > 0;
    }

    public boolean deleteResult(int id) { return dao.delete(id) > 0; }

    public String calculateGrade(double marks, int maxMarks) {
        if (maxMarks <= 0) return Constants.GRADE_F;
        double pct = (marks / maxMarks) * 100;
        if (pct >= 90) return Constants.GRADE_A_PLUS;
        if (pct >= 80) return Constants.GRADE_A;
        if (pct >= 70) return Constants.GRADE_B;
        if (pct >= 60) return Constants.GRADE_C;
        if (pct >= 50) return Constants.GRADE_D;
        return Constants.GRADE_F;
    }
}
