package com.carpool.tagalong.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.carpool.tagalong.R;

public class SimpleAlertDialog {

    private static AlertDialog alertDialog;

    public static void show(final Activity activityContext, String title, String message, final SimpleDialogListener dialogListener) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        alertDialog = new AlertDialog.Builder(activityContext).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(true);
        if (!title.isEmpty()) {
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertDialog != null && alertDialog.isShowing()) {
                            if (dialogListener != null) {
                                dialogListener.onFinishDialog();
                            }
                            alertDialog.dismiss();
                        }
                    }
                });
        alertDialog.show();
    }

    public static void show(Activity activityContext, String message, SimpleDialogListener dialogListener) {
        show(activityContext, "", message, dialogListener);
    }
    public static void show(Activity activityContext, String message) {
        show(activityContext, "", message,  null);
    }
    public static void show(Activity activityContext, String title, String message) {
        show(activityContext, title, message,  null);
    }
    public interface SimpleDialogListener {
        public void onFinishDialog();
    }
}
