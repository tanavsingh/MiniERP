package com.minierp.services;

import com.minierp.dao.FacultyDAO;
import com.minierp.models.Faculty;
import java.util.List;

public class FacultyService {
    private final FacultyDAO dao = new FacultyDAO();

    public List<Faculty> getAllFaculty() { return dao.findAll(); }
    public Faculty getById(int id) { return dao.findById(id); }

    public boolean addFaculty(Faculty f, String username, String password) {
        return dao.insert(f, username, password) > 0;
    }

    public boolean updateFaculty(Faculty f) { return dao.update(f) > 0; }
    public int getTotalCount() { return dao.count(); }
}
