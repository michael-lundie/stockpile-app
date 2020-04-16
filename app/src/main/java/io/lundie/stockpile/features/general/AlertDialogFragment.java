package io.lundie.stockpile.features.general;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import io.lundie.stockpile.R;

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

            AlertDialog alertDialog =  new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(posBtnLabel,
                            (dialog, whichButton) -> {
                                dialogListener.onPositiveClicked();
                                AlertDialogFragment.this.dismiss();
                            }
                    )
                    .setNegativeButton(negBtnLabel,
                            (dialog, whichButton) -> dismiss()
                    )
                    .create();

            alertDialog.setOnShowListener((DialogInterface dialogInterface) -> {
                Button negButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins((int)getResources().getDimension(R.dimen.keyline_1),0,(int)getResources().getDimension(R.dimen.keyline_1),0);
                negButton.setLayoutParams(params);
            });
            return alertDialog;
    }

    private void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public interface DialogListener {
        void onPositiveClicked();
    }
}