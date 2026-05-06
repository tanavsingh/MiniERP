package com.minierp.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static String today() { return LocalDate.now().format(DATE_FMT); }
    public static String now() { return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }

    public static String formatDisplay(String dbDate) {
        try { return LocalDate.parse(dbDate, DATE_FMT).format(DISPLAY_FMT); }
        catch (Exception e) { return dbDate; }
    }

    public static boolean isPast(String date) {
        try { return LocalDate.parse(date, DATE_FMT).isBefore(LocalDate.now()); }
        catch (Exception e) { return false; }
    }

    private DateUtils() {}
}
