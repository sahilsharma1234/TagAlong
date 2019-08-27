package com.carpool.tagalong.activities;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.fragments.CurrentUpcomingFragment;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetRecentRidesResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsRequest;
import com.carpool.tagalong.models.ModelRateRiderequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;
import com.carpool.tagalong.views.RegularTextView;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ModelGetRecentRidesResponse.RideData rideData;
    private RegularTextView riderName, sourceLoc, destLoc, cabDetails, startTime, endTime, amountpaid, rating;
    private CircleImageView image;
    private  ModelGetCurrentRideResponse.RideData modelGetCurrentRideResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

        initializeData();
    }

    private void initializeData() {

        toolbar = findViewById(R.id.toolbar_recent_ride);
        rating = toolbar.findViewById(R.id.rating_toolbar);
        riderName = findViewById(R.id.userName);
        sourceLoc = findViewById(R.id.start_point_source_name);
        destLoc = findViewById(R.id.end_point_dest_name);
        cabDetails = findViewById(R.id.car_details);
        startTime = findViewById(R.id.startRideTime);
        endTime = findViewById(R.id.endTime);
        amountpaid = findViewById(R.id.estimated_cost);
        image = findViewById(R.id.user_image);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        if (getIntent().getExtras().containsKey(Constants.RIDE_DETAIL)) {

            rideData = (ModelGetRecentRidesResponse.RideData) getIntent().getExtras().getSerializable(Constants.RIDE_DETAIL);
            getRideDetails(rideData.get_id());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rating_toolbar:
                showSubmitReviewDialog();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSubmitReviewDialog() {

        com.carpool.tagalong.views.RegularTextView iv_userName;
        RatingBar ratingBar;
        final EditText feedBackComments;
        Button submitFeedback;
        CircleImageView user_image;
        final float rating;

        AlertDialog alertDialog = null;

        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.submit_review_dialog_layout, null);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(dialogView);

            feedBackComments = dialogView.findViewById(R.id.feedback_comments);
            submitFeedback = dialogView.findViewById(R.id.submitReview);
            user_image = dialogView.findViewById(R.id.iv_user_profile_image);
            iv_userName = dialogView.findViewById(R.id.tv_driver_name);
            ratingBar = dialogView.findViewById(R.id.rating_bar);

            rating = ratingBar.getRating();

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.avatar_avatar_12)
                    .error(R.drawable.avatar_avatar_12);

            Glide.with(this).load(rideData.getDriverPic()).apply(options).into(user_image);

            iv_userName.setText(rideData.getDriverName());

            alertDialog = dialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;

            submitFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rateDriver(feedBackComments.getText().toString(), rating);
                    finalAlertDialog.cancel();
                }
            });

            alertDialog.show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void rateDriver(String comments, final float rating) {

        try {

            ModelRateRiderequest modelRateRiderequest = new ModelRateRiderequest();
            modelRateRiderequest.setRateTo(rideData.getUserId());
            modelRateRiderequest.setRideId(rideData.get_id());
            modelRateRiderequest.setRating(Double.valueOf(String.valueOf(rating)));
            modelRateRiderequest.setReview(comments);

            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.rateRide(TagALongPreferenceManager.getToken(this), modelRateRiderequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                Toast.makeText(RideDetailActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                if (response.body().getStatus() == 1) {

                                    handleRatingResponse(rating);

                                } else {
                                    Toast.makeText(RideDetailActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(RideDetailActivity.this, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();
                            Toast.makeText(RideDetailActivity.this, "Some error in rating driver!!", Toast.LENGTH_LONG).show();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Rate driver", "FAILURE Rating driver");
                        }
                    });
                }
            } else {
                Toast.makeText(RideDetailActivity.this, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }

    private void handleRatingResponse(float rating) {
        this.rating.setText(rating + "");
    }

    private void getRideDetails(final String rideId) {

        try {
            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ModelGetRideDetailsRequest modelGetRideDetailsRequest = new ModelGetRideDetailsRequest();
                    modelGetRideDetailsRequest.setRideId(rideId);

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.getRideDetails(TagALongPreferenceManager.getToken(this), modelGetRideDetailsRequest).enqueue(new Callback<ModelGetCurrentRideResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetCurrentRideResponse> call, Response<ModelGetCurrentRideResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                Log.i(CurrentUpcomingFragment.class.getSimpleName(), "Get rides RESPONSE " + response.body().toString());
                                modelGetCurrentRideResponse = response.body().getRideData();
                                handleRideDetails();

                            } else {
                                Toast.makeText(RideDetailActivity.this, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetCurrentRideResponse> call, Throwable t) {
                            ProgressDialogLoader.progressDialogDismiss();
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Get All rides", "FAILURE GETTING ALL RIDES");
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ProgressDialogLoader.progressDialogDismiss();
        }
    }

    private void handleRideDetails() {

        if (modelGetCurrentRideResponse.isDrive) {  // if you are the driver of this ride // then your own pic will be shown
            Glide.with(this)
                    .load(DataManager.getModelUserProfileData().getProfile_pic())
                    .into(image);
            riderName.setText(DataManager.getModelUserProfileData().getUserName());
            rating.setVisibility(View.GONE);
        } else { // if you are the rider then you have to rate driver here so driver name and details

            Glide.with(this)
                    .load(rideData.getDriverPic())
                    .into(image);
            riderName.setText(rideData.getDriverName());

            if (rideData.getRating().equals("")) {
                rating.setText("RATE RIDE");
                rating.setOnClickListener(this);
            } else {
                rating.setText(rideData.getRating() + "");
                rating.setOnClickListener(null);
            }
            cabDetails.setText(modelGetCurrentRideResponse.getDriverDetails().getVehicle()+ " "+modelGetCurrentRideResponse.getDriverDetails().getVehicleNumber());
        }

        sourceLoc.setText(rideData.getStartLocation());
        destLoc.setText(rideData.getEndLocation());
        startTime.setText(rideData.getStartedDateWithTime());
        endTime.setText(rideData.getCompletedDateWithTime());
        amountpaid.setText("$ "+modelGetCurrentRideResponse.getEstimatedFare());
    }
}