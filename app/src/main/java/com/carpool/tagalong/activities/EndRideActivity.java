package com.carpool.tagalong.activities;

import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelRidePostRequest;
import com.carpool.tagalong.models.ModelRidePostResponse;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndRideActivity extends BaseActivity implements View.OnClickListener {

    private static final String LOG_TAG = "Google Places";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyChV_kPPwQkFZ_beep4K4y6DEHz9dUKYg4";
    private static Double endLat, endLong;
    private static int carry_bag_count = 1;
    private static int mAX_bag_count = 4;
    private final String TAG = EndRideActivity.this.getClass().getSimpleName();
    @BindView(R.id.et_end_trip)
    AutoCompleteTextView endRide;
    ArgbEvaluator argbEvaluator;
    @BindView(R.id.confirm_end_ride)
    Button endRideConfirm;
    com.carpool.tagalong.views.RegularTextView carryBagCountTxt;
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;
    private ArrayList<String> placeIdList = null;
    private Context context;
    private ArrayList resultList;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private String startLocation;
    private String datetime;
    private CheckBox smokeCheckBox, kidsCheckBox, bagsCheckBox;
    private ImageView endPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_ride);

        ButterKnife.bind(this);

        context = this;
        initializeViews();
    }

    private void initializeViews() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        argbEvaluator = new ArgbEvaluator();

        DisplayMetrics displayMetrics   = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout.list_item);

        endRide.setAdapter(googlePlacesAutocompleteAdapter);
        endRide.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                endPin.setVisibility(View.VISIBLE);
                getLatLongByPlace(placeIdList.get(position), Constants.END_RIDE);
            }
        });

        endRide.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 1) {
                    googlePlacesAutocompleteAdapter.getFilter().filter(s.toString());
                }
            }
        });

        toolbarLayout = findViewById(R.id.toolbar_start_ride);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        endPin = findViewById(R.id.endPin);
        endPin.setOnClickListener(this);
        title.setText("End Ride");
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

        endRideConfirm.setOnClickListener(this);

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey(Constants.STARTLOCATION)) {
                startLocation = getIntent().getExtras().get(Constants.STARTLOCATION).toString();
            }
            if (getIntent().getExtras().containsKey(Constants.DATETIME)) {
                datetime = getIntent().getExtras().get(Constants.DATETIME).toString();
            }
        }
    }

    private void handleEndPinClick() {

        if (endPin.getVisibility() == View.VISIBLE) {
            endRide.clearComposingText();
            endRide.setText("");
            endPin.setVisibility(View.GONE);
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
                addMarker();
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
        if (flag.equals(Constants.END_RIDE)) {
            this.endLat = lat;
            this.endLong = lng;
            setDestinationLatLong(new LatLng(lat, lng));
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

            case R.id.confirm_end_ride:
                handleConfirmEndRide();
                break;

            case R.id.minus_stepper:
                handleReduceCarryBag();
                break;

            case R.id.plus_stepper:
                handleIncreaseCarryBag();
                break;

            case R.id.endPin:
                handleEndPinClick();
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

    private void handleConfirmEndRide() {

        showPreferencesAlert();
    }

    private void addMarker() {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(getDestinationLatLong())
                .zoom(17)
                .build();

        addOverlay(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        MarkerOptions options = new MarkerOptions();
        options.position(getDestinationLatLong());
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_end_ride_point_xhdpi);
        options.icon(icon);
        mMap.addMarker(options);
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

            carryBagCountTxt.setText(carry_bag_count+"");

            smokeCheckBox = dialogLayout.findViewById(R.id.smoke_prefe_chck);

            kidsCheckBox = dialogLayout.findViewById(R.id.kids_travelling_chck);

            bagsCheckBox = dialogLayout.findViewById(R.id.carry_bags_pref_chck);

            confirm_ride.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alert.cancel();

                    handleConfirmRideAction();

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

    private void handleConfirmRideAction() {

        try {

            ModelRidePostRequest modelRidePostRequest = new ModelRidePostRequest();
            modelRidePostRequest.setStartLat(getSourceLatLng().latitude);
            modelRidePostRequest.setStartLong(getSourceLatLng().longitude);
            modelRidePostRequest.setEndLat(endLat);
            modelRidePostRequest.setEndLong(endLong);
            modelRidePostRequest.setStartLocation(startLocation);
            modelRidePostRequest.setEndLocation(endRide.getText().toString());
            modelRidePostRequest.setRideDateTime(datetime);
            modelRidePostRequest.setBags(bagsCheckBox.isChecked() ? 1 : 0);
            modelRidePostRequest.setAllowKids(kidsCheckBox.isChecked() ? true : false);
            modelRidePostRequest.setNoOfSeats(carry_bag_count);
            modelRidePostRequest.setDrive(true);
            modelRidePostRequest.setSmoke(smokeCheckBox.isChecked() ? true : false);

            if (Utils.isNetworkAvailable(context)) {

                ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "Ride post driver request is: " + modelRidePostRequest.toString());

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.ridePost(TagALongPreferenceManager.getToken(context), modelRidePostRequest).enqueue(new Callback<ModelRidePostResponse>() {

                        @Override
                        public void onResponse(Call<ModelRidePostResponse> call, Response<ModelRidePostResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null && response.body().getStatus() == 1) {

                                Log.i(TAG, "Ride post Response is: " + response.body().toString());
                                DataManager.setStatus(1);

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent("launchCurrentRideFragment");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                                finish();
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    finishAndRemoveTask();
//                                }
                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelRidePostResponse> call, Throwable t) {
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

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context,textViewResourceId);
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
}