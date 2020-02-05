package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;
import android.widget.CompoundButton;

import androidx.databinding.BindingAdapter;

public class CompoundButtonBindingAdapter {

    public interface OnCompoundCheckedChangeListener {
        View.OnClickListener onCompoundCheckedChanged(View compoundButton, Boolean isChecked);
    }

    @BindingAdapter("onCompoundCheckedChanged")
    public static void setCompoundCheckedChangedListener(View view,
                                                         OnCompoundCheckedChangeListener listener) {
        if(listener == null) {
            view.setOnClickListener(null);
        } else {
            view.setOnClickListener(listener.onCompoundCheckedChanged(view, ((CompoundButton) view).isChecked()));
        }
    }
}