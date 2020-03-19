package io.lundie.stockpile.features.general;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment {

    private DialogListener dialogListener;

    private final static String TITLE_KEY = "title";
    private final static String POS_BTN_KEY = "positive";
    private final static String NEG_BTN_KEY = "negative";
    private final static String MSG_KEY = "message";

    public AlertDialogFragment() { }

    public static AlertDialogFragment newInstance(String title, String message,
                                                  String posButton, String negButton,
                                                  DialogListener listener) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setDialogListener(listener);
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MSG_KEY, message);
        args.putString(POS_BTN_KEY, posButton);
        args.putString(NEG_BTN_KEY, negButton);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = getArguments().getString(TITLE_KEY);
            String posBtnLabel = getArguments().getString(POS_BTN_KEY);
            String negBtnLabel = getArguments().getString(NEG_BTN_KEY);
            String message = getArguments().getString(MSG_KEY);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(posBtnLabel,
                            (dialog, whichButton) -> {
                        dialogListener.onPositiveClicked();
                        dismiss();
                            }
                    )
                    .setNegativeButton(negBtnLabel,
                            (dialog, whichButton) -> dismiss()
                    )
                    .create();
    }


    private void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public interface DialogListener {
        void onPositiveClicked();
    }
}