package com.carpool.tagalong.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.utils.LocationAddress;
import com.carpool.tagalong.utils.Utils;

/**
 * Created by sahilsharma on 28/11/17.
 */

public class JobSchedulerService extends JobService {

    private static final String TAG = JobSchedulerService.class.getName();
    private static LocationHelper locationHelper = null;
    private Location mLastLocation;
    private static int locationcount = 0;

    private Handler mJobHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            try {

                locationHelper = LocationHelper.getInstance(JobSchedulerService.this);

                Log.e(" Job Scheduler JOB RUNS", "********************** count ");

                if (locationHelper != null) {
                    mLastLocation = locationHelper.getmLastLocation();
                    if (locationHelper.checkPlayServices()) {
                        if (!locationHelper.isConnected()) {
                            locationHelper.buildGoogleApiClient();
                            locationHelper.connectApiClient();
                        }
                    }
                    getLocation();
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    Utils.scheduleApplicationPackageJob(JobSchedulerService.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (Build.VERSION.SDK_INT >= 21) {
                    Utils.scheduleApplicationPackageJob(JobSchedulerService.this);
                }
            }
            return true;
        }
    });

    private void getLocation() {

        if (mLastLocation != null) {

            Double latitude  = mLastLocation.getLatitude();
            Double longitude = mLastLocation.getLongitude();

            float accuracy   = mLastLocation.getAccuracy();
            Log.e("Location fetched", "is " + latitude + " " + longitude + " Accuracy " + accuracy);

            if (String.valueOf(latitude).equals("")) {
            }

            TagALongPreferenceManager.saveUserLocationLatitude(getApplicationContext(), String.valueOf(latitude));
            TagALongPreferenceManager.saveUserLocationLongitude(getApplicationContext(), String.valueOf(longitude));

            Utils.updateCoordinates1(mLastLocation,this);
            LocationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mJobHandler.removeMessages(1);
        return false;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, jobParameters));
        return true;
    }

    private class GeocoderHandler extends Handler {

        @Override
        public void handleMessage(Message message) {

            String locationAddress;

            switch (message.what) {

                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    TagALongPreferenceManager.saveUserAddress(getApplicationContext(), locationAddress);
                    break;

                default:
            }
        }
    }
}