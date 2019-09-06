package com.carpool.tagalong.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.TimelineAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetTimelineRequest;
import com.carpool.tagalong.models.ModelGetTimelineResponse;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverProfileRideTimelineActivity extends AppCompatActivity {

    private static final String TAG = DriverProfileRideTimelineActivity.class.getSimpleName();
    private List<ModelGetCurrentRideResponse.Timeline> timelineData = new ArrayList<>();
    private String rideId;
    private TimelineAdapter timelineAdapter;
    private Toolbar toolbar;
    private LinearLayout toolbarLayout;
    private RecyclerView timelineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_ride_timeline);

        initializeViews();

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey(Constants.RIDEID)) {
                rideId = getIntent().getExtras().getString(Constants.RIDEID);
                if (!rideId.equals("")) {
                    getPost();
                }
            }
        }
    }

    private void initializeViews() {

        timelineList = findViewById(R.id.ride_list_timeline);
        toolbarLayout = findViewById(R.id.toolbar_timeline_data);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Rides");
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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        timelineList.setLayoutManager(mLayoutManager);
        timelineList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPost() {

        try {

            ModelGetTimelineRequest modelGetTimelineRequest = new ModelGetTimelineRequest();
            modelGetTimelineRequest.setRideId(rideId);

            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.getPost(TagALongPreferenceManager.getToken(this), modelGetTimelineRequest).enqueue(new Callback<ModelGetTimelineResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetTimelineResponse> call, Response<ModelGetTimelineResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    Toast.makeText(DriverProfileRideTimelineActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    timelineData = response.body().getData();
                                    timelineAdapter = new TimelineAdapter(DriverProfileRideTimelineActivity.this, timelineData);
                                    timelineList.setAdapter(timelineAdapter);
                                }
                            } else {
                                Toast.makeText(DriverProfileRideTimelineActivity.this, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetTimelineResponse> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e(TAG, "FAILURE verification");
                        }
                    });
                }
            } else {
                Toast.makeText(DriverProfileRideTimelineActivity.this, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }
}