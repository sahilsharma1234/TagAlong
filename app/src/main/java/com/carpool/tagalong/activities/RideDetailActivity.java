package com.carpool.tagalong.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelGetRecentRidesResponse;
import com.carpool.tagalong.views.RegularTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RideDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ModelGetRecentRidesResponse.RideData rideData;
    private RegularTextView riderName, sourceLoc, destLoc, cabDetails, startTime, endTime, amountpaid;
    private CircleImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

        initializeData();
    }

    private void initializeData() {

        toolbar  = findViewById(R.id.toolbar_recent_ride);

        riderName  = findViewById(R.id.userName);
        sourceLoc  = findViewById(R.id.start_point_source_name);
        destLoc    = findViewById(R.id.end_point_dest_name);
        cabDetails = findViewById(R.id.car_details);
        startTime  = findViewById(R.id.startRideTime);
        endTime    = findViewById(R.id.endTime);
        amountpaid = findViewById(R.id.estimated_cost);
        image      = findViewById(R.id.user_image);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        if(getIntent().getExtras().containsKey(Constants.RIDE_DETAIL)){

            rideData   = (ModelGetRecentRidesResponse.RideData) getIntent().getExtras().getSerializable(Constants.RIDE_DETAIL);
        }

        if(rideData.isDrive){
            Glide.with(this)
                    .load(rideData.getDriverPic())
                .into(image);
            riderName.setText(rideData.getDriverName());
        }else{
            Glide.with(this)
                    .load(DataManager.getModelUserProfileData().getProfile_pic())
                    .into(image);
            riderName.setText(DataManager.getModelUserProfileData().getUserName());
        }

        sourceLoc.setText(rideData.getStartLocation());
        destLoc.setText(rideData.getEndLocation());
        cabDetails.setText("cab Detaisl");
        startTime.setText(rideData.getRideDate());
        endTime.setText("endTime");
        amountpaid.setText("$10");
    }
}