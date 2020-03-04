package io.lundie.stockpile.utils.bindingadapters;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.MonthDay;
import org.threeten.bp.Year;

import static io.lundie.stockpile.features.targets.FrequencyTrackerType.MONTHLY;
import static io.lundie.stockpile.features.targets.FrequencyTrackerType.WEEKLY;

public class TextViewBindingAdapters {

    @BindingAdapter({"targetFrequency", "targetStart"})
    public static void setDaysRemaining(TextView view, int targetFrequency, int targetStartDay) {
        int daysRemaining = 0;
            if(targetFrequency == MONTHLY) {
                Month month = Month.from(LocalDate.now());
                boolean isLeapYear = false;
                if(month.equals(Month.FEBRUARY)) { isLeapYear = Year.now().isLeap(); }
                int currentMonthLength = month.length(isLeapYear);
                MonthDay currentMonthDay = MonthDay.from(LocalDate.now());
                daysRemaining = (currentMonthLength - currentMonthDay.getDayOfMonth()) +1;
            } else if (targetFrequency == WEEKLY) {
                int currentDay = DayOfWeek.from(LocalDate.now()).getValue();

                if(currentDay > targetStartDay) {
                    daysRemaining = 7 - (currentDay - targetStartDay);
                } else {
                    daysRemaining = targetStartDay - currentDay;
                }
            }
        view.setText(daysRemaining);
    }
}
