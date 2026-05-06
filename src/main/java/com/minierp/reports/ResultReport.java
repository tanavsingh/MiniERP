package com.minierp.reports;

import java.util.ArrayList;
import java.util.List;

import com.minierp.models.Result;
import com.minierp.services.ResultService;
import com.minierp.utils.DateUtils;
import com.minierp.utils.PDFGenerator;

public class ResultReport {
    private final ResultService service = new ResultService();

    public void generateStudentReport(int studentId, String studentName, String outputPath) {
        List<Result> results = service.getByStudent(studentId);
        List<String[]> data = new ArrayList<>();
        for (Result r : results) {
            double pct = r.getMaxMarks() > 0 ? (r.getMarksObtained() / r.getMaxMarks()) * 100 : 0;
            data.add(new String[]{
                r.getExamTitle(),
                r.getSubjectName(),
                String.valueOf(r.getMarksObtained()),
                String.valueOf(r.getMaxMarks()),
                String.format("%.1f%%", pct),
                r.getGrade()
            });
        }
String[] headersArr = {"Exam", "Subject", "Marks", "Max", "Percentage", "Grade"}; List<String[]> headers = new java.util.ArrayList<>(); headers.add(headersArr);
        String title = "Result Report - " + studentName + " | " + DateUtils.today();
        PDFGenerator.generateReport(title, headers, data, outputPath);
    }

    public void generateExamReport(int examId, String examTitle, String outputPath) {
        List<Result> results = service.getByExam(examId);
        List<String[]> data = new ArrayList<>();
        double total = 0;
        for (Result r : results) {
            total += r.getMarksObtained();
            double pct = r.getMaxMarks() > 0 ? (r.getMarksObtained() / r.getMaxMarks()) * 100 : 0;
            data.add(new String[]{r.getStudentName(), String.valueOf(r.getMarksObtained()),
                String.format("%.1f%%", pct), r.getGrade()});
        }
        if (!results.isEmpty()) {
            data.add(new String[]{"--- CLASS AVG ---", String.format("%.1f", total / results.size()), "", ""});
        }
String[] headersArr2 = {"Student", "Marks", "Percentage", "Grade"}; List<String[]> headers = new java.util.ArrayList<>(); headers.add(headersArr2);
        String title = "Exam Results - " + examTitle + " | " + DateUtils.today();
        PDFGenerator.generateReport(title, headers, data, outputPath);
    }
}
