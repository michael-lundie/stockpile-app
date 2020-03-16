package io.lundie.stockpile.utils;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import io.lundie.stockpile.data.model.internal.ExpiryPile;
import timber.log.Timber;


public class DateUtils {

    private static DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    }

    public static Date getCurrentDate() {
        return localDateToDate(LocalDate.now());
    }

    public static int getDayOfWeek() {
        return DayOfWeek.from(LocalDate.now()).getValue();
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
        LocalDate localDate = dateToLocalDate(date);
        return localDate.format(getFormatter());
    }

    /**
     * Method from: https://www.baeldung.com/java-date-to-localdate-and-localdatetime
     * @param dateToConvert
     * @return
     */
    public static LocalDate dateToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date getDatePlusXMonths(int monthsToAdd) {
        return localDateToDate(LocalDate.now().plusMonths(monthsToAdd));
    }

    /**
     * Orders an ArrayList of date objects in an ascending format.
     * Since {@link java.util.Date} implements {@link Comparable} we can simple convert to an
     * object array and use the sort method and convert back.
     * @param dateArrayList ArrayList<Date> in any order
     * @return an ArrayList<Date> sorted in ascending order
     */
    public static ArrayList<Date> orderDateArrayListAscending(ArrayList<Date> dateArrayList) {

        // {@link java.utils.Date} implements comparable, so
        Date[] dateArray = new Date[dateArrayList.size()];
        for (int i = 0; i < dateArrayList.size(); i++) {
            dateArray[i] = dateArrayList.get(i);
        }
        Arrays.sort(dateArray);
        dateArrayList = new ArrayList<>();
        Collections.addAll(dateArrayList, dateArray);
        return dateArrayList;
    }

    /**
     * Converts an ArrayList of {@link ExpiryPile} into an ArrayList of {@link Date}.
     * @param expiryPileArrayList ArrayList<ExpiryPile>
     * @return ArrayList<Date>
     */
    public static ArrayList<Date> convertExpiryPilesToDates(ArrayList<ExpiryPile> expiryPileArrayList) {

        if(expiryPileArrayList != null && !expiryPileArrayList.isEmpty()) {
            ArrayList<Date> dateArrayList = new ArrayList<>();

            for (ExpiryPile expiryPile: expiryPileArrayList) {
                Date date = stringToDate(expiryPile.getExpiry());
                for (int i = 0; i < expiryPile.getItemCount(); i++) {
                    dateArrayList.add(date);
                }
            }
            return dateArrayList;
        }
        Timber.e("Warning: Value passed to convertExpiryPilesToDates method was null or clear.");
        return null;
    }

    /**
     * Converts an ArrayList of {@link Date} into an ArrayList of {@link ExpiryPile}.
     * @param dateList ArrayList<Date>
     * @return ArrayList<ExpiryPile>
     */
    public static ArrayList<ExpiryPile> convertDatesToExpiryPiles(ArrayList<Date> dateList) {

        if(dateList != null && !dateList.isEmpty()) {
            ArrayList<ExpiryPile> expiryPiles = new ArrayList<>();

            for (int i = 0; i < dateList.size() ; i++) {
                ExpiryPile expiryPile = new ExpiryPile();
                expiryPile.setExpiry(dateToString(dateList.get(i)));
                expiryPile.setItemCount(1);
                expiryPile.setItemId(i);
                expiryPiles.add(expiryPile);
            }
            return expiryPiles;
        }
        Timber.e("Warning: Value passed to convertDatesToExpiryPiles method was null or clear.");
        return null;
    }

    public static boolean isDateWithinRange(Date date) {
        LocalDate thresholdDate = dateToLocalDate(date).minusMonths(2);
        Date currentDate = getCurrentDate();
        return currentDate.compareTo(localDateToDate(thresholdDate)) > 0;
    }
}
