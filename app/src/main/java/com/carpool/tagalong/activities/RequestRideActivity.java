package com.carpool.tagalong.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelRequestRide;
import com.carpool.tagalong.models.ModelSearchRideResponseData;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carpool.tagalong.managers.DataManager.modelSearchRideRequest;

public class RequestRideActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private Button onlinePaymentBtn, requestRide;
    private com.carpool.tagalong.views.RegularTextView profileDriver, name, startLocation, endLocation, startTime, estimatedCost, seats, bags, kids, ratings;
    private CircleImageView profile_pic;
    private Context context;
    private RelativeLayout progressBarLayout;
    private ModelSearchRideResponseData modelSearchRideResponseData;
    private String TAG = RequestRideActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_ride);
        context = this;

        toolbarLayout = findViewById(R.id.toolbar_request_ride);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        onlinePaymentBtn = findViewByIdAndCast(R.id.online_payment);
//        cashBtn = findViewByIdAndCast(R.id.cash_payment);
        requestRide   = findViewByIdAndCast(R.id.request_ride_btn);
        profileDriver = findViewByIdAndCast(R.id.profile_driver_txt);

        name = findViewById(R.id.profile_main_name);
        startLocation = findViewById(R.id.start_point_source_name);
        endLocation = findViewById(R.id.end_point_dest_name);
        startTime = findViewById(R.id.ride_start_timing);
        profile_pic = findViewById(R.id.image_profile_user);
        estimatedCost = findViewById(R.id.estimated_cost);
        seats = findViewById(R.id.noOfSeats);
        bags = findViewById(R.id.noOfBags);
        kids = findViewById(R.id.noOfKids);
        ratings = findViewById(R.id.ratings);
        progressBarLayout = findViewById(R.id.requestRideProgressBar);

        profileDriver.setOnClickListener(this);
        onlinePaymentBtn.setOnClickListener(this);
//        cashBtn.setOnClickListener(this);
        requestRide.setOnClickListener(this);

        title.setText("Request Ride");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        if (DataManager.modelSearchRideResponseData != null) {
            modelSearchRideResponseData = DataManager.modelSearchRideResponseData;

            name.setText(modelSearchRideResponseData.getUserName());
            startLocation.setText(modelSearchRideResponseData.getStartLocation());
            endLocation.setText(modelSearchRideResponseData.getEndLocation().trim());
            startTime.setText(modelSearchRideResponseData.getRideDateTime());
            Glide.with(context)
                    .load(modelSearchRideResponseData.getProfile_pic())
                    .into(profile_pic);

            estimatedCost.setText(String.valueOf(modelSearchRideResponseData.getEstimatedFare()));
            ratings.setText(modelSearchRideResponseData.getRating() + "");

            if (modelSearchRideRequest != null) {

                seats.setText(modelSearchRideRequest.getNoOfSeats() + "");
                bags.setText(modelSearchRideRequest.getBags() + "");
                kids.setText(modelSearchRideRequest.isAllowKids() ? "Yes" : "No");
            }
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.request_ride_btn) {

            requestRide();

        } else if (id == R.id.profile_driver_txt) {

            Intent intent;
            intent = new Intent(RequestRideActivity.this, DriverProfileActivity.class);
            intent.putExtra(Constants.DRIVER_DATA,modelSearchRideResponseData.getUserId());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else {
//
//            Button button = (Button) v;
//            onlinePaymentBtn.setBackground(null);
//            onlinePaymentBtn.setTextColor(RequestRideActivity.this.getResources().getColor(R.color.darker_gray));
//
//            cashBtn.setBackground(null);
//            cashBtn.setTextColor(RequestRideActivity.this.getResources().getColor(R.color.darker_gray));
//
//            button.setBackground(RequestRideActivity.this.getResources().getDrawable(R.drawable.button_shape_payment));
//            button.setTextColor(RequestRideActivity.this.getResources().getColor(R.color.payment_btn_color));
        }
    }

    private void handleRequestRide() {

        DataManager.setStatus(1);

        Intent intent = new Intent("launchCurrentRideFragment");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
    }

    private void requestRide() {

        ModelRequestRide modelRequestRide = new ModelRequestRide();
        modelRequestRide.setRideId(modelSearchRideResponseData.getRideId());
        modelRequestRide.setDriverId(modelSearchRideResponseData.getUserId());
        modelRequestRide.setStartLocation(modelSearchRideResponseData.getStartLocation());
        modelRequestRide.setEndLocation(modelSearchRideResponseData.getEndLocation());
        modelRequestRide.setStartLat(modelSearchRideRequest.getStartLat());
        modelRequestRide.setEndLat(modelSearchRideRequest.getEndLat());
        modelRequestRide.setStartLong(modelSearchRideRequest.getStartLong());
        modelRequestRide.setEndLong(modelSearchRideRequest.getEndLong());

        modelRequestRide.setRideDateTime(modelSearchRideRequest.getRideDateTime());
        modelRequestRide.setNoOfSeats(modelSearchRideRequest.getNoOfSeats());
        modelRequestRide.setAllowKids(modelSearchRideRequest.isAllowKids());
        modelRequestRide.setSmoke(modelSearchRideRequest.isSmoke());
        modelRequestRide.setEstimatedFare(modelSearchRideResponseData.getEstimatedFare());
        modelRequestRide.setDistBtwSrcDest(modelSearchRideResponseData.getTotalDistanceFromSrcDest());

        if (Utils.isNetworkAvailable(context)) {

            ProgressDialogLoader.progressDialogCreation(this,context.getString(R.string.please_wait));

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            Log.i(TAG, "Request Ride request is: " + modelSearchRideRequest.toString());

            if (restClientRetrofitService != null) {

                restClientRetrofitService.requestRide(TagALongPreferenceManager.getToken(context), modelRequestRide).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                        ProgressDialogLoader.progressDialogDismiss();

                        if (response.body() != null && response.body().getStatus() == 1) {

                            Log.i(TAG, "Request Ride Response is: " + response.body().toString());

                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            handleRequestRide();

                        } else if (response.body() != null && response.body().getStatus() == 0) {

                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {
                        ProgressDialogLoader.progressDialogDismiss();

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }

                        Toast.makeText(context, "Some error occurred!! Please try again!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "FAILURE ");
                    }
                });
            }
        } else {
            Toast.makeText(context, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T findViewByIdAndCast(int id) {
        return (T) findViewById(id);
    }
}