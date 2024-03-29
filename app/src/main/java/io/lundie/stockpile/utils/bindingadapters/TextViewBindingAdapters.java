package io.lundie.stockpile.utils.bindingadapters;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.MonthDay;
import org.threeten.bp.Year;

import io.lundie.stockpile.R;
import io.lundie.stockpile.features.targets.TargetsTrackerType;

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
                daysRemaining = (currentMonthLength - currentMonthDay.getDayOfMonth());
            } else if (targetFrequency == WEEKLY) {
                int currentDay = DayOfWeek.from(LocalDate.now()).getValue();

                if(currentDay > targetStartDay) {
                    daysRemaining = 7 - (currentDay - targetStartDay);
                } else {
                    daysRemaining = targetStartDay - currentDay;
                }
            }
            String text = Integer.toString(daysRemaining) + " " +
                    view.getResources().getString(R.string.days_remaining);
        view.setText(text);
    }

    @BindingAdapter({"targetGoal", "targetProgress", "targetType"})
    public static void setProgressText(TextView view, int targetGoal, int targetProgress, int targetType) {
        StringBuilder builder = new StringBuilder();
        builder.append(targetProgress);
        builder.append("/");
        builder.append(targetGoal);
        builder.append(" ");
        if(targetType == TargetsTrackerType.ITEMS) {
            builder.append(view.getResources().getString(R.string.items));
        } else if (targetType == TargetsTrackerType.CALORIES) {
            builder.append(view.getResources().getString(R.string.calories));
        }
        view.setText(builder);
    }
}
