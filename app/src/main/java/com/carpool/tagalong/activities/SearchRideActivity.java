package com.carpool.tagalong.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelSearchRideRequest;
import com.carpool.tagalong.models.ModelSearchRideResponse;
import com.carpool.tagalong.models.ModelSearchRideResponseData;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.service.LocationHelper;
import com.carpool.tagalong.utils.LocationAddress;
import com.carpool.tagalong.utils.UIUtils;
import com.carpool.tagalong.utils.Utils;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRideActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private static final int MY_LOCATION_PERMISSIONS_REQUEST = 108;
    private static final String LOG_TAG = "Google Places";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyChV_kPPwQkFZ_beep4K4y6DEHz9dUKYg4";
    private static String lastHit = "";
    private static Double startLat, startlongt, endLat, endLng;
    private final String TAG = SearchRideActivity.this.getClass().getSimpleName();
    private Toolbar toolbar;
    private CheckBox nowCheckBox, laterCheckBox, bagsCheckBox, kidsCheckBox;
    private RelativeLayout laterDateLyt, laterTimeLyt;
    private AutoCompleteTextView startTrip, endTrip;
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;
    private GooglePlacesAutocompleteAdapterEndTrip googlePlacesAutocompleteAdapterEndTrip;
    private ArrayList<String> placeIdList = null;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String txtDate, txtTime;
    private com.carpool.tagalong.views.RegularTextView chengeDateTimeTxtview, date, time;
    private LocationManager locationManager;
    private ImageView startPin, endPin;
    private Spinner seatsSpinner;
    private com.carpool.tagalong.views.RegularTextView seatSelected;
    private LinearLayout toolbarLayout;
    private Button searchBtn;
    private String[] seatsCountArray = new String[]{"1", "2", "3", "4"};
    private ArrayList resultList;
    private RelativeLayout progressBarLayout;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private Context context;
    private TextWatcher startTextWatcher;
    private LocationHelper locationHelper;
    private Location mLastLocation;
    private String finalFormattedTime,finalFormattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride);
        context = this;

        if (checkAndRequestPermissions()) {
            if (!Utils.isJobServiceOn(this)) {
                Utils.scheduleApplicationPackageJob(this);
            }
            locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
        }

        initializeViews();
        setToolBar();
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("launchCurrentRideFragment"));
    }

    private void setToolBar() {

        toolbarLayout = findViewById(R.id.toolbar_searchride);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Ride Search");
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

    private void initializeViews() {

        nowCheckBox = findViewById(R.id.nowCheckBox);
        laterCheckBox = findViewById(R.id.laterCheckBox);
        laterDateLyt  = findViewById(R.id.dateLayout);
        laterTimeLyt  = findViewById(R.id.timeLyt);
        startTrip = findViewById(R.id.et_start_trip);
        endTrip   = findViewById(R.id.et_end_trip);
        chengeDateTimeTxtview = findViewById(R.id.changeDateTime);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        startPin = findViewById(R.id.startPin);
        endPin = findViewById(R.id.endPin);
        searchBtn = findViewById(R.id.search_rides);
        progressBarLayout = findViewById(R.id.lytBarSearchride);
        bagsCheckBox = findViewById(R.id.bags_carrying);
        kidsCheckBox = findViewById(R.id.kids_travelling);
        seatsSpinner = findViewById(R.id.seats_spinner);
        seatSelected = findViewById(R.id.seat_count);
        seatSelected.setOnClickListener(this);
        chengeDateTimeTxtview.setOnClickListener(this);
        startPin.setOnClickListener(this);
        endPin.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        nowCheckBox.setChecked(true);
        chengeDateTimeTxtview.setVisibility(View.GONE);
        getCurrentDateTimeAndSet();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        nowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    chengeDateTimeTxtview.setVisibility(View.GONE);
                    laterCheckBox.setChecked(false);

                    getCurrentDateTimeAndSet();
                } else {
                    laterCheckBox.setChecked(true);
                }
            }
        });

        laterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    chengeDateTimeTxtview.setVisibility(View.VISIBLE);
                    nowCheckBox.setChecked(false);
                } else {
                    nowCheckBox.setChecked(true
                    );
                }
            }
        });

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

        ArrayAdapter<String> selectedSeatsAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        seatsCountArray); //selected item will look like a spinner set from XML
        selectedSeatsAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        seatsSpinner.setAdapter(selectedSeatsAdapter);
        seatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                seatSelected.setText(selectedItem);

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {
                AdapterView<?> selectedItem = parent;
            }
        });
    }

    private void getCurrentDateTimeAndSet() {

        final Calendar c = Calendar.getInstance();
        mYear  = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay   = c.get(Calendar.DAY_OF_MONTH);
        mHour  = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mMonth  = mMonth + 1;

        if (mMonth < 10) {
            txtDate = (mDay + "/" + "0" + mMonth + "/" + mYear);
        } else
            txtDate = (mDay + "/" + (mMonth + 1) + "/" + mYear);

        finalFormattedDate = Utils.getRidePostDateFromDateString(txtDate);

        onTimeSet1(mHour, mMinute);
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        String str = (String) parent.getItemAtPosition(position);
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
//        getLatLongByPlace(placeIdList.get(position));
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ArrayList autocomplete(String input) {

        ArrayList resultList = null;
        ArrayList placeIdList = null;

        HttpURLConnection conn = null;

        StringBuilder jsonResults = new StringBuilder();

        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);

            sb.append("?key=" + API_KEY);

//            sb.append("&components=country:in");

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

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.changeDateTime:
                handleDateTimePickerAction();
                break;

            case R.id.startPin:
                handleStartPinClick();
                break;

            case R.id.endPin:
                handleEndPinClick();
                break;

            case R.id.seat_count:
                handleSeatCountClick();
                break;

            case R.id.search_rides:
                handleSearchRidesClick();
                break;
        }
    }

    private void handleSearchRidesClick() {

        if (startTrip.getText().toString().equals("") || endTrip.getText().toString().equals("")) {
            Toast.makeText(context, "Location can't be empty!!", Toast.LENGTH_LONG).show();
            return;
        }
        searchRides();
    }

    private void handleSeatCountClick() {
        seatsSpinner.setEnabled(true);
        seatsSpinner.performClick();
    }

    private void handleEndPinClick() {

        if (endPin.getVisibility() == View.VISIBLE) {
            endTrip.clearComposingText();
            endTrip.setText("");
            endPin.setVisibility(View.GONE);
            endTrip.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_end_ride_point_xhdpi, 0);
        }
    }

    private void handleStartPinClick() {

        if (!startTrip.getText().toString().equalsIgnoreCase("")) {

            startTrip.clearComposingText();
            startTrip.setText("");
//            startPin.setVisibility(View.GONE);
            startTrip.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_start_ride_point_xhdpi, 0);
        }else{
            locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
        }
    }

    private void handleDateTimePickerAction() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerDialog,this, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis() - 1000);

