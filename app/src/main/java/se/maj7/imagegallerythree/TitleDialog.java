package se.maj7.imagegallerythree;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TitleDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_title, null);

        // EditText & OK-button
        final EditText editTitle = (EditText) dialogView.findViewById(R.id.dialogEditTitle);
        Button btnOK = (Button) dialogView.findViewById(R.id.dialogButtonOK);

        // OK-button click
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryActivity callingActivity = (GalleryActivity) getActivity();
                callingActivity.setImageTitle(editTitle.getText().toString());
                dismiss();
            }
        });

        // Keyboard button 'Done' pressed
        editTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    GalleryActivity callingActivity = (GalleryActivity) getActivity();
                    callingActivity.setImageTitle(editTitle.getText().toString());
                    dismiss();
                }
                return false;
            }
        });

        builder.setView(dialogView);
        final Dialog dialog = builder.create();

        // Show keyboard
        editTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        editTitle.requestFocus();

        return dialog;
    }

}

