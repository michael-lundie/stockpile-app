package io.lundie.stockpile.utils.bindingadapters;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputLayout;

public class MaterialEditTextBindingAdapters {

    @BindingAdapter("app:errorText")
    public static void setErrorMessage(TextInputLayout textInputLayout, String errorMessage) {
        if(errorMessage != null && !errorMessage.isEmpty()) {
            textInputLayout.setError(errorMessage);
        } else {
            textInputLayout.setError(null);
        }
    }
}