//        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//
//                new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year,
//                                          int monthOfYear, int dayOfMonth) {
//
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

        if(txtTime.length() < 2){
            txtTime = "0"+txtTime;
        }

        date.setText(txtDate);
        if (minute < 10) {
            String s = 0 + "" + minute;
            time.setText(txtTime + ":" + s + ":"+"00"+ " " + am_pm);
            finalFormattedTime = Utils.getRideTimeFromDateString(txtDate+" "+txtTime + ":" + s+ ":" + "00"+" "+am_pm);
        } else {
            time.setText(txtTime + ":" + datetime.get(Calendar.MINUTE) + ":"+ "00"+ " " + am_pm);
            finalFormattedTime = Utils.getRideTimeFromDateString(txtDate+" "+txtTime + ":" + datetime.get(Calendar.MINUTE)+ ":" + "00" +" "+am_pm);
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
                    ActivityCompat.requestPermissions(SearchRideActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_PERMISSIONS_REQUEST);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
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
            }
        }.execute();
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

    private void searchRides() {

        try {

            ModelSearchRideRequest modelSearchRideRequest = new ModelSearchRideRequest();
            modelSearchRideRequest.setStartLat(startLat);
            modelSearchRideRequest.setStartLong(startlongt);
            modelSearchRideRequest.setEndLat(endLat);
            modelSearchRideRequest.setEndLong(endLng);
            modelSearchRideRequest.setStartLocation(startTrip.getText().toString());
            modelSearchRideRequest.setEndLocation(endTrip.getText().toString());
            modelSearchRideRequest.setRideDateTime(finalFormattedDate + " " +finalFormattedTime);
            modelSearchRideRequest.setBags(bagsCheckBox.isChecked() ? 1 : 0);
            modelSearchRideRequest.setAllowKids(kidsCheckBox.isChecked() ? true : false);
            int seats = Integer.parseInt(seatsCountArray[seatsSpinner.getSelectedItemPosition()]);
            modelSearchRideRequest.setNoOfSeats(seats);

            DataManager.modelSearchRideRequest = modelSearchRideRequest;

            if (Utils.isNetworkAvailable(context)) {

                progressBarLayout.setVisibility(View.VISIBLE);

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "Search Ride request is: " + modelSearchRideRequest.toString());

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.searchRide(TagALongPreferenceManager.getToken(context), modelSearchRideRequest).enqueue(new Callback<ModelSearchRideResponse>() {

                        @Override
                        public void onResponse(Call<ModelSearchRideResponse> call, Response<ModelSearchRideResponse> response) {

                            progressBarLayout.setVisibility(View.GONE);

                            if (response.body() != null && response.body().getStatus() == 1) {

                                Log.i(TAG, "Search Ride Response is: " + response.body().toString());

                                List<ModelSearchRideResponseData> data = response.body().getData();

                                DataManager.modelSearchRideResponseDataLIst = data;

                                Intent intent = new Intent(context, SearchResultActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra(Constants.DATETIME, txtDate);
                                startActivity(intent);

                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                UIUtils.alertBox(context,response.body().getMessage());
                            } else {
                                UIUtils.alertBox(context,response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelSearchRideResponse> call, Throwable t) {
                            progressBarLayout.setVisibility(View.GONE);

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
        } else {
            this.endLat = lat;
            this.endLng = lng;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month = month + 1;
        if (month < 10) {
            txtDate = (dayOfMonth + "/" + "0" + month + "/" + year);
        } else
            txtDate = (dayOfMonth + "/" + month  + "/" + year);

        finalFormattedDate = Utils.getRidePostDateFromDateString(txtDate);

//        Utils.getDateFromDateString(txtDate);
        if (!txtDate.equals("")) {
            handleTimePicker();
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
                                    lastHit    = constraint.toString();

                                    // Assign the data to the FilterResults
                                    filterResults.values = resultList;
                                    filterResults.count  = resultList.size();
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
                        Log.e("PLace", "Tried to invalidate");
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
                        startTrip.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.cross), null);
                        startPin.setVisibility(View.VISIBLE);
                        startLat   = Double.valueOf(TagALongPreferenceManager.getUserLocationLatitude(context));
                        startlongt = Double.valueOf(TagALongPreferenceManager.getUserLocationLongitude(context));
                    }
                    break;
                default:
            }
        }
    }

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

    private void getLocation() {

        if (mLastLocation != null) {

            Double latitude  = mLastLocation.getLatitude();
            Double longitude = mLastLocation.getLongitude();

            TagALongPreferenceManager.saveUserLocationLatitude(getApplicationContext(), String.valueOf(latitude));
            TagALongPreferenceManager.saveUserLocationLongitude(getApplicationContext(), String.valueOf(longitude));

            LocationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());

            locationHandler.removeMessages(1);
            startTrip.addTextChangedListener(startTextWatcher);
        }
        else{
            locationHandler.sendMessage(Message.obtain(locationHandler, 1, null));
        }
    }
}