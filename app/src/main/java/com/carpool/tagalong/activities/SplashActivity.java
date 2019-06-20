package com.carpool.tagalong.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.carpool.tagalong.R;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SHOW_TIME = 3000;
    protected static final String TAG = "SplashActivity";
    //Handler to finish the activity after some time of delay in displaying Splash screen
    private Handler handler;
    //Runnable
    private Runnable runnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeData();
        displayLocationSettingsRequest(this);
    }

    private void displayLocationSettingsRequest(Context context) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("", "All location settings are satisfied.");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SplashActivity.this, 123);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
    /**
     * Initialise the objects.
     */
    private void initializeData() {
        handler = new Handler();
        runnable = new ActivityRunnable();
    }

    /**
     * This Inner class handles the navigation from Splash activity to other activity
     * when splash time ends.
     */
    private class ActivityRunnable implements Runnable {
        @Override
        public void run() {
            moveFurther();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, SPLASH_SHOW_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // remove callback in onPause method to avoid reoccurring of splash time wait.
        handler.removeCallbacks(runnable);
    }

    private void moveFurther() {

        Intent intent;

        if(TagALongPreferenceManager.getToken(this).equals("") || TagALongPreferenceManager.getDeviceProfile(this) == null)
            intent = new Intent(SplashActivity.this, SignInActivity.class);

        else if(!TagALongPreferenceManager.getToken(this).equals("") && TagALongPreferenceManager.getDeviceProfile(this) != null)
            intent = new Intent(SplashActivity.this, HomeActivity.class);

        else if(!TagALongPreferenceManager.getToken(this).equals("") && TagALongPreferenceManager.getDeviceProfile(this) == null)
            intent = new Intent(SplashActivity.this, SignInActivity.class);

        else
            intent = new Intent(SplashActivity.this, HomeActivity.class);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}