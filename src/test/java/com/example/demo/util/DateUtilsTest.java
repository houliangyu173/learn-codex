package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

    @Test
    void shouldFormatDateTimeWithDefaultPattern() throws Exception {
        Class<?> dateUtilsClass = Class.forName("com.example.demo.util.DateUtils");
        Method formatDateTime = dateUtilsClass.getMethod("formatDateTime", LocalDateTime.class);

        String formatted = (String) formatDateTime.invoke(null, LocalDateTime.of(2026, 4, 29, 8, 30, 15));

        assertEquals("2026-04-29 08:30:15", formatted);
    }

    @Test
    void shouldParseDateWithDefaultPattern() throws Exception {
        Class<?> dateUtilsClass = Class.forName("com.example.demo.util.DateUtils");
        Method parseDate = dateUtilsClass.getMethod("parseDate", String.class);

        LocalDate parsed = (LocalDate) parseDate.invoke(null, "2026-04-29");

        assertEquals(LocalDate.of(2026, 4, 29), parsed);
    }

    @Test
    void shouldRejectBlankDateText() throws Exception {
        Class<?> dateUtilsClass = Class.forName("com.example.demo.util.DateUtils");
        Method parseDate = dateUtilsClass.getMethod("parseDate", String.class);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class,
                () -> parseDate.invoke(null, " "));

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }
}
