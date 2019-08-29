package com.carpool.tagalong.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.RecentRideDriverProfileAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelGetDriverProfileResponse;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;
import com.carpool.tagalong.views.RegularTextView;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carpool.tagalong.managers.DataManager.modelSearchRideRequest;

public class DriverProfileActivity extends AppCompatActivity implements View.OnClickListener, RecentRideDriverProfileAdapter.rideclicklistener {

    private String TAG = DriverProfileActivity.this.getClass().getSimpleName();
    private RecyclerView recycler_view_recent_rides_driver_profile;
    private List<ModelGetDriverProfileResponse.Rides> ridesList;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private String driverId;
    private Context context;
    private RegularTextView name, address, drove, rating, trips, viewAll;
    private CircleImageView profile_image;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        initView();
    }

    private void initView() {

        context = this;
        toolbarLayout = findViewById(R.id.toolbar_driverprofile);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Driver");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name    = findViewById(R.id.tv_driver_name);
        address = findViewById(R.id.tv_driver_address);
        profile_image =  findViewById(R.id.iv_rider_profile_image2);
        drove   =        findViewById(R.id.driver_drove);
        rating  =        findViewById(R.id.driver_rating);
        trips   =        findViewById(R.id.driver_trips);
        viewAll =        findViewById(R.id.viewAllRidesDriver);
        viewAll.setVisibility(View.GONE);
        viewAll.setOnClickListener(this);
        recycler_view_recent_rides_driver_profile = findViewById(R.id.recycler_view_recent_rides_driver_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        if (getIntent().getExtras() != null) {
            driverId = getIntent().getExtras().getString(Constants.DRIVER_DATA);
            getDriverProfile(driverId);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(listener,
                new IntentFilter("launchCurrentRideFragment"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }

    private void getDriverProfile(String driverId) {

        try {
            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "Request Ride request is: " + modelSearchRideRequest.toString());

                if (restClientRetrofitService != null) {
                    ProgressDialogLoader.progressDialogCreation(this,getResources().getString(R.string.please_wait));

                    restClientRetrofitService.getDriverProfile(TagALongPreferenceManager.getToken(context), driverId).enqueue(new Callback<ModelGetDriverProfileResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetDriverProfileResponse> call, Response<ModelGetDriverProfileResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null && response.body().getStatus() == 1) {

                                Log.i(TAG, "Get driver profile Response is: " + response.body().toString());

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                handleResponse(response.body().getData());

                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetDriverProfileResponse> call, Throwable t) {

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
                Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }

    private void handleResponse(ModelGetDriverProfileResponse.Data data) {

        if (data != null) {

            name.setText(data.getUserName());
            address.setText(data.getAddress());
            drove.setText(data.getDrove());
            rating.setText(data.getRating()+"");
            trips.setText(data.getTrips()+" trips");
            Glide.with(this).load(data.getProfile_pic()).into(profile_image);

            ridesList = data.getRides();

            if (ridesList != null && ridesList.size() > 0) {
                viewAll.setVisibility(View.VISIBLE);

                RecentRideDriverProfileAdapter mAdapter = new RecentRideDriverProfileAdapter(this, ridesList, this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                recycler_view_recent_rides_driver_profile.setLayoutManager(mLayoutManager);
                recycler_view_recent_rides_driver_profile.setItemAnimator(new DefaultItemAnimator());
                recycler_view_recent_rides_driver_profile.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){

            case R.id.viewAllRidesDriver:
                handleViewAllRides();
                break;
        }
    }

    private void handleViewAllRides() {

        Intent intent = new Intent(context, ViewAllRidesDriverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("driverId",driverId);
        startActivity(intent);
    }

    @Override
    public void onItemClick(ModelGetDriverProfileResponse.Rides s) {

        if(s != null) {
            Intent intent = new Intent(context, DriverProfileRideTimelineActivity.class);
            intent.putExtra(Constants.RIDEID, s.get_id());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}