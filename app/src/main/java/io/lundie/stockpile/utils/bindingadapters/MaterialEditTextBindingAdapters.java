package io.lundie.stockpile.utils.bindingadapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.adapters.ListenerUtil;

import com.google.android.material.textfield.TextInputLayout;

import io.lundie.stockpile.R;
import io.lundie.stockpile.features.targets.TargetsTrackerType;
import io.lundie.stockpile.features.targets.TargetsTrackerTypeWrapper;

public class MaterialEditTextBindingAdapters {

    @BindingAdapter("errorText")
    public static void setErrorMessage(TextInputLayout textInputLayout, String errorMessage) {
        if(errorMessage != null && !errorMessage.isEmpty()) {
            textInputLayout.setError(errorMessage);
        } else {
            textInputLayout.setError(null);
        }
    }

    @BindingAdapter("android:hint")
    public static void setAddTargetQuantityHint(TextInputLayout textInputLayout,
                                                TargetsTrackerTypeWrapper trackerTarget) {
        int targetType = trackerTarget.getTypeDef();
        String hintMessage = "";
        if(targetType == TargetsTrackerType.CALORIES) {
            hintMessage = textInputLayout.getResources().getString(R.string.calories);
        } else if (targetType == TargetsTrackerType.ITEMS) {
            hintMessage = textInputLayout.getResources().getString(R.string.items);
        }

        textInputLayout.setHint(hintMessage + " "
                + textInputLayout.getResources().getString(R.string.target));
    }

    /**
     * Customised Binding Adapter from the android framework:
     * https://android.googlesource.com/platform/frameworks/data-binding/+/android-6.0.0_r7/
     * Bind in SML as follows:
     * "@{(s, start, count, after) -> handler.callToMethod(s) }" where s is {@link CharSequence}
     */
    @BindingAdapter("onTextChanged")
    public static void setListener(TextView view, OnTextChanged onTextChanged) {
        setListener(view, null, onTextChanged, null);
    }

    /**
     * Customised Binding Adapter from the android framework:
     * Bind in XML as follows:
     */
    @BindingAdapter({"beforeTextChanged", "onTextChanged", "afterTextChanged"})
    public static void setListener(TextView view, final BeforeTextChanged before,
                                   final OnTextChanged on, final AfterTextChanged after) {
        final TextWatcher newValue;
        if (before == null && after == null && on == null) {
            newValue = null;
        } else {
            newValue = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (before != null) {
                        before.beforeTextChanged(s, start, count, after);
                    }
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (on != null) {
                        on.onTextChanged(s, start, before, count);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                    if (after != null) {
                        after.afterTextChanged(s);
                    }
                }
            };
        }
        final TextWatcher oldValue = ListenerUtil.trackListener(view, newValue, R.id.textWatcher);
        if (oldValue != null) {
            view.removeTextChangedListener(oldValue);
        }
        if (newValue != null) {
            view.addTextChangedListener(newValue);
        }
    }
    public interface AfterTextChanged {
        void afterTextChanged(Editable s);
    }
    public interface BeforeTextChanged {
        void beforeTextChanged(CharSequence s, int start, int count, int after);
    }
    public interface OnTextChanged {
        void onTextChanged(CharSequence s, int start, int before, int count);
    }
}
