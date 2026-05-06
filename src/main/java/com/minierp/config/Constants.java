package com.minierp.config;

public class Constants {
    // Attendance
    public static final double MIN_ATTENDANCE_PERCENT = 75.0;
    public static final String ATTENDANCE_PRESENT = "PRESENT";
    public static final String ATTENDANCE_ABSENT = "ABSENT";
    public static final String ATTENDANCE_LATE = "LATE";

    // Assignment Status
    public static final String ASSIGNMENT_PENDING = "PENDING";
    public static final String ASSIGNMENT_SUBMITTED = "SUBMITTED";
    public static final String ASSIGNMENT_GRADED = "GRADED";

    // Exam Types
    public static final String EXAM_MID = "MID_TERM";
    public static final String EXAM_FINAL = "FINAL";
    public static final String EXAM_QUIZ = "QUIZ";
    public static final String EXAM_PRACTICAL = "PRACTICAL";

    // Lost & Found Status
    public static final String ITEM_LOST = "LOST";
    public static final String ITEM_FOUND = "FOUND";
    public static final String ITEM_CLAIMED = "CLAIMED";
    public static final String CLAIM_PENDING = "PENDING";
    public static final String CLAIM_APPROVED = "APPROVED";
    public static final String CLAIM_REJECTED = "REJECTED";

    // Grades
    public static final String GRADE_A_PLUS = "A+";
    public static final String GRADE_A = "A";
    public static final String GRADE_B = "B";
    public static final String GRADE_C = "C";
    public static final String GRADE_D = "D";
    public static final String GRADE_F = "F";

    // Notification Types
    public static final String NOTIF_INFO = "INFO";
    public static final String NOTIF_WARNING = "WARNING";
    public static final String NOTIF_URGENT = "URGENT";

    // Days of week
    public static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    // Time slots
    public static final String[] TIME_SLOTS = {
        "08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00",
        "12:00-13:00", "13:00-14:00", "14:00-15:00", "15:00-16:00",
        "16:00-17:00", "17:00-18:00"
    };

    private Constants() {}
}
