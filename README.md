# Mini ERP - College Management System

A complete Java desktop ERP application for college management built with:
- **Java 11+** with **Swing UI**
- **FlatLaf** dark theme (modern look and feel)
- **SQLite** embedded database (no setup required)
- **AI features** for timetable generation, exam scheduling, attendance analysis

---

## Features

### Faculty / Admin
- Student Management (add, edit, delete, search)
- Attendance Marking with AI low-attendance analysis
- Assignment Management & Grading
- Timetable Manager with AI auto-generation
- Exam Scheduler with AI scheduling
- Result Entry & Grade calculation (A+/A/B/C/D/F)
- Lost & Found with AI item matching
- Notification Broadcasting

### Student
- Dashboard with attendance warnings
- View attendance percentage per subject
- Submit assignments, track submission status
- View timetable, exam schedule
- View results and grades
- Report lost/found items, submit claims
- AI study recommendations

---

## Build & Run

### Requirements
- Java 11 or higher
- Maven 3.6+

### Steps

```bash
# 1. Extract ZIP
unzip MiniERP-Project.zip
cd MiniERP

# 2. Build fat JAR
mvn clean package

# 3. Run
java -jar target/MiniERP.jar
```

---

## Demo Credentials

| Role    | Username   | Password     |
|---------|------------|--------------|
| Faculty | faculty1   | faculty123   |
| Student | student1   | student123   |
| Student | student2   | student123   |
| Student | student3   | student123   |
| Admin   | admin      | admin123     |

---

## Project Structure

```
MiniERP/
├── pom.xml
└── src/main/java/com/minierp/
    ├── main/          MainApp.java (entry point)
    ├── config/        AppConfig.java, Constants.java
    ├── database/      DBConnection, DatabaseInitializer, QueryExecutor
    ├── models/        13 model classes
    ├── dao/           11 DAO classes
    ├── services/      10 service classes
    ├── ai/            5 AI feature classes
    ├── controllers/   6 controller classes
    ├── ui/
    │   ├── LoginUI.java
    │   ├── components/   UITheme, Sidebar, Navbar, DashboardCard
    │   ├── faculty/      9 faculty UI panels
    │   └── student/      7 student UI panels
    ├── utils/         DateUtils, ValidationUtils, FileUtils, PDFGenerator
    └── reports/       AttendanceReport, ResultReport, StudentPerformanceReport
```

---

## Database

SQLite database is auto-created at `resources/database/erp.db` on first run.
Demo data is seeded automatically including courses, subjects, students, faculty.

---

## Notes

- On first run the database is created automatically — no manual setup needed
- The database file is stored next to the JAR: `resources/database/erp.db`
- FlatLaf requires Java 11+; tested with OpenJDK 11, 17, 21
=======
# MiniERP
>>>>>>> f34b075d07d2e9a8f8535080ebac6d42d167a52c
