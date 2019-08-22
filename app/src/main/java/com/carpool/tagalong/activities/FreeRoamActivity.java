package com.carpool.tagalong.activities;

import android.animation.ArgbEvaluator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.QuickRidesRiderAdapter;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeRoamActivity extends BaseActivity implements View.OnClickListener, QuickRidesRiderAdapter.ridersatusclicklistener




{

    private final String TAG = FreeRoamActivity.this.getClass().getSimpleName();
    private static final String ANIM_REQUEST_COUNT_BUTTON = "anim_request_count", ANIM_DOWN_SETTINGS = "anim_request_selection", ANIM_SELFIE_TAKEN = "anim_selfie_taken", ANIM_SELFIE_ACTION = "anim_selfie_action";
    @BindView(R.id.rootll)
    LinearLayout rootll;
    ArgbEvaluator argbEvaluator;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private Context context;
    private Button disableFreeRoam;
    private static boolean isFreeRoamEnabled = false;
    private List<ModelGetCurrentRideResponse.OnBoard> currentRiderList = new ArrayList<>();
    private RecyclerView quickRideRidersList;
    private QuickRidesRiderAdapter quickRidesRiderAdapter;

    private BroadcastReceiver riderListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent != null){

                if(intent.getExtras() != null){
                    if(getIntent().getExtras().containsKey("rideId")){
                        String rideId = getIntent().getExtras().getString("rideId");
                        if(!rideId.equals("")){
                            getRideDetails(rideId);
                        }
                    }
                }
            }

        }
    };

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private RelativeLayout riderDtlsLyt;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_free_roam);

        ButterKnife.bind(this);
        context = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(FreeRoamActivity.this);

        argbEvaluator = new ArgbEvaluator();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        disableFreeRoam  = findViewById(R.id.disableFreeRoam);
        disableFreeRoam.setOnClickListener(this);
        toolbarLayout    = findViewById(R.id.toolbarFreeRoaming);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        ImageView share = toolbarLayout.findViewById(R.id.share);
        riderDtlsLyt    = findViewById(R.id.rider_dtls_lyt_roam);
        share.setImageResource(R.drawable.ic_support);
        share.setVisibility(View.VISIBLE);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Free Roam");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(isFreeRoamEnabled){
            disableFreeRoam.setVisibility(View.VISIBLE);
        }else
            disableFreeRoam.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("launchCurrentRideFragment"));

        LocalBroadcastManager.getInstance(this).registerReceiver(riderListener, new IntentFilter("riderListener"));

        if (!isFreeRoamEnabled) {
            enableFreeRoam();
            blink();
        }
    }

    private void enableFreeRoam() {

        try {

            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.enableDisableQuickRide(TagALongPreferenceManager.getToken(this), true).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            isFreeRoamEnabled = true;
                            disableFreeRoam.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("SAVE Payment DETAILS", "FAILURE SAVING PROFILE");
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableFreeRoam() {

        try {

            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.enableDisableQuickRide(TagALongPreferenceManager.getToken(this), false).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            isFreeRoamEnabled = false;
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("SAVE Payment DETAILS", "FAILURE SAVING PROFILE");
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.disableFreeRoam:
                disableFreeRoam();
                break;
        }
    }

    private void blink() {

        handler = new Handler();

        new Thread(new Runnable() {

            @Override
            public void run() {

                int timeToBlink = 1000;//in milissegunds
                int count = 0;

                try {
                    Thread.sleep(timeToBlink);
                    count++;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (count == 5) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView txt = findViewById(R.id.waiting_txt);
                            if (txt.getVisibility() == View.VISIBLE) {
                                txt.setVisibility(View.INVISIBLE);
                            } else {
                                txt.setVisibility(View.VISIBLE);
                            }
                            blink();
                        }
                    });
                } else {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView txt = findViewById(R.id.waiting_txt);
                            if (txt.getVisibility() == View.VISIBLE) {
                                txt.setVisibility(View.INVISIBLE);
                            } else {
                                txt.setVisibility(View.VISIBLE);
                            }
                            blink();
                        }
                    });
                }
            }
        }).start();
    }

    private void addStartMarker() {

        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                    .zoom(17)
                    .build();

//        addOverlay(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_ride_point_xhdpi);
            options.icon(icon);
            mMap.addMarker(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRiderDetailsLayout(ModelGetCurrentRideResponse.RideData rideData) {

        riderDtlsLyt.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.quick_ride_driver_layout, null);
        quickRideRidersList     = view.findViewById(R.id.quick_ride_riders_list);
        ImageView navigateIcon               = view.findViewById(R.id.ic_navigate_quickRide);

        navigateIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        if(rideData != null){

            if(rideData.getOnBoard()!= null && rideData.getOnBoard().size() > 0){
                currentRiderList  = rideData.getOnBoard();
                quickRidesRiderAdapter = new QuickRidesRiderAdapter(context,currentRiderList, this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                quickRideRidersList.setLayoutManager(linearLayoutManager);
                quickRideRidersList.setAdapter(quickRidesRiderAdapter);

            }
        }
        riderDtlsLyt.addView(view);
        animate(ANIM_DOWN_SETTINGS);
    }

    private void animate(String condition) {

        Animation slideFromBottom = AnimationUtils.loadAnimation(context,
                R.anim.slide_in_bottom);

        switch (condition) {

            case ANIM_DOWN_SETTINGS:

                riderDtlsLyt.setVisibility(View.VISIBLE);
                riderDtlsLyt.startAnimation(slideFromBottom);
                break;
        }
    }

    private void getRideDetails(final String rideId) {

        try {
            if (Utils.isNetworkAvailable(context)) {

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
                                Log.i(FreeRoamActivity.class.getSimpleName(), "Get rides RESPONSE " + response.body().toString());
                                handleRiderDetailsLayout(response.body().getRideData());
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onItemClick(ModelGetCurrentRideResponse.OnBoard onboard) {

    }
}