package com.minierp.controllers;

import com.minierp.services.FacultyService;
import com.minierp.models.Faculty;
import java.util.List;

public class FacultyController {
    private final FacultyService service = new FacultyService();

    public List<Faculty> getAll() { return service.getAllFaculty(); }
    public Faculty getById(int id) { return service.getById(id); }
    public boolean add(Faculty f, String u, String p) { return service.addFaculty(f, u, p); }
    public boolean update(Faculty f) { return service.updateFaculty(f); }
    public int getCount() { return service.getTotalCount(); }
}
