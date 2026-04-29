package com.example.demo.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类。
 */
public final class DateUtils {

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);

    private DateUtils() {
        // utility class
    }

    public static String formatDate(LocalDate date) {
        return formatDate(date, DEFAULT_DATE_PATTERN);
    }

    public static String formatDate(LocalDate date, String pattern) {
        validateNotNull(date, "date must not be null");
        return date.format(buildFormatter(pattern));
    }

    public static LocalDate parseDate(String text) {
        validateText(text);
        return LocalDate.parse(text, DEFAULT_DATE_FORMATTER);
    }

    public static LocalDate parseDate(String text, String pattern) {
        validateText(text);
        return LocalDate.parse(text, buildFormatter(pattern));
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATE_TIME_PATTERN);
    }

    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        validateNotNull(dateTime, "dateTime must not be null");
        return dateTime.format(buildFormatter(pattern));
    }

    public static LocalDateTime parseDateTime(String text) {
        validateText(text);
        return LocalDateTime.parse(text, DEFAULT_DATE_TIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String text, String pattern) {
        validateText(text);
        return LocalDateTime.parse(text, buildFormatter(pattern));
    }

    private static DateTimeFormatter buildFormatter(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("pattern must not be blank");
        }
        return DateTimeFormatter.ofPattern(pattern);
    }

    private static void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("text must not be blank");
        }
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
