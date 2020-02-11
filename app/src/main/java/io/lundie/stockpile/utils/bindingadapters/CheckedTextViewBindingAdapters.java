package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;

import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import io.lundie.stockpile.data.model.CategoryCheckListItem;

public class CheckedTextViewBindingAdapters {

    @BindingAdapter("setTextViewIsChecked")
    public static void setTextViewChecked(AppCompatCheckedTextView view, Object object) {

    }

}
