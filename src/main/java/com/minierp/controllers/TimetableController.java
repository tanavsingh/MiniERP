package com.minierp.controllers;

import com.minierp.services.TimetableService;
import com.minierp.ai.TimetableGeneratorAI;
import com.minierp.models.Timetable;
import java.util.List;

public class TimetableController {
    private final TimetableService service = new TimetableService();
    private final TimetableGeneratorAI ai = new TimetableGeneratorAI();

    public List<Timetable> getAll() { return service.getAll(); }
    public List<Timetable> getBySemesterAndCourse(int semester, int courseId) {
        return service.getBySemesterAndCourse(semester, courseId);
    }
    public boolean add(Timetable t) { return service.addEntry(t); }
    public boolean delete(int id) { return service.deleteEntry(id); }

    public List<Timetable> generateWithAI(int courseId, int semester) {
        return ai.generate(courseId, semester);
    }

    public void saveGeneratedTimetable(List<Timetable> list) {
        for (Timetable t : list) service.addEntry(t);
    }
}
