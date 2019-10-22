package com.carpool.tagalong.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelCancelOwnRideRequest;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetEstimatedFareRequest;
import com.carpool.tagalong.models.ModelGetEstimatedFareResponse;
import com.carpool.tagalong.models.ModelGetNearbyDriversRequest;
import com.carpool.tagalong.models.ModelGetNearbyDriversResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsRequest;
import com.carpool.tagalong.models.ModelGetUserLocationResponse;
import com.carpool.tagalong.models.ModelQuickRideBookResponse;
import com.carpool.tagalong.models.ModelRateRiderequest;
import com.carpool.tagalong.models.ModelRequestQuickRideRider;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.service.LocationHelper;
import com.carpool.tagalong.utils.LocationAddress;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;
import com.carpool.tagalong.views.DirectionsJSONParser;
import com.carpool.tagalong.views.RegularTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class QuickSearchRideActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, RatingBar.OnRatingBarChangeListener {

    private static final int MY_LOCATION_PERMISSIONS_REQUEST = 108;
    private static final String LOG_TAG = "Google Places";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyChV_kPPwQkFZ_beep4K4y6DEHz9dUKYg4";
    private static final String ANIM_DOWN_SETTINGS = "anim_request_selection";
    private static final int CALL_PHONE_CODE = 235;
    private static String lastHit = "";
    private static Double startLat, startlongt, endLat, endLng = 0.0;
    private static int carry_bag_count = 1;
    private static int mAX_bag_count = 4;
    private static ModelQuickRideBookResponse modelQuickRideBookResponse;
    private static ModelGetCurrentRideResponse modelGetRideDetailsResponse;
    private static boolean pickedUpFlag = false;
    private final String TAG = QuickSearchRideActivity.this.getClass().getSimpleName();
    @BindView(R.id.rootll)
    LinearLayout rootll;
    ArgbEvaluator argbEvaluator;
    ArrayList<LatLng> points;
    MarkerOptions options = null;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private ArrayList<String> placeIdList = null;
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;
    private ArrayList resultList;
    private Context context;
    private ImageView startPin, endPin;
    private LocationHelper locationHelper;
    private Location mLastLocation;
    private AutoCompleteTextView startTrip, endTrip;
    private GooglePlacesAutocompleteAdapterEndTrip googlePlacesAutocompleteAdapterEndTrip;
    private TextWatcher startTextWatcher;
    private RegularTextView carryBagCountTxt;
    private Button searchRide, payAndBookNow, payAndBookNowDisable;
    private RelativeLayout estimatedFareDetailsLayout, bookedCabDetailsLayout, stratTripLyt, endtripLyt;
    private Shimmer shimmer, shimmerEst;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private ShimmerTextView estimatedCost, estimatedTime;
    private RegularTextView sourceLocname, destLocName;
    private Timer getDriverLocationtimer = new Timer();
    private BroadcastReceiver pickedUpListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            showPickedUpDialog(QuickSearchRideActivity.this);
        }
    };
    private Handler locationHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            try {
                locationHelper = LocationHelper.getInstance(context);

                if (locationHelper != null) {
                    mLastLocation = locationHelper.getmLastLocation();
                    getLocation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    });
    private String finalRating;

    private BroadcastReceiver droppedListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            showDroppedDialog(QuickSearchRideActivity.this);
        }
    };

    public static double computeHeading(LatLng from, LatLng to) {

        Double HeadingRotation = SphericalUtil.computeHeading(from, to);
        return HeadingRotation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressDialogLoader.progressDialogCreation(this,getString(R.string.please_wait));

        setContentView(R.layout.quick_search_ride);

        ButterKnife.bind(this);
        context = this;
        initializeViews(); // will do binding later

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(QuickSearchRideActivity.this);

        argbEvaluator = new ArgbEvaluator();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        toolbarLayout = findViewById(R.id.toolbar_quick_search);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Search Ride");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (checkAndRequestPermissions()) {
            if (!Utils.isJobServiceOn(this)) {
                Utils.scheduleApplicationPackageJob(this);
            }
            if (!pickedUpFlag)
                locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CODE);
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("rideId")) {
            getRideDetails(getIntent().getExtras().getString("rideId"));
        }else{
            ProgressDialogLoader.progressDialogDismiss();
        }

        Utils.clearNotifications(context);
    }

    private void initializeViews() {

        startTrip = findViewById(R.id.et_start_trip);
        endTrip = findViewById(R.id.et_end_trip);
        startPin = findViewById(R.id.startPin);
        endPin = findViewById(R.id.endPin);
        searchRide = findViewById(R.id.search_rides);

        payAndBookNow = findViewById(R.id.payBookNow);
        payAndBookNowDisable = findViewById(R.id.payAndBookDisable);
        sourceLocname = findViewById(R.id.start_point_source_name);
        destLocName = findViewById(R.id.end_point_dest_name);
        estimatedCost = findViewById(R.id.estimatedCost);
        estimatedTime = findViewById(R.id.eta);
        payAndBookNow.setOnClickListener(this);
        estimatedFareDetailsLayout = findViewById(R.id.rideSearchResultLyt);
        bookedCabDetailsLayout = findViewById(R.id.bookedCabDriverDetails);
        stratTripLyt = findViewById(R.id.startLayout);
        endtripLyt = findViewById(R.id.endTrip);

        estimatedCost.setText("    ");
        estimatedTime.setText("    ");
        startPin.setOnClickListener(this);
        endPin.setOnClickListener(this);
        startPin.setOnClickListener(this);

        searchRide.setOnClickListener(this);

        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout.list_item);

        startTrip.setAdapter(googlePlacesAutocompleteAdapter);
        startTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startTrip.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.cross), null);
                startPin.setVisibility(View.VISIBLE);
                getLatLongByPlace(placeIdList.get(position), Constants.START_RIDE);
            }
        });

        startTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 1) {
                        mMap.clear();
                        googlePlacesAutocompleteAdapter.getFilter().filter(s.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        startTrip.addTextChangedListener(startTextWatcher);

        googlePlacesAutocompleteAdapterEndTrip = new GooglePlacesAutocompleteAdapterEndTrip(this, R.layout.list_item);

        endTrip.setAdapter(googlePlacesAutocompleteAdapterEndTrip);

        endTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                endPin.setVisibility(View.VISIBLE);
                endTrip.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.cross), null);
                getLatLongByPlace(placeIdList.get(position), Constants.END_RIDE);
            }
        });

        endTrip.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {

                    mMap.clear();
                    estimatedFareDetailsLayout.setVisibility(View.GONE);
                    searchRide.setVisibility(View.VISIBLE);

                    if (s.length() > 1) {
                        googlePlacesAutocompleteAdapterEndTrip.getFilter().filter(s.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleStartPinClick() {

        if (!startTrip.getText().toString().equalsIgnoreCase("")) {
            startTrip.clearComposingText();
            startTrip.setText("");
            startTrip.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_start_ride_point_xhdpi, 0);
        } else {
            locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
        }
    }

    private ArrayList autocomplete(String input) {

        ArrayList resultList = null;
        ArrayList placeIdList = null;

        HttpURLConnection conn = null;

        StringBuilder jsonResults = new StringBuilder();

        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);

            sb.append("?key=" + API_KEY);

            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            Log.d("GOOGLE", url.toString());

            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;

            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {

            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;

        } catch (IOException e) {

            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;

        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results

            JSONObject jsonObj = new JSONObject(jsonResults.toString());

            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results

            resultList = new ArrayList(predsJsonArray.length());
            placeIdList = new ArrayList(predsJsonArray.length());

            for (int i = 0; i < predsJsonArray.length(); i++) {

                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));

                System.out.println("================");

                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                placeIdList.add(predsJsonArray.getJSONObject(i).getString("place_id"));
                this.placeIdList = placeIdList;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    private void getLatLongByPlace(final String placeID, final String flag) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                performPlaceAPIHit(placeID, flag);
                return null;
            }

            @Override
            protected void onPostExecute(Void dummy) {

                mMap.clear();
                addStartMarker();
            }
        }.execute();
    }

    public void parse(JSONObject jObject, String flag) {

        Double lat = Double.valueOf(0);
        Double lng = Double.valueOf(0);

        try {
            lat = (Double) jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lat");
            lng = (Double) jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag.equals(Constants.START_RIDE)) {
            startLat = lat;
            startlongt = lng;
            showNearbyDrivers(lat, lng);

        } else {
            endLat = lat;
            endLng = lng;

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    return null;
                }

                @Override
                protected void onPostExecute(Void dummy) {
                    addEndMarker();
                }
            }.execute();
        }

        if (startLat != null && endLat != null) {
            String url = getDirectionsUrl(new LatLng(startLat, startlongt), new LatLng(endLat, endLng));

            DownloadTask downloadTask = new DownloadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
//            getEstimatedFare();
        }
    }

    private void addEndMarker() {

        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(endLat, endLng))
                    .zoom(12)
                    .build();

