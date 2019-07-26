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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carpool.tagalong.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FreeRoamActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = FreeRoamActivity.this.getClass().getSimpleName();
    @BindView(R.id.rootll)
    LinearLayout rootll;
    ArgbEvaluator argbEvaluator;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private Context context;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

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

        toolbarLayout = findViewById(R.id.toolbarFreeRoaming);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        ImageView share = toolbarLayout.findViewById(R.id.share);
        share.setImageResource(R.drawable.ic_support);
        share.setVisibility(View.VISIBLE);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Free Roam");
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
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("launchCurrentRideFragment"));
        blink();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }

    @Override
    public void onClick(View v) {

    }

    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;//in milissegunds
                int count = 0;
                try {
                    Thread.sleep(timeToBlink);
                    count++;
                } catch (Exception e) {
                }

                if(count ==5){

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
                else {

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
}