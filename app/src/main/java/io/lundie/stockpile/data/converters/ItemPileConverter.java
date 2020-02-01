package io.lundie.stockpile.data.converters;

import java.util.ArrayList;
import java.util.Date;

import io.lundie.stockpile.utils.DateUtils;

/**
 * A simple converter class used for importing into XML layouts where conversion of date objects
 * are required.
 */
public class ItemPileConverter {

    public static String itemDateToString(Date value) {
        if (value != null) {
            return DateUtils.dateToString(value);
        }
        // Following returned null value is replaced with a default string value in the XML layout
        return null;
    }

    public static String itemDateToString(ArrayList<Date> values) {
        if (values != null && values.size() > 0) {
            Date date = values.get(0);
            return DateUtils.dateToString(date);
        }
        // Following returned null value is replaced with a default string value in the XML layout
        return null;
    }
}