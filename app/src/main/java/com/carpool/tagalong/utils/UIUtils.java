package com.carpool.tagalong.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.listeners.AlertDialogPermissionBoxClickInterface;

/**
 * Created by sahilsharma on 8/11/17.
 */

public class UIUtils {

    /**
     * Show Custom Confirm Dialog
     *
     * @param context
     * @param msg
     * @param imageResource
     * @param positiveBtnCaption
     * @param negativeBtnCaption
     * @param alertDialogBoxClickListener
     */
    public static void showCustomConfirmDialog(Activity context, String msg, int imageResource, String positiveBtnCaption, String negativeBtnCaption, final AlertDialogPermissionBoxClickInterface alertDialogBoxClickListener) {

        TextView textViewMsg;
        ImageView imageView;
        TextView buttonPositive;
        TextView buttonNegative;
        AlertDialog alertDialog;
        try {
            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            LayoutInflater inflater = context.getLayoutInflater();
            ViewGroup parent = null;
            View dialogView = null;
            if (imageResource == 2) {
                dialogView = inflater.inflate(R.layout.custom_alert_logout, parent);
            }else if(imageResource == 2) {

            }
            else
                dialogView = inflater.inflate(R.layout.confirmation_dialog_layout, parent);

            textViewMsg = dialogView.findViewById(R.id.textViewMessage);
            textViewMsg.setText(msg);

            imageView = dialogView.findViewById(R.id.imageViewAlertImage);
            if (imageResource == -1 || imageResource == 2) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setBackgroundResource(imageResource);
            }

            CustomConfirmAlertButtonClickListener customConfirmAlertButtonClickListener = new CustomConfirmAlertButtonClickListener(alertDialog, alertDialogBoxClickListener);

            buttonPositive = dialogView.findViewById(R.id.buttonPositive);
            buttonPositive.setText(positiveBtnCaption);
            buttonPositive.setOnClickListener(customConfirmAlertButtonClickListener);

            buttonNegative = dialogView.findViewById(R.id.buttonNegative);
            buttonNegative.setText(negativeBtnCaption);
            buttonNegative.setOnClickListener(customConfirmAlertButtonClickListener);

            alertDialog.show();
            alertDialog.setContentView(dialogView);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void dismissDialog(Dialog dialog) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void showToastNotification(Context context, String message, String title, boolean isLongDuration) {
        int duration = 0;

        if (isLongDuration) {
            duration = Toast.LENGTH_LONG;
        } else {
            duration = Toast.LENGTH_SHORT;
        }

        if (message != null && !message.isEmpty()) {
            if (title != null) {
                String toastMessage = title + "\n\n" + message;
                Toast.makeText(context, toastMessage, duration).show();
            } else {
                String toastMessage = message;
                Toast.makeText(context, toastMessage, duration).show();
            }
        }
    }

    public static void showSnackBar(ViewGroup view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * Alert dialog box for alert message
     *
     * @param context Activity context
     * @param msg     Message to show on dialog
     */
    public static void alertBox(Context context, String msg) {
        alertBox(context, msg, 0);
    }

    public static void alertBox(final Context context, String msg, final int code) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setMessage(msg);

            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static class CustomConfirmAlertButtonClickListener implements android.view.View.OnClickListener {
        private AlertDialogPermissionBoxClickInterface alertDialogClickListener;
        private AlertDialog alertDialog;

        public CustomConfirmAlertButtonClickListener(AlertDialog alertDialog, AlertDialogPermissionBoxClickInterface alertDialogClickListener) {
            this.alertDialogClickListener = alertDialogClickListener;
            this.alertDialog = alertDialog;
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            boolean isPositiveButtonClicked;
            switch (id) {
                case R.id.buttonPositive:
                    isPositiveButtonClicked = true;
                    alertDialogClickListener.onButtonClicked(isPositiveButtonClicked);
                    break;
                case R.id.buttonNegative:
                    isPositiveButtonClicked = false;
                    alertDialogClickListener.onButtonClicked(isPositiveButtonClicked);
                    break;
                default:
                    break;
            }
            dismissDialog(alertDialog);
        }
    }
}