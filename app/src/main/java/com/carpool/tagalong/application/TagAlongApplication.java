package com.carpool.tagalong.application;

import android.app.Application;

import com.carpool.tagalong.utils.TypefaceUtil;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import co.paystack.android.PaystackSdk;

public class TagAlongApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.setDefaultFont(this, "MONOSPACE", "fonts/roboto_regular.ttf");
        FacebookSdk.sdkInitialize(getApplicationContext());
        PaystackSdk.initialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}