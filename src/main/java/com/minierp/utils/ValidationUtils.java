package com.minierp.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
private static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9_\\.\\+\\-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z]{2,}$");
private static final Pattern PHONE = Pattern.compile("^[6-9][0-9]{9}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone == null || phone.isBlank() || PHONE.matcher(phone.trim()).matches();
    }

    public static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    public static boolean isValidMarks(double marks, int max) {
        return marks >= 0 && marks <= max;
    }

    private ValidationUtils() {}
}
