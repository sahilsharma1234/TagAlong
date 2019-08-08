package com.carpool.tagalong.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelGetNearbyDriversRequest;
import com.carpool.tagalong.models.ModelGetNearbyDriversResponse;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.service.LocationHelper;
import com.carpool.tagalong.utils.LocationAddress;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.UIUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class QuickSearchRideActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int MY_LOCATION_PERMISSIONS_REQUEST = 108;
    private static final String LOG_TAG = "Google Places";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyChV_kPPwQkFZ_beep4K4y6DEHz9dUKYg4";
    private static String lastHit = "";
    private static Double startLat, startlongt, endLat, endLng = 0.0;
    private static int carry_bag_count = 1;
    private static int mAX_bag_count = 4;
    private final String TAG = QuickSearchRideActivity.this.getClass().getSimpleName();
    @BindView(R.id.rootll)
    LinearLayout rootll;
    ArgbEvaluator argbEvaluator;
    ArrayList<LatLng> points;
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
    private CheckBox smokeCheckBox, kidsCheckBox, bagsCheckBox;
    private Button searchRide;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }
//        getCurrentDateTimeAndSet();
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("launchCurrentRideFragment"));
    }

    private void initializeViews() {

        startTrip = findViewById(R.id.et_start_trip);
        endTrip = findViewById(R.id.et_end_trip);
        startPin = findViewById(R.id.startPin);
        endPin = findViewById(R.id.endPin);
        searchRide = findViewById(R.id.search_rides);

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
            this.startLat = lat;
            this.startlongt = lng;
            showNearbyDrivers(lat, lng);

        } else {
            this.endLat = lat;
            this.endLng = lng;

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
        }
    }

    private void addEndMarker() {

        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(endLat, endLng))
                    .zoom(10)
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
        }
    }

    private void addStartMarker() {

        try {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(startLat, startlongt))
                    .zoom(10)
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
        showPreferencesAlert();
//        searchRides();
    }

//    @Override
//    public void onClick(View v) {
//
//        int id = v.getId();
//
//        switch (id) {
//
//            case R.id.changeDateTime:
//                break;
//
//            case R.id.startPin:
//                handleStartPinClick();
//                break;
//
//            case R.id.endPin:
//                handleEndPinClick();
//                break;
//
//            case R.id.search_rides:
//                handleSearchRidesClick();
//                break;
//        }
//    }

    private void handleEndPinClick() {

        if (endPin.getVisibility() == View.VISIBLE) {
            endTrip.clearComposingText();
            endTrip.setText("");
            endPin.setVisibility(View.GONE);
            endTrip.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_end_ride_point_xhdpi, 0);
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

    private void searchRides() {

//        try {
//
//            ModelSearchRideRequest modelSearchRideRequest = new ModelSearchRideRequest();
//            modelSearchRideRequest.setStartLat(startLat);
//            modelSearchRideRequest.setStartLong(startlongt);
//            modelSearchRideRequest.setEndLat(endLat);
//            modelSearchRideRequest.setEndLong(endLng);
//            modelSearchRideRequest.setStartLocation(startTrip.getText().toString());
//            modelSearchRideRequest.setEndLocation(endTrip.getText().toString());
//            modelSearchRideRequest.setRideDateTime(Utils.getRidePostDateFromDateString(date.getText().toString()) + " " + Utils.getRideTimeFromDateString(time.getText().toString()));
//            modelSearchRideRequest.setBags(bagsCheckBox.isChecked() ? 1 : 0);
//            modelSearchRideRequest.setAllowKids(kidsCheckBox.isChecked() ? true : false);
//            int seats = Integer.parseInt(seatsCountArray[seatsSpinner.getSelectedItemPosition()]);
//            modelSearchRideRequest.setNoOfSeats(seats);
//
//            DataManager.modelSearchRideRequest = modelSearchRideRequest;
//
//            if (Utils.isNetworkAvailable(context)) {
//
//                progressBarLayout.setVisibility(View.VISIBLE);
//
//                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();
//
//                Log.i(TAG, "Search Ride request is: " + modelSearchRideRequest.toString());
//
//                if (restClientRetrofitService != null) {
//
//                    restClientRetrofitService.searchRide(TagALongPreferenceManager.getToken(context), modelSearchRideRequest).enqueue(new Callback<ModelSearchRideResponse>() {
//
//                        @Override
//                        public void onResponse(Call<ModelSearchRideResponse> call, Response<ModelSearchRideResponse> response) {
//
//                            progressBarLayout.setVisibility(View.GONE);
//
//                            if (response.body() != null && response.body().getStatus() == 1) {
//
//                                Log.i(TAG, "Search Ride Response is: " + response.body().toString());
//
//                                List<ModelSearchRideResponseData> data = response.body().getData();
//
//                                DataManager.modelSearchRideResponseDataLIst = data;
//
//                                Intent intent = new Intent(context, SearchResultActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.putExtra(Constants.DATETIME, txtDate);
//                                startActivity(intent);
//
//                            } else if (response.body() != null && response.body().getStatus() == 0) {
//
//                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ModelSearchRideResponse> call, Throwable t) {
//                            progressBarLayout.setVisibility(View.GONE);
//
//                            if (t != null && t.getMessage() != null) {
//                                t.printStackTrace();
//                            }
//
//                            Toast.makeText(context, "Some error occurred!! Please try again!", Toast.LENGTH_LONG).show();
//                            Log.e(TAG, "FAILURE ");
//                        }
//                    });
//                }
//            } else {
//                Toast.makeText(context, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(context, "Some error occurs.Please try again!!", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
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
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                //starting saving location in preferences
                if (!Utils.isJobServiceOn(this)) {
                    Utils.scheduleApplicationPackageJob(this);
                }
                locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
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
            View dialogLayout = inflater.inflate(R.layout.alert_ride_preferences, null);
            builder.setView(dialogLayout);
            builder.setCancelable(true);

            final AlertDialog alert = builder.create();
            Button confirm_ride = dialogLayout.findViewById(R.id.confirm_prefs);

            ImageView minus_stepper = dialogLayout.findViewById(R.id.minus_stepper);
            ImageView plus_stepper = dialogLayout.findViewById(R.id.plus_stepper);
            carryBagCountTxt = dialogLayout.findViewById(R.id.carry_bag_count_txt);

            smokeCheckBox = dialogLayout.findViewById(R.id.smoke_prefe_chck);

            kidsCheckBox = dialogLayout.findViewById(R.id.kids_travelling_chck);

            bagsCheckBox = dialogLayout.findViewById(R.id.carry_bags_pref_chck);

            confirm_ride.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alert.cancel();
                    UIUtils.alertBox(context, "This is in progress!!!");

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

//                ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "Get Nearby driver request  is: " + modelGetNearbyDriversRequest.toString());

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.getNearestDrivers(TagALongPreferenceManager.getToken(context), modelGetNearbyDriversRequest).enqueue(new Callback<ModelGetNearbyDriversResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetNearbyDriversResponse> call, Response<ModelGetNearbyDriversResponse> response) {

//                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null && response.body().getStatus() == 1) {

                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetNearbyDriversResponse> call, Throwable t) {
//                            ProgressDialogLoader.progressDialogDismiss();

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
//            ProgressDialogLoader.progressDialogDismiss();
            Toast.makeText(context, "Some error occurs.Please try again!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
//                            lastHit = constraint.toString();

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;

                        filterResults.count = resultList.size();
                    }
//                    }
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
                        addStartMarker();
                        showNearbyDrivers(startLat,startlongt);

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
                lineOptions.width(8);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
                lineOptions.jointType(ROUND);
            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}