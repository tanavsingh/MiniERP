package com.minierp.services;

import com.minierp.dao.TimetableDAO;
import com.minierp.models.Timetable;
import java.util.List;

public class TimetableService {
    private final TimetableDAO dao = new TimetableDAO();

    public List<Timetable> getAll() { return dao.findAll(); }
    public List<Timetable> getBySemesterAndCourse(int semester, int courseId) {
        return dao.findBySemesterAndCourse(semester, courseId);
    }
    public boolean addEntry(Timetable t) { return dao.insert(t) > 0; }
    public boolean deleteEntry(int id) { return dao.delete(id) > 0; }
    public void clearAll() { dao.deleteAll(); }
}
