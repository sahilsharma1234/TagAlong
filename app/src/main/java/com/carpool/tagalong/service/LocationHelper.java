package com.carpool.tagalong.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class LocationHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public  static final int REQUEST_CODE_PERMISSION = 201;
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private static Activity current_activity;
    private static LocationHelper locationHelper = null;
    private final String TAG = LocationHelper.class.getSimpleName();
    private Context context;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private int intervel = 5000;
    private int fastestIntervel = 1000;
    private int maxWaitTime = 2 * intervel;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private LocationRequest mLocationRequest;

    private LocationHelper(Context context) {
        this.context = context;
    }

    public LocationHelper(Activity context) {

        current_activity = context;
        this.context = context;
//        hasPermission();
    }

    public static LocationHelper getInstance(Context context) {

        if (locationHelper == null) {
            locationHelper = new LocationHelper(context);
        }
//        current_activity = context;
        return locationHelper;
    }

    public static LocationHelper getLocationHelper() {
        return locationHelper;
    }

    public Location getmLastLocation() {
        return mLastLocation;
    }

    public void setmLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
    }

    public boolean isConnected() {
        if (mGoogleApiClient != null)
            return mGoogleApiClient.isConnected();
        else
            return false;
    }

    /**
     * Method to verify google play services on the device
     */

    public boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                if(current_activity!=null)
                googleApiAvailability.getErrorDialog(current_activity, resultCode, PLAY_SERVICES_REQUEST).show();
            } else {
//                Utils.showToast(context,"This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Method to display the location on UI
     */

//    @SuppressLint("MissingPermission")
//    public Location getLocation() {
//        try {
//            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
////            mLastLocation = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,);
//            return mLastLocation;
//        } catch (Exception e) {
////            Utils.print(TAG,e.getMessage());
//        }
//        return null;
//    }
    protected void startLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to build GoogleApiClient
     */

    @SuppressLint("RestrictedApi")
    public void buildGoogleApiClient() {
//        if (hasPermission()) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();

            mGoogleApiClient.connect();

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(intervel);
            mLocationRequest.setFastestInterval(fastestIntervel);
            mLocationRequest.setMaxWaitTime(maxWaitTime);
            mLocationRequest.setSmallestDisplacement(0);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
//                    Utils.print(TAG, locationSettingsResult.toString());
                    final Status status = locationSettingsResult.getStatus();

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location requests here
//                            mLastLocation = getLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                if(current_activity!=null)
                                status.startResolutionForResult(current_activity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
//        }
    }

    /**
     * Method used to connect GoogleApiClient
     */
    public void connectApiClient() {
//        if (hasPermission())
            mGoogleApiClient.connect();
    }

    /**
     * Method used to disconnect GoogleApiClient
     */
    private void disConnectApiClient() {
        mGoogleApiClient.disconnect();
        mGoogleApiClient.isConnected();
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            disConnectApiClient();
        }
    }

    /**
     * Handles the activity results
     */
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CHECK_SETTINGS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        // All required changes were successfully made
//                        mLastLocation=getLocation();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        // The user was asked to change settings, but chose not to
//                        break;
//                    default:
//                        break;
//                }
//                break;
//        }
//    }
//    private boolean hasPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            if (ActivityCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED)
//                current_activity.requestPermissions(permissions, REQUEST_CODE_PERMISSION);
//            else
//                return true;
//        return true;
//    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}