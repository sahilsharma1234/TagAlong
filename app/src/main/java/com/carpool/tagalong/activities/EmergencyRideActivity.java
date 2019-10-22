package com.carpool.tagalong.activities;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.RidersAdapterInEmergencyRide;
import com.carpool.tagalong.models.emergencysos.ModelGetEmergencyRidesResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EmergencyRideActivity extends AppCompatActivity implements OnMapReadyCallback, RidersAdapterInEmergencyRide.RiderInEmergencyInterface {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 858;
    private static final int CALL_PHONE_CODE = 266;
    ArgbEvaluator argbEvaluator;
    private ModelGetEmergencyRidesResponse.EmergencyRides emergencyRide;
    private RecyclerView ridersListInEmergencyRide;
    private RidersAdapterInEmergencyRide ridersAdapterInEmergencyRide;
    private com.carpool.tagalong.views.RegularTextView startLoc, endLoc, rideDateTime;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_emergency_rides);
        initViews();

        if (getIntent().getExtras() != null) {
            emergencyRide = (ModelGetEmergencyRidesResponse.EmergencyRides) getIntent().getSerializableExtra("emergency_ride");
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(EmergencyRideActivity.this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        initViews();
        argbEvaluator = new ArgbEvaluator();

        if (emergencyRide == null) {
            alertBox(this, "Some error getting this ride's location");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CODE);
        }
    }

    private void initViews() {

        ridersListInEmergencyRide = findViewById(R.id.ridersList);
        startLoc = findViewById(R.id.emergency_start_point_source_name);
        rideDateTime = findViewById(R.id.emergency_ride_start_timing);
        endLoc = findViewById(R.id.emergency_end_point_dest_name);

        toolbarLayout = findViewById(R.id.toolbar_emergency_ride);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Emergency Ride");
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
    }

    private void addMarkerAndFillDetails() {

        if (!checkPermission()) return;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(emergencyRide.getDriverDetail().getLocation().getCoordinates().get(1), emergencyRide.getDriverDetail().getLocation().getCoordinates().get(0)))
                .zoom(15)
                .build();

        addOverlay(new LatLng(emergencyRide.getDriverDetail().getLocation().getCoordinates().get(1), emergencyRide.getDriverDetail().getLocation().getCoordinates().get(0)));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(emergencyRide.getDriverDetail().getLocation().getCoordinates().get(1), emergencyRide.getDriverDetail().getLocation().getCoordinates().get(0)));
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_sos);
        options.icon(icon);
        mMap.addMarker(options);

        startLoc.setText(emergencyRide.getStartLocation());
        endLoc.setText(emergencyRide.getEndLocation());
        rideDateTime.setText(emergencyRide.getRideDateTime());

        ridersAdapterInEmergencyRide = new RidersAdapterInEmergencyRide(this, emergencyRide.riderList, emergencyRide.getDriverDetail().getLocation(), this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ridersListInEmergencyRide.setLayoutManager(mLayoutManager);
        ridersListInEmergencyRide.setItemAnimator(new DefaultItemAnimator());
        ridersListInEmergencyRide.setAdapter(ridersAdapterInEmergencyRide);
    }

    public void addOverlay(LatLng place) {

        GroundOverlay groundOverlay = mMap.addGroundOverlay(new
                GroundOverlayOptions()
                .position(place, 100)
                .transparency(0.5f)
                .zIndex(3)
                .image(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(getDrawable(R.drawable.map_overlay)))));

        startOverlayAnimation(groundOverlay);
    }

    private void startOverlayAnimation(final GroundOverlay groundOverlay) {

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator vAnimator = ValueAnimator.ofInt(0, 100);
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final Integer val = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(val);

            }
        });

        ValueAnimator tAnimator = ValueAnimator.ofFloat(0, 1);
        tAnimator.setRepeatCount(ValueAnimator.INFINITE);
        tAnimator.setRepeatMode(ValueAnimator.RESTART);
        tAnimator.setInterpolator(new LinearInterpolator());
        tAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                groundOverlay.setTransparency(val);
            }
        });

        animatorSet.setDuration(3000);
        animatorSet.playTogether(vAnimator, tAnimator);
        animatorSet.start();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void alertBox(final Context context, String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setMessage(msg);

            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void callUser(ModelGetEmergencyRidesResponse.RiderList ride, ModelGetEmergencyRidesResponse.Location location) {

        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + ride.getMobileNo()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(call);

        openPlaceCallActivity(ride);
    }

    @Override
    public void navigate(ModelGetEmergencyRidesResponse.RiderList ride, ModelGetEmergencyRidesResponse.Location location) {
        handleStartNavigation(ride, location);
    }

    private void handleStartNavigation(ModelGetEmergencyRidesResponse.RiderList ride, ModelGetEmergencyRidesResponse.Location location) {

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + location.coordinates.get(1) + "," + location.getCoordinates().get(0)));
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMaxZoomPreference(20);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
        mMap.setMyLocationEnabled(true);
        if (checkPermission()) addMarkerAndFillDetails();
    }

    private boolean checkPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Ask for the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            Toast.makeText(this, "Please give location permission", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        try {

            switch (requestCode) {

                case LOCATION_PERMISSION_REQUEST_CODE:

                    if (checkPermission()) {
                        addMarkerAndFillDetails();
                    } else {
                        Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case CALL_PHONE_CODE:

                    if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    } else {
                        Toast.makeText(this, "This app needs phone permission", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPlaceCallActivity(ModelGetEmergencyRidesResponse.RiderList onboard) {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        mainActivity.putExtra("callerId", onboard.get_id());
        mainActivity.putExtra("recepientName", onboard.getUserName());
        mainActivity.putExtra("recepientImage", onboard.getProfile_pic());
        startActivity(mainActivity);
    }
}