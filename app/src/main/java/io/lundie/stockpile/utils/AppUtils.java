package io.lundie.stockpile.utils;

import org.threeten.bp.LocalDate;

import java.util.Calendar;


public class AppUtils {
    public static LocalDate calendarToLocalDate(Calendar calendar) {
        return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }
}
