package io.lundie.stockpile.utils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AppUtils {

    private static DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    }

    public static LocalDate calendarToLocalDate(Calendar calendar) {
        // Note that we are returning Calendar.MONTH + 1 as calender counts months from index 0.
        // ie: 0 = January, 1 = February, etc
        return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Date localDateToDate(LocalDate localDate) {
        return DateTimeUtils.toDate(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String calendarToString(Calendar calendar) {
        LocalDate localDate = calendarToLocalDate(calendar);
        return localDate.format(getFormatter());
    }

    public static Date stringToDate(String stringDate) {
        return localDateToDate(LocalDate.parse(stringDate, getFormatter()));
    }

    public static String dateToString(Date date) {
        LocalDate localDate = convertToLocalDateViaMillisecond(date);
        return localDate.format(getFormatter());
    }

    /**
     * Method from: https://www.baeldung.com/java-date-to-localdate-and-localdatetime
     * @param dateToConvert
     * @return
     */
    public static LocalDate convertToLocalDateViaMillisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static int generateEventId(int currentId) {
        int id = getRandomInteger(1, 1000);
        while(id == currentId) {
            id = getRandomInteger(1, 1000);
        } return id;
    }

    public static int getRandomInteger(int min, int max){
        return (int) (Math.random()*((max-min)+1))+min;
    }
}
