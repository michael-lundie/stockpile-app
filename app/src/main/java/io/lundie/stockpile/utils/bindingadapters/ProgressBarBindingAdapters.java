package io.lundie.stockpile.utils.bindingadapters;

import android.widget.ProgressBar;

import androidx.databinding.BindingAdapter;

public class ProgressBarBindingAdapters {

    @BindingAdapter({"targetGoal", "targetProgress"})
    public static void setTargetProgress(ProgressBar view, int targetGoal, int targetProgress) {

        if(targetProgress >= targetGoal) {
            view.setProgress(100);
        } else {
            view.setProgress((targetProgress * 100) / targetGoal);
        }
    }
}
