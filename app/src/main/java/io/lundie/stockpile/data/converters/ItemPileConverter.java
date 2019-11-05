package io.lundie.stockpile.data.converters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemPileConverter {
    public static String itemDateToString(ArrayList<Date> values) {
        if (values != null) {
            Date date = values.get(0);
            //TODO: Update to local formatting
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        }
        // Following returned null value is replaced with a default string value in the XML layout
        // using null coalescing operator.
        return null;
    }
}
