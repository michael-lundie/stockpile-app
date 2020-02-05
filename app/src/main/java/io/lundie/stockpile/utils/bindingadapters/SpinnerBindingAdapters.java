package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;

/**
 * The following BindingAdapter class was modified from code and explanation provided here:
 * https://stackoverflow.com/a/50338894 and https://stackoverflow.com/a/39444728
 */
@InverseBindingMethods({
        @InverseBindingMethod(type = AdapterView.class, attribute = "android:selectedValue",
                method = "getSelectedItem")})
public class SpinnerBindingAdapters {
        @BindingAdapter("android:selectedValueAttrChanged")
        public static void setSelectedValueListener(AdapterView view,
                                                    final InverseBindingListener attrChanged) {
            if (attrChanged == null) {
                view.setOnItemSelectedListener(null);
            } else {
                view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        attrChanged.onChange();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        attrChanged.onChange();
                    }
                });
            }
        }

        @BindingAdapter("android:selectedValue")
        public static void setSelectedValue(AdapterView<?> view, Object selectedValue) {
            Adapter adapter = view.getAdapter();
            if (adapter == null) {
                return;
            }
            // Setting INVALID_POSITION clears the adapter view
            int position = AdapterView.INVALID_POSITION;
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(selectedValue)) {
                    position = i;
                    break;
                }
            }
            view.setSelection(position);
        }
}