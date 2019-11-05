package com.carpool.tagalong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.service.SinchService;
import com.sinch.android.rtc.calling.Call;

import java.util.HashMap;
import java.util.Map;

public class PlaceCallActivity extends BaseActivityCalling {

    private Button mCallButton;
    private TextView mCallName;
    private String recepientId = "", recepientname = "", recepientImage = "";
    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.callButton:
                    callButtonClicked();
                    break;

                case R.id.stopButton:
                    stopButtonClicked();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCallName = findViewById(R.id.callName);
        mCallButton = findViewById(R.id.callButton);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);

        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(buttonClickListener);

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().get(Constants.CALLERID) != null) {
                recepientId = getIntent().getExtras().get(Constants.CALLERID).toString();
            }
            if (getIntent().getExtras().get(Constants.RECEPIENTNAME) != null) {
                recepientname = getIntent().getExtras().get(Constants.RECEPIENTNAME).toString();
                mCallName.setText(recepientname);
            }
            if (getIntent().getExtras().get(Constants.RECEPIENTIMAGE) != null) {
                recepientImage = getIntent().getExtras().get(Constants.RECEPIENTIMAGE).toString();
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        TextView userName = findViewById(R.id.loggedInName);
        userName.setText(TagALongPreferenceManager.getDeviceProfile(this).getUserName());
        mCallButton.setEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void stopButtonClicked() {
        finish();
    }

    private void callButtonClicked() {

/*
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Double longitude = lastLoc.getLongitude();
        Double latitude = lastLoc.getLatitude();
     /*   Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(Constants.CALLERNAME, TagALongPreferenceManager.getDeviceProfile(this).getUserName());
        headers.put(Constants.CALLERIMAGE, TagALongPreferenceManager.getDeviceProfile(this).getProfile_pic());

//        String userName = mCallName.getText().toString();
        if (recepientId.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call = getSinchServiceInterface().callUser(recepientId, headers);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra(Constants.RECEPIENTNAME, recepientname);
        callScreen.putExtra(Constants.RECEPIENTIMAGE, recepientImage);
        startActivity(callScreen);
    }
}