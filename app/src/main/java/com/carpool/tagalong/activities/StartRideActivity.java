package com.carpool.tagalong.activities;

import android.animation.ArgbEvaluator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
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
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StartRideActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final String LOG_TAG = "Google Places";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyChV_kPPwQkFZ_beep4K4y6DEHz9dUKYg4";
    private static Double startLat, startlongt;
    private final String TAG = StartRideActivity.this.getClass().getSimpleName();
    @BindView(R.id.rootll)
    LinearLayout rootll;
    ArgbEvaluator argbEvaluator;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private ArrayList<String> placeIdList = null;
    private AutoCompleteTextView startTrip;
    private RelativeLayout startTime;
    private com.carpool.tagalong.views.RegularTextView changeDateTime;
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;
    private ArrayList resultList;
    private Context context;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageView startPin;
    @BindView(R.id.trip_start_time)
    com.carpool.tagalong.views.RegularTextView trip_start_time;
    @BindView(R.id.confirm_start_ride)
    Button confirm;
    private String txtDate, txtTime;
    private String finalFormattedDate, finalFormattedTime;

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_ride);

        ButterKnife.bind(this);
        context = this;
        initializeViews(); // will do binding later

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(StartRideActivity.this);

        argbEvaluator = new ArgbEvaluator();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        toolbarLayout = findViewById(R.id.toolbar_start_ride);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Start Ride");
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
        getCurrentDateTimeAndSet();
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("launchCurrentRideFragment"));
    }

    private void initializeViews() {

        startTrip = findViewById(R.id.et_start_trip);
        startTime = findViewById(R.id.et_start_time);
        changeDateTime = findViewById(R.id.change_start_time);
        startPin = findViewById(R.id.startPin);
        startPin.setOnClickListener(this);

        changeDateTime.setOnClickListener(this);
        confirm.setOnClickListener(this);

        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout.list_item);

        startTrip.setAdapter(googlePlacesAutocompleteAdapter);
        startTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startPin.setVisibility(View.VISIBLE);
                getLatLongByPlace(placeIdList.get(position), Constants.START_RIDE);
            }
        });

        startTrip.addTextChangedListener(new TextWatcher() {

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
    }

    private void handleStartPinClick() {

        if (startPin.getVisibility() == View.VISIBLE) {
            startTrip.clearComposingText();
            startTrip.setText("");
            startPin.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setUpPolyLine() {
//        LatLng source = new LatLng(getUserLocation().getLatitude(), getUserLocation().getLongitude());
//        LatLng destination = getDestinationLatLong();
//        if (source != null && destination != null) {
//
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("https://maps.googleapis.com/maps/api/directions/")
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            getPolyline polyline = retrofit.create(getPolyline.class);
//
//            polyline.getPolylineData(source.latitude + "," + source.longitude, destination.latitude + "," + destination.longitude)
//                    .enqueue(new Callback<JsonObject>() {
//                        @Override
//                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//
//                            JsonObject gson = new JsonParser().parse(response.body().toString()).getAsJsonObject();
//                            try {
//
//                                Single.just(parse(new JSONObject(gson.toString())))
//                                        .subscribeOn(Schedulers.io())
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(new Consumer<List<List<HashMap<String, String>>>>() {
//                                            @Override
//                                            public void accept(List<List<HashMap<String, String>>> lists) throws Exception {
//
//                                                drawPolyline(lists);
//                                            }
//                                        });
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<JsonObject> call, Throwable t) {
//
//                        }
//                    });
//        } else
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
        if (flag.equals(Constants.START_RIDE)) {
            this.startLat = lat;
            this.startlongt = lng;
            setSourceLatLong(new LatLng(lat, lng));
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

            case R.id.change_start_time:
                handleDateTimePickerAction();
                break;

            case R.id.confirm_start_ride:
                handleConfirmStartRide();
                break;

            case R.id.startPin:
                handleStartPinClick();
                break;
        }
    }

    private void handleConfirmStartRide() {

        if(startTrip.getText().equals("") || getSourceLatLng() == null){
            Toast.makeText(context,"Please enter start location", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(context, EndRideActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.DATETIME, finalFormattedDate +" "+finalFormattedTime);
        intent.putExtra(Constants.STARTLOCATION, startTrip.getText().toString());
        startActivity(intent);
    }

    private void handleDateTimePickerAction() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis() - 1000);

//        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//
//                new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year,
//                                          int monthOfYear, int dayOfMonth) {
//
//                        view.setMinDate(System.currentTimeMillis() -1000);
//                        monthOfYear = monthOfYear + 1;
//                        if (monthOfYear < 10) {
//                            txtDate = (dayOfMonth + "/" + "0" + monthOfYear + "/" + year);
//                        } else
//                            txtDate = (dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//
//                        Utils.getDateFromDateString(txtDate);
//                        if (!txtDate.equals("")) {
//                            handleTimePicker();
//                        }
//                    }
//                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month = month + 1;
        if (month < 10) {
            txtDate = (dayOfMonth + "/" + "0" + month + "/" + year);
        } else
            txtDate = (dayOfMonth + "/" + (month + 1) + "/" + year);

        Utils.getDateFromDateString(txtDate);
        if (!txtDate.equals("")) {
            handleTimePicker();
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

    private void getCurrentDateTimeAndSet() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mMonth = mMonth + 1;

        if (mMonth < 10) {
            txtDate = (mDay + "/" + "0" + mMonth + "/" + mYear);
        } else
            txtDate = (mDay + "/" + (mMonth + 1) + "/" + mYear);

        onTimeSet1(mHour, mMinute);

        finalFormattedDate = Utils.getRidePostDateFromDateString(txtDate);
    }

    private void handleTimePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        onTimeSet1(hourOfDay, minute);
                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    public void onTimeSet1(int hourOfDay, int minute) {

        String am_pm = "";

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        txtTime = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";

//        date.setText(txtDate);
        if (minute < 10) {
            String s = 0 + "" + minute;
            trip_start_time.setText(txtDate+" "+txtTime + ":" + s + " " + am_pm);
        } else {
            trip_start_time.setText(txtDate+" "+txtTime + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);
        }
//        trip_start_time.setText(txtDate +" "+ txtTime);
        finalFormattedTime = Utils.getRideTimeFromDateString(txtTime + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);
    }

    private void addMarker() {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(getSourceLatLng())
                .zoom(17)
                .build();

        addOverlay(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        MarkerOptions options = new MarkerOptions();
        options.position(getSourceLatLng());
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_ride_point_xhdpi);
        options.icon(icon);
        mMap.addMarker(options);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }
}