//        addOverlay(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(endLat, endLng));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_end_ride_point_xhdpi);
            options.icon(icon);
            mMap.addMarker(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performPlaceAPIHit(String placeID, String flag) {

        HttpURLConnection conn = null;

        StringBuilder jsonResults = new StringBuilder();

        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);

            sb.append("?placeid=" + URLEncoder.encode(placeID, "utf8"));

            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());

            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder

            int read;

            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            parse(jsonObj, flag);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
    }

    private void addAvailableDriversCarMarker(Double lat, Double lont) {

        try {
            double degrees;

            if (mMap != null) {
                mMap.clear();
                addStartMarker();
                addEndMarker();
            }

            if (!pickedUpFlag)
                degrees = computeHeading(new LatLng(lat, lont), new LatLng(startLat, startlongt));
            else
                degrees = computeHeading(new LatLng(lat, lont), new LatLng(endLat, endLng));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lont))
                    .zoom(12)
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            options = new MarkerOptions();
            options.position(new LatLng(lat, lont));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car);
            options.icon(icon);
            options.zIndex(1.0f);
            options.flat(true);
            options.rotation((float) degrees);
            mMap.addMarker(options);

            if (!pickedUpFlag) {

                String url = getDirectionsUrl(new LatLng(lat, lont), new LatLng(startLat, startlongt));
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.startPin:
                handleStartPinClick();
                break;

            case R.id.endPin:
                handleEndPinClick();
                break;

            case R.id.search_rides:
                handleSearchRidesClick();
                break;

            case R.id.payBookNow:
                showPreferencesAlert();
                break;

            case android.R.id.home:
                finish();
                break;
        }
    }

    private void addStartMarker() {

        try {
            mMap.clear();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(startLat, startlongt))
                    .zoom(12)
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(startLat, startlongt));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_ride_point_xhdpi);
            options.icon(icon);
            mMap.addMarker(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
        getDriverLocationtimer.cancel();
        startLat = null;
        startlongt = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void handleSearchRidesClick() {

        if (startTrip.getText().toString().equals("") || endTrip.getText().toString().equals("")) {
            Toast.makeText(context, "Location can't be empty!!", Toast.LENGTH_LONG).show();
            return;
        }
        getEstimatedFare();
    }

    private void handleEndPinClick() {

        if (endPin.getVisibility() == View.VISIBLE) {
            endTrip.clearComposingText();
            endTrip.setText("");
            endPin.setVisibility(View.GONE);
            endTrip.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_end_ride_point_xhdpi, 0);
            destLocName.setText("");
            estimatedCost.setText("   ");
            estimatedTime.setText("   ");
        }
    }

    private void getLocation() {

        if (mLastLocation != null) {

            Double latitude = mLastLocation.getLatitude();
            Double longitude = mLastLocation.getLongitude();

            TagALongPreferenceManager.saveUserLocationLatitude(getApplicationContext(), String.valueOf(latitude));
            TagALongPreferenceManager.saveUserLocationLongitude(getApplicationContext(), String.valueOf(longitude));

            LocationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());

            locationHandler.removeMessages(1);
            startTrip.addTextChangedListener(startTextWatcher);
        } else {
            locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
        }
    }

    private boolean checkAndRequestPermissions() {

        int permissionStorage = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionlocationGPS = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionlocationGPS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_LOCATION_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        try {

            switch (requestCode) {

                case MY_LOCATION_PERMISSIONS_REQUEST:

                    if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    } else {
                        showAlertDialog("This app needs location permission", "Need Location Permission", false, 1);
                    }
                    break;

                case CALL_PHONE_CODE:

                    if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    } else {
                        showAlertDialog("This app needs phone permission", "Need phone call permission", false, 1);
                    }
                    break;
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                //starting saving location in preferences
                if (!Utils.isJobServiceOn(this)) {
                    Utils.scheduleApplicationPackageJob(this);
                }
                locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + modelQuickRideBookResponse.getDriverDetails().getMobileNo()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(call);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String message, String title, boolean cancelable, final int code) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);

        if (code == 1) {
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(QuickSearchRideActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_PERMISSIONS_REQUEST);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        builder.show();
    }

    private void showPreferencesAlert() {

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.alert_ride_preferences_quick_ride, null);
            builder.setView(dialogLayout);
            builder.setCancelable(true);

            final AlertDialog alert = builder.create();
            Button confirm_ride = dialogLayout.findViewById(R.id.confirm_prefs);

            ImageView minus_stepper = dialogLayout.findViewById(R.id.minus_stepper);
            ImageView plus_stepper = dialogLayout.findViewById(R.id.plus_stepper);
            carryBagCountTxt = dialogLayout.findViewById(R.id.carry_bag_count_txt);

            confirm_ride.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alert.cancel();
                    bookCab(false, false, false);
                }
            });

            minus_stepper.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    handleReduceCarryBag();

                }
            });

            plus_stepper.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    handleIncreaseCarryBag();
                }
            });

            alert.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void bookCab(boolean allowkids, boolean smoke, boolean carrybags) {

        try {

            if (Utils.isNetworkAvailable(context)) {

                ModelRequestQuickRideRider modelRequestQuickRideRider = new ModelRequestQuickRideRider();
                modelRequestQuickRideRider.setStartLocation(sourceLocname.getText().toString());
                modelRequestQuickRideRider.setEndLocation(destLocName.getText().toString());
                modelRequestQuickRideRider.setStartLat(startLat);
                modelRequestQuickRideRider.setStartLong(startlongt);
                modelRequestQuickRideRider.setEndLat(endLat);
                modelRequestQuickRideRider.setEndLong(endLng);
                modelRequestQuickRideRider.setRideDateTime(Utils.getCurrentDateTimeAndSet());
//                modelRequestQuickRideRider.setNoOfSeats(Integer.parseInt(carryBagCountTxt.getText().toString()));
                modelRequestQuickRideRider.setNoOfSeats(0);
                modelRequestQuickRideRider.setEstimatedFare(Double.valueOf(estimatedCost.getText().toString().split(" ")[1]));

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "quickRide book request is: " + modelRequestQuickRideRider.toString());

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.bookQuickRide(TagALongPreferenceManager.getToken(context), modelRequestQuickRideRider).enqueue(new Callback<ModelQuickRideBookResponse>() {

                        @Override
                        public void onResponse(Call<ModelQuickRideBookResponse> call, Response<ModelQuickRideBookResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();
                            if (response.body() != null && response.body().getStatus() == 1) {
                                //                                shimmer.cancel();

                                modelQuickRideBookResponse = response.body();

                                handleCabBookData(response.body());
                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelQuickRideBookResponse> call, Throwable t) {

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
        } catch (Exception e) {
            Toast.makeText(context, "Some error occurs!!", Toast.LENGTH_LONG).show();
            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }

    private void handleCabBookData(final ModelQuickRideBookResponse modelQuickRideBookResponse) {

        stratTripLyt.setVisibility(View.GONE);
        endtripLyt.setVisibility(View.GONE);

        bookedCabDetailsLayout.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.quick_ride_cab_details, null);
        CircleImageView imageView = view.findViewById(R.id.driverImage);
        TextView driverRating = view.findViewById(R.id.driverRating);
        TextView driverName = view.findViewById(R.id.drivernameText);
        TextView cabDtls = view.findViewById(R.id.cab_dtls_quick_ride_passenger);
        ImageView callDriver = view.findViewById(R.id.callDriver);
        RegularTextView otp = view.findViewById(R.id.otp_text);

        otp.setText("OTP: " + modelQuickRideBookResponse.getPickupVerificationCode());

        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openPlaceCallActivity(modelQuickRideBookResponse);

//                Intent call = new Intent(Intent.ACTION_CALL);
//                call.setData(Uri.parse("tel:" + modelQuickRideBookResponse.getDriverDetails().getMobileNo()));
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                startActivity(call);
            }
        });

        TextView countPickup = view.findViewById(R.id.countPickup);
        cabDtls.setText(modelQuickRideBookResponse.getDriverDetails().getVehicle() + " Plate no: " + modelQuickRideBookResponse.getDriverDetails().getVehicleNumber());

        if (modelQuickRideBookResponse.getPassengersPickupComingUp() != null) {

            countPickup.setText(modelQuickRideBookResponse.getPassengersPickupComingUp().size() + " pickup coming up");
        }

        Button cancelRide = view.findViewById(R.id.cancelRide);

        cancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCancelAlert(modelQuickRideBookResponse.getRequestId());
            }
        });

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        Glide.with(context)
                .load(modelQuickRideBookResponse.getDriverDetails().getProfile_pic())
                .apply(options)
                .into(imageView);

        driverRating.setText(modelQuickRideBookResponse.getDriverDetails().getRating() + "");
        driverName.setText(modelQuickRideBookResponse.getDriverDetails().getUserName() + "");
        bookedCabDetailsLayout.addView(view);
        animate(ANIM_DOWN_SETTINGS);

        TimerTask getDriverLocationTimerTask = new TimerTask() {

            @Override
            public void run() {

                getDriverLocationAndDraw(modelQuickRideBookResponse.getDriverDetails().get_id());
            }
        };

        getDriverLocationtimer.schedule(getDriverLocationTimerTask, 500, 10 * 1000);
    }

    private void openPlaceCallActivity(ModelQuickRideBookResponse modelQuickRideBookResponse) {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        mainActivity.putExtra("callerId", modelQuickRideBookResponse.getDriverDetails().get_id());
        mainActivity.putExtra("recepientName", modelQuickRideBookResponse.getDriverDetails().getUserName());
        mainActivity.putExtra("recepientImage", modelQuickRideBookResponse.getDriverDetails().getProfile_pic());
        startActivity(mainActivity);
    }

    private void showCancelAlert(String requestId) {
        showCustomCancelRideDialog(this, requestId);
    }

    private void showCustomCancelRideDialog(Activity context, final String requestId) {

        com.carpool.tagalong.views.RegularTextView buttonPositive;
        com.carpool.tagalong.views.RegularTextView buttonNegative;
        AlertDialog alertDialog;
        final EditText reasonText;

        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.cancel_ride_alert_doalog_lyt, null);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(dialogView);

            reasonText = dialogView.findViewById(R.id.cancel_reason_edt);
            buttonNegative = dialogView.findViewById(R.id.no_cancel_ride);
            buttonPositive = dialogView.findViewById(R.id.cancel_ride);
            alertDialog = dialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;
            buttonPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCancelRide(reasonText.getText().toString(), requestId);
                    finalAlertDialog.cancel();
                }
            });

            buttonNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.cancel();
                }
            });

            alertDialog.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleCancelRide(String reason, String requestId) {

        try {

            ModelCancelOwnRideRequest modelCancelOwnRideRequest = new ModelCancelOwnRideRequest();
            modelCancelOwnRideRequest.setRequestId(requestId);
            modelCancelOwnRideRequest.setCancelReason(reason);

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.cancelRide(TagALongPreferenceManager.getToken(context), modelCancelOwnRideRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            finish();
                            Log.e("Cancel Rider Own quick Ride", "FAILURE verification");
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animate(String condition) {

        Animation slideFromBottom = AnimationUtils.loadAnimation(context,
                R.anim.slide_in_bottom);

        switch (condition) {

            case ANIM_DOWN_SETTINGS:

                estimatedFareDetailsLayout.setVisibility(View.GONE);
                bookedCabDetailsLayout.setVisibility(View.VISIBLE);
                bookedCabDetailsLayout.startAnimation(slideFromBottom);
                break;
        }
    }

    private void handleIncreaseCarryBag() {

        if (carry_bag_count >= mAX_bag_count) {
            return;
        }
        carry_bag_count = carry_bag_count + 1;
        carryBagCountTxt.setText(carry_bag_count + "");
    }

    private void handleReduceCarryBag() {

        if (carry_bag_count <= 1) {
            return;
        }
        carry_bag_count = carry_bag_count - 1;
        carryBagCountTxt.setText(carry_bag_count + "");
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + API_KEY;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void showNearbyDrivers(double lat, double lng) {

        try {

            if (Utils.isNetworkAvailable(context)) {

                ModelGetNearbyDriversRequest modelGetNearbyDriversRequest = new ModelGetNearbyDriversRequest();
                modelGetNearbyDriversRequest.setCurrentLat(lat);
                modelGetNearbyDriversRequest.setCurrentLong(lng);

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "Get Nearby driver request  is: " + modelGetNearbyDriversRequest.toString());

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.getNearestDrivers(TagALongPreferenceManager.getToken(context), modelGetNearbyDriversRequest).enqueue(new Callback<ModelGetNearbyDriversResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetNearbyDriversResponse> call, Response<ModelGetNearbyDriversResponse> response) {

                            if (response.body() != null && response.body().getStatus() == 1) {
                                if (response.body().getData() != null) {

                                    List<ModelGetNearbyDriversResponse.Data> datalist = response.body().getData();

                                    if (datalist.size() > 1) {

                                        handleAddCarMarker(datalist);

                                    } else {
                                        addAvailableDriversCarMarker(datalist.get(0).getLocation().getCoordinates().get(1), datalist.get(0).getLocation().getCoordinates().get(0));
                                    }
                                }
                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetNearbyDriversResponse> call, Throwable t) {

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
        } catch (Exception e) {
            Toast.makeText(context, "Some error occurs.Please try again!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void handleAddCarMarker(List<ModelGetNearbyDriversResponse.Data> datalist) {

        for (int i = 0; i < datalist.size(); i++) {
            addAvailableDriversCarMarker(datalist.get(i).getLocation().getCoordinates().get(1), datalist.get(i).getLocation().getCoordinates().get(0));
        }
    }

    private void getEstimatedFare() {

        try {

            if (Utils.isNetworkAvailable(context)) {

                ModelGetEstimatedFareRequest modelGetEstimatedFareRequest = new ModelGetEstimatedFareRequest();
                modelGetEstimatedFareRequest.setStartLat(startLat);
                modelGetEstimatedFareRequest.setStartLong(startlongt);
                modelGetEstimatedFareRequest.setEndLat(endLat);
                modelGetEstimatedFareRequest.setEndLong(endLng);

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "Get estimated fare is: " + modelGetEstimatedFareRequest.toString());

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.getEstimatedFare(TagALongPreferenceManager.getToken(context), modelGetEstimatedFareRequest).enqueue(new Callback<ModelGetEstimatedFareResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetEstimatedFareResponse> call, Response<ModelGetEstimatedFareResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();
                            if (response.body() != null && response.body().getStatus() == 1) {
//                                shimmer.cancel();
                                handleRideData(response.body().getData());
                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetEstimatedFareResponse> call, Throwable t) {
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
        } catch (Exception e) {
            ProgressDialogLoader.progressDialogDismiss();
            Toast.makeText(context, "Some error occurs.Please try again!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void handleRideData(ModelGetEstimatedFareResponse.Data data) {

        if (data != null) {

            estimatedFareDetailsLayout.setVisibility(View.VISIBLE);
            searchRide.setVisibility(View.GONE);
            shimmer = new Shimmer();
            shimmer.start(estimatedCost);

            shimmerEst = new Shimmer();
            shimmerEst.start(estimatedTime);
            payAndBookNow.setVisibility(View.GONE);
            payAndBookNowDisable.setVisibility(View.VISIBLE);

            sourceLocname.setText(startTrip.getText());
            destLocName.setText(endTrip.getText());
            estimatedCost.setText("$ " + data.getEstimatedFare());
            estimatedTime.setText("ETA: " + data.getEstimatedTimeToReachDestination());
            payAndBookNow.setVisibility(View.VISIBLE);
            payAndBookNowDisable.setVisibility(View.GONE);
        }
    }

    private void getDriverLocationAndDraw(String driverId) {

        try {

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "Driver ID is: " + driverId);

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.getUserLocation(TagALongPreferenceManager.getToken(context), driverId).enqueue(new Callback<ModelGetUserLocationResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetUserLocationResponse> call, Response<ModelGetUserLocationResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();
                            if (response.body() != null && response.body().getStatus() == 1) {

                                showDriver(response.body().getCoordinates().get(1), response.body().getCoordinates().get(0));

                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetUserLocationResponse> call, Throwable t) {
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
        } catch (Exception e) {
            ProgressDialogLoader.progressDialogDismiss();
            Toast.makeText(context, "Some error occurs.Please try again!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void showDriver(Double lat, Double longt) {
        addAvailableDriversCarMarker(lat, longt);
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
                                handleCabBookDataNew(response.body().getRideData());
                                modelGetRideDetailsResponse = response.body();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleCabBookDataNew(final ModelGetCurrentRideResponse.RideData rideData) {

        pickedUpFlag = rideData.getStatus() == Constants.PICKUP;

        startLat = rideData.getStartLat();
        startlongt = rideData.getStartLong();

        stratTripLyt.setVisibility(View.GONE);
        endtripLyt.setVisibility(View.GONE);
        searchRide.setVisibility(View.GONE);

        bookedCabDetailsLayout.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.quick_ride_cab_details, null);
        CircleImageView imageView = view.findViewById(R.id.driverImage);
        RegularTextView driverRating = view.findViewById(R.id.driverRating);
        RegularTextView driverName = view.findViewById(R.id.drivernameText);
        RegularTextView cabDtls = view.findViewById(R.id.cab_dtls_quick_ride_passenger);
        ImageView callDriver = view.findViewById(R.id.callDriver);

        RegularTextView otp = view.findViewById(R.id.otp_text);

        if(!pickedUpFlag)
        otp.setText("OTP: " + rideData.getPickupVerificationCode());

        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent call = new Intent(Intent.ACTION_CALL);
//                call.setData(Uri.parse("tel:" + rideData.getDriverDetails().getMobileNo()));
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                startActivity(call);
//
                Intent mainActivity = new Intent(context, PlaceCallActivity.class);
                mainActivity.putExtra("callerId", rideData.getDriverDetails().getUserId());
                mainActivity.putExtra("recepientName", rideData.getDriverDetails().getUserName());
                mainActivity.putExtra("recepientImage", rideData.getDriverDetails().getProfile_pic());
                startActivity(mainActivity);
            }
        });

        TextView countPickup = view.findViewById(R.id.countPickup);
        cabDtls.setText(rideData.getDriverDetails().getVehicle() + " Plate no: " + rideData.getDriverDetails().getVehicleNumber());
        int count = 0;

        if (rideData.getOnBoard() != null) {

            for (int i = 0; i < rideData.getOnBoard().size(); i++) {

                if (rideData.getOnBoard().get(i).getStatus() == Constants.ACCEPTED) {
                    count++;
                }
            }

            countPickup.setText(count + " pickup coming up");
        }

        Button cancelRide = view.findViewById(R.id.cancelRide);

        cancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCancelAlert(rideData.get_id());
            }
        });

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        Glide.with(context)
                .load(rideData.getDriverDetails().getProfile_pic())
                .apply(options)
                .into(imageView);

        driverRating.setText(rideData.getDriverDetails().getRating() + "");
        driverName.setText(rideData.getDriverDetails().getUserName() + "");
        bookedCabDetailsLayout.addView(view);
        animate(ANIM_DOWN_SETTINGS);
        ProgressDialogLoader.progressDialogDismiss();

        TimerTask getDriverLocationTimerTask = new TimerTask() {

            @Override
            public void run() {

                getDriverLocationAndDraw(rideData.getDriverDetails().get_id());
            }
        };
        getDriverLocationtimer.schedule(getDriverLocationTimerTask, 20 * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter(Constants.LAUNCH_CURRENT_RIDE_FRAGMENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(droppedListener,
                new IntentFilter(Constants.DROPPED));

        LocalBroadcastManager.getInstance(this).registerReceiver(pickedUpListener,
                new IntentFilter(Constants.PICKED_UP));
    }

    private void showPickedUpDialog(Activity context) {

        Button okButton;
        final AlertDialog alertDialog;

        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.welcome_onboard, null);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(dialogView);

            alertDialog = dialogBuilder.create();

            okButton = dialogView.findViewById(R.id.ok);

            okButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                    pickedUpFlag = true;
                    finish();
                }
            });

            alertDialog.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void showDroppedDialog(Activity context) {

        Button reviewButton;
        final AlertDialog alertDialog;

        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.ok_review_dialog_layout, null);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(dialogView);

            alertDialog = dialogBuilder.create();
//            final AlertDialog finalAlertDialog = alertDialog;
            reviewButton = dialogView.findViewById(R.id.review_ride);

            reviewButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    pickedUpFlag = false;
                    showSubmitReviewDialog();
                    alertDialog.cancel();
                }
            });

            alertDialog.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void showSubmitReviewDialog() {

        com.carpool.tagalong.views.RegularTextView iv_userName;
        RatingBar ratingBar;
        final EditText feedBackComments;
        Button submitFeedback;
        CircleImageView user_image;
        AlertDialog alertDialog = null;

        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView   = inflater.inflate(R.layout.submit_review_dialog_layout, null);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(dialogView);

            feedBackComments = dialogView.findViewById(R.id.feedback_comments);
            submitFeedback   = dialogView.findViewById(R.id.submitReview);
            user_image  = dialogView.findViewById(R.id.iv_user_profile_image);
            iv_userName = dialogView.findViewById(R.id.tv_driver_name);
            ratingBar   = dialogView.findViewById(R.id.rating_bar);
            ratingBar.setOnRatingBarChangeListener(this);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.avatar_avatar_12)
                    .error(R.drawable.avatar_avatar_12);

            Glide.with(context).load(modelGetRideDetailsResponse.getRideData().getDriverDetails().getProfile_pic()).apply(options).into(user_image);

            iv_userName.setText(modelGetRideDetailsResponse.getRideData().getDriverDetails().getUserName());

            alertDialog = dialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;

            submitFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rateRider(feedBackComments.getText().toString());
                    finalAlertDialog.cancel();
                }
            });

            alertDialog.show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void rateRider(String comments) {

        try {

            ModelRateRiderequest modelRateRiderequest = new ModelRateRiderequest();
            modelRateRiderequest.setRateTo(modelGetRideDetailsResponse.getRideData().getDriverDetails().getUserId());
            modelRateRiderequest.setRideId(modelGetRideDetailsResponse.getRideData().getDriverDetails().get_id());
            modelRateRiderequest.setRating(Double.valueOf(finalRating));
            modelRateRiderequest.setReview(comments);

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.rateRide(TagALongPreferenceManager.getToken(context), modelRateRiderequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                if (response.body().getStatus() == 1) {
                                    finish();
                                } else {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();
                            Toast.makeText(context, "Some error in rating rider!!", Toast.LENGTH_LONG).show();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Rate Driver", "FAILURE Rating driver");
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        finalRating = String.valueOf(rating);

    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint != null) {
                        // Retrieve the autocomplete results.
//                        if (!lastHit.equals(constraint.toString())) {
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    class GooglePlacesAutocompleteAdapterEndTrip extends ArrayAdapter implements Filterable {

        public GooglePlacesAutocompleteAdapterEndTrip(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            try {
                return resultList.get(index).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = null;
                    try {
                        filterResults = new FilterResults();

                        synchronized (filterResults) {

                            if (constraint != null) {

                                // Retrieve the autocomplete results.
                                if (!lastHit.equals(constraint.toString())) {

                                    resultList = autocomplete(constraint.toString());
                                    lastHit = constraint.toString();

                                    // Assign the data to the FilterResults
                                    filterResults.values = resultList;

                                    filterResults.count = resultList.size();
                                }
                            }
                            return filterResults;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private class GeocoderHandler extends Handler {

        @Override
        public void handleMessage(Message message) {

            String locationAddress;

            switch (message.what) {

                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    TagALongPreferenceManager.saveUserAddress(getApplicationContext(), locationAddress);

                    if (startTrip.getText().toString().equalsIgnoreCase("")) {

                        startTrip.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        startTrip.setText(locationAddress);

                        addStartMarker();
                        startTrip.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.cross), null);
                        startPin.setVisibility(View.VISIBLE);
                        startLat = Double.valueOf(TagALongPreferenceManager.getUserLocationLatitude(context));
                        startlongt = Double.valueOf(TagALongPreferenceManager.getUserLocationLongitude(context));
                        mMap.clear();
                        addStartMarker();
                        showNearbyDrivers(startLat, startlongt);
                    }
                    break;
                default:
            }
        }
    }

    private class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            points = new ArrayList<LatLng>();
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(6);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);
                lineOptions.jointType(ROUND);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}