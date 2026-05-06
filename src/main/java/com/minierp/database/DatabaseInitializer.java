package com.minierp.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        System.out.println("[DB] Initializing database schema...");
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {
            createTables(stmt);
            seedDefaultData(stmt);
            System.out.println("[DB] Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[DB] Initialization error: " + e.getMessage());
            throw new RuntimeException("DB init failed", e);
        }
    }

    private static void createTables(Statement stmt) throws SQLException {
        // Users table (shared login)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('ADMIN','FACULTY','STUDENT')),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Students
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER REFERENCES users(id),
                student_id TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                phone TEXT,
                course TEXT,
                semester INTEGER DEFAULT 1,
                address TEXT,
                dob TEXT,
                gender TEXT,
                cgpa REAL DEFAULT 0.0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Faculty
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS faculty (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER REFERENCES users(id),
                faculty_id TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                phone TEXT,
                department TEXT,
                designation TEXT,
                specialization TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Courses
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS courses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                duration_years INTEGER DEFAULT 4,
                department TEXT,
                description TEXT
            )
        """);

        // Subjects
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS subjects (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                course_id INTEGER REFERENCES courses(id),
                faculty_id INTEGER REFERENCES faculty(id),
                semester INTEGER,
                credits INTEGER DEFAULT 3,
                type TEXT DEFAULT 'THEORY'
            )
        """);

        // Attendance
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS attendance (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER REFERENCES students(id),
                subject_id INTEGER REFERENCES subjects(id),
                date TEXT NOT NULL,
                status TEXT NOT NULL CHECK(status IN ('PRESENT','ABSENT','LATE')),
                marked_by INTEGER REFERENCES faculty(id),
                remarks TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Assignments
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS assignments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                subject_id INTEGER REFERENCES subjects(id),
                faculty_id INTEGER REFERENCES faculty(id),
                due_date TEXT,
                max_marks INTEGER DEFAULT 100,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Assignment submissions
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS assignment_submissions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                assignment_id INTEGER REFERENCES assignments(id),
                student_id INTEGER REFERENCES students(id),
                submission_text TEXT,
                file_path TEXT,
                submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                marks_obtained REAL,
                feedback TEXT,
                status TEXT DEFAULT 'SUBMITTED'
            )
        """);

        // Exams
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS exams (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                subject_id INTEGER REFERENCES subjects(id),
                exam_type TEXT NOT NULL,
                exam_date TEXT NOT NULL,
                start_time TEXT,
                end_time TEXT,
                venue TEXT,
                max_marks INTEGER DEFAULT 100,
                instructions TEXT,
                created_by INTEGER REFERENCES faculty(id),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Results
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER REFERENCES students(id),
                exam_id INTEGER REFERENCES exams(id),
                marks_obtained REAL DEFAULT 0,
                grade TEXT,
                remarks TEXT,
                entered_by INTEGER REFERENCES faculty(id),
                entered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(student_id, exam_id)
            )
        """);

        // Timetable
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS timetable (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id INTEGER REFERENCES subjects(id),
                faculty_id INTEGER REFERENCES faculty(id),
                day_of_week TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                room TEXT,
                semester INTEGER,
                course_id INTEGER REFERENCES courses(id)
            )
        """);

        // Notifications
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                message TEXT NOT NULL,
                type TEXT DEFAULT 'INFO',
                target_role TEXT DEFAULT 'ALL',
                target_id INTEGER,
                sent_by INTEGER REFERENCES users(id),
                is_read INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Lost Items
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS lost_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                reported_by INTEGER REFERENCES students(id),
                item_name TEXT NOT NULL,
                description TEXT,
                category TEXT,
                lost_date TEXT,
                lost_location TEXT,
                contact_info TEXT,
                image_path TEXT,
                status TEXT DEFAULT 'LOST',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Found Items
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS found_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                reported_by INTEGER REFERENCES students(id),
                item_name TEXT NOT NULL,
                description TEXT,
                category TEXT,
                found_date TEXT,
                found_location TEXT,
                contact_info TEXT,
                image_path TEXT,
                status TEXT DEFAULT 'FOUND',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Claim Requests
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS claim_requests (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                lost_item_id INTEGER REFERENCES lost_items(id),
                found_item_id INTEGER REFERENCES found_items(id),
                claimed_by INTEGER REFERENCES students(id),
                claim_description TEXT,
                proof_description TEXT,
                status TEXT DEFAULT 'PENDING',
                reviewed_by INTEGER REFERENCES faculty(id),
                reviewed_at TIMESTAMP,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }

    private static void seedDefaultData(Statement stmt) throws SQLException {
        // Check if already seeded
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        if (rs.getInt(1) > 0) return;

        // Default Admin/Faculty user
        stmt.execute("""
            INSERT INTO users (username, password, role) VALUES
            ('admin', 'admin123', 'ADMIN'),
            ('faculty1', 'faculty123', 'FACULTY'),
            ('student1', 'student123', 'STUDENT'),
            ('student2', 'student123', 'STUDENT'),
            ('student3', 'student123', 'STUDENT')
        """);

        // Courses
        stmt.execute("""
            INSERT INTO courses (code, name, duration_years, department) VALUES
            ('CSE', 'Computer Science & Engineering', 4, 'Engineering'),
            ('ECE', 'Electronics & Communication', 4, 'Engineering'),
            ('MBA', 'Master of Business Administration', 2, 'Management'),
            ('MCA', 'Master of Computer Applications', 3, 'Computer Science')
        """);

        // Faculty
        stmt.execute("""
            INSERT INTO faculty (user_id, faculty_id, name, email, phone, department, designation, specialization) VALUES
            (2, 'FAC001', 'Dr. Rajesh Kumar', 'rajesh@minierp.edu', '9876543210', 'Computer Science', 'Associate Professor', 'Algorithms & AI')
        """);

        // Students
        stmt.execute("""
            INSERT INTO students (user_id, student_id, name, email, phone, course, semester, cgpa) VALUES
            (3, 'STU001', 'Arjun Singh', 'arjun@student.edu', '9123456789', 'CSE', 3, 8.5),
            (4, 'STU002', 'Priya Sharma', 'priya@student.edu', '9234567890', 'CSE', 3, 9.1),
            (5, 'STU003', 'Rahul Verma', 'rahul@student.edu', '9345678901', 'ECE', 5, 7.8)
        """);

        // Subjects
        stmt.execute("""
            INSERT INTO subjects (code, name, course_id, faculty_id, semester, credits, type) VALUES
            ('CS301', 'Data Structures & Algorithms', 1, 1, 3, 4, 'THEORY'),
            ('CS302', 'Database Management Systems', 1, 1, 3, 4, 'THEORY'),
            ('CS303', 'Object Oriented Programming', 1, 1, 3, 3, 'THEORY'),
            ('CS304', 'Computer Networks', 1, 1, 3, 3, 'THEORY'),
            ('CS305', 'DSA Lab', 1, 1, 3, 2, 'LAB')
        """);

        // Timetable
        stmt.execute("""
            INSERT INTO timetable (subject_id, faculty_id, day_of_week, start_time, end_time, room, semester, course_id) VALUES
            (1, 1, 'Monday', '09:00', '10:00', 'Room 101', 3, 1),
            (2, 1, 'Monday', '10:00', '11:00', 'Room 102', 3, 1),
            (3, 1, 'Tuesday', '09:00', '10:00', 'Room 101', 3, 1),
            (4, 1, 'Wednesday', '11:00', '12:00', 'Room 103', 3, 1),
            (5, 1, 'Friday', '14:00', '16:00', 'Lab 01', 3, 1)
        """);

        // Sample notifications
        stmt.execute("""
            INSERT INTO notifications (title, message, type, target_role, sent_by) VALUES
            ('Welcome to Mini ERP', 'Welcome to the new college management system!', 'INFO', 'ALL', 1),
            ('Exam Schedule Released', 'Mid-term exam schedule has been published. Please check the exam section.', 'INFO', 'STUDENT', 2),
            ('Attendance Warning', 'Students with attendance below 75% will not be allowed to sit in exams.', 'WARNING', 'ALL', 2)
        """);

        // Sample exams
        stmt.execute("""
            INSERT INTO exams (title, subject_id, exam_type, exam_date, start_time, end_time, venue, max_marks, created_by) VALUES
            ('DSA Mid-Term Exam', 1, 'MID_TERM', '2024-03-15', '10:00', '12:00', 'Hall A', 50, 1),
            ('DBMS Mid-Term Exam', 2, 'MID_TERM', '2024-03-17', '10:00', '12:00', 'Hall B', 50, 1),
            ('OOP Final Exam', 3, 'FINAL', '2024-05-10', '09:00', '12:00', 'Hall A', 100, 1)
        """);

        // Sample lost items
        stmt.execute("""
            INSERT INTO lost_items (reported_by, item_name, description, category, lost_date, lost_location, contact_info) VALUES
            (1, 'Blue Backpack', 'Blue Nike backpack with laptop inside, has stickers on it', 'Bag', '2024-02-10', 'Library', 'arjun@student.edu'),
            (2, 'Scientific Calculator', 'Casio fx-991ES Plus calculator', 'Electronics', '2024-02-12', 'Canteen', 'priya@student.edu')
        """);

        // Sample found items
        stmt.execute("""
            INSERT INTO found_items (reported_by, item_name, description, category, found_date, found_location, contact_info) VALUES
            (3, 'Water Bottle', 'Steel water bottle, Tupperware brand, blue color', 'Accessories', '2024-02-11', 'Classroom 201', 'rahul@student.edu'),
            (1, 'ID Card', 'College ID card belonging to a student', 'Documents', '2024-02-13', 'Parking Lot', 'arjun@student.edu')
        """);

        System.out.println("[DB] Default demo data seeded.");
    }
}
