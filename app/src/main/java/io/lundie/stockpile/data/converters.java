package io.lundie.stockpile.data;

import java.util.ArrayList;
import java.util.Date;

public class converters {
    public static class ItemPileConverters {
        public static String itemDateToString(ArrayList<Date> values) {
            if(values != null) {
                Date date = values.get(0);
                return date.toString();
            } return null;
        }
    }
}
