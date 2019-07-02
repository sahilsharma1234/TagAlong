package com.carpool.tagalong.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogLoader {
    private ProgressDialog pd;

    private static ProgressDialog progressDialog;

    Context context;
    Activity activity;

    public ProgressDialogLoader(Context context) {
        this.context = context;
        this.activity = (Activity) context;
    }

    public void progressDialogCreation() {
        try {
            if (pd == null)
                pd = ProgressDialog.show(activity, "", "Loading", true);
        } catch (Exception e) {

        }
    }

    public static void progressDialogCreation(Activity activity, String title) {
        try {
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(activity, "", title, true);
        } catch (Exception e) {

        }
    }

    public static void progressDialogDismiss() {

        try {
            if ((progressDialog != null) && progressDialog.isShowing())
                progressDialog.dismiss();

            progressDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}