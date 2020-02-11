package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;
import android.widget.CompoundButton;

import androidx.databinding.BindingAdapter;

public class CompoundButtonBindingAdapter {

    public interface OnCompoundCheckedChangeListener {
        CompoundButton.OnCheckedChangeListener onCompoundCheckedChanged(View compoundButton, Boolean isChecked);
    }

    @BindingAdapter("onCompoundCheckedChanged")
    public static void setCompoundCheckedChangedListener(View view,
                                                         OnCompoundCheckedChangeListener listener) {
        if(listener == null) {
            ((CompoundButton) view).setOnCheckedChangeListener(null);
        } else {
            ((CompoundButton) view).setOnCheckedChangeListener(
                    listener.onCompoundCheckedChanged(view, ((CompoundButton) view).isChecked()));
        }
    }
}