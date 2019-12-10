package io.lundie.stockpile.utils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import org.threeten.bp.DateTimeUtils;
import java.util.Calendar;
import java.util.Date;


public class AppUtils {
    public static LocalDate calendarToLocalDate(Calendar calendar) {
        return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Date localDatetoDate(LocalDate localDate) {
        return DateTimeUtils.toDate(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
