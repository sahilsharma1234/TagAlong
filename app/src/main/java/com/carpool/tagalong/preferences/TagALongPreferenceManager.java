package com.carpool.tagalong.preferences;

/**
 * Created by rajan on 5/4/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelSignInResponseData;
import com.google.gson.Gson;

public class TagALongPreferenceManager {

    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.TOKEN, "");
        return token;
    }

    public static void saveDeviceProfile(Context context, ModelSignInResponseData data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("device_profile", json);
        editor.apply();
    }

    public static ModelSignInResponseData getDeviceProfile(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("device_profile", null);
        return gson.fromJson(json, ModelSignInResponseData.class);
    }

    public static boolean getDocumentUploadedStatus(Context activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean("isDocumentUploaded", false);
        return status;
    }

    public static void setDocumentUploadedStatus(Context activity,boolean status) {

        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDocumentUploaded", status);
        editor.apply();
    }

    public static String getDeviceToken(Context context) {
        SharedPreferences preference = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        String token = preference.getString(Constants.DEVICE_TOKEN, "");
        return token;
    }

    public static void setDeviceToken(String token, Context context) {
        SharedPreferences preference = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(Constants.DEVICE_TOKEN, token);
        editor.commit();
    }

    public static void saveUserLocationLatitude(Context context, String latitude) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("userLocationLatitude", latitude);
        prefsEditor.apply();
    }

    public static void saveUserLocationLongitude(Context context, String longitude) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("userLocationLongitude", longitude);
        prefsEditor.apply();
    }

    public static String getUserLocationLatitude(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        String token = mPrefs.getString("userLocationLatitude", "");
        return token;
    }

    public static String getUserLocationLongitude(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        String token = mPrefs.getString("userLocationLongitude", "");
        return token;
    }

    public static void saveUserAddress(Context context, String address) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("address", address);
        prefsEditor.apply();
    }

    public static String getUserAddress(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        String address = mPrefs.getString("address", "");
        return address;
    }
}