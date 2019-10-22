package com.carpool.tagalong.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelGetUserLocationResponse;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;
import com.carpool.tagalong.views.DirectionsJSONParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackDriverActivity extends BaseActivity {

    private static final String API_KEY = "AIzaSyChV_kPPwQkFZ_beep4K4y6DEHz9dUKYg4";
    MarkerOptions options = null;
    ArgbEvaluator argbEvaluator;
    Double lat, longt;
    //    ArrayList<LatLng> points;
    private DownloadTask downloadTask;
    private ParserTask parserTask;
    private String driverId;
    private Timer getDriverTimer = new Timer();
    private Marker marker;
    private float v;
    //    private AppCompatImageView sendReportActionButton;
    private BroadcastReceiver cancelledListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_driver);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(TrackDriverActivity.this);

        argbEvaluator = new ArgbEvaluator();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        sendReportActionButton = findViewById(R.id.sendReportActionButton);

        try {
            if (getIntent().getExtras().containsKey("driverId")) {
                driverId = getIntent().getExtras().getString("driverId");
            }

            if (getIntent().getExtras().containsKey("rideLat")) {

                lat = (Double) getIntent().getExtras().get("rideLat");
            }

            if (getIntent().getExtras().containsKey("rideLongt")) {

                longt = (Double) (getIntent().getExtras().get("rideLongt"));
            }

            if (mMap != null)
                addCurrentLocationMarker(lat, longt);

        } catch (Exception e) {
            e.printStackTrace();
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getDriverLocationAndDraw(driverId);
            }
        };

        getDriverTimer.schedule(timerTask, 800, 20 * 1000);

//        sendReportActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TrackDriverActivity.this, SendReportActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(cancelledListener,
                new IntentFilter(Constants.LAUNCH_CURRENT_RIDE_FRAGMENT));
    }

    private void addCurrentLocationMarker(Double lat, Double longt) {

        try {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, longt))
                    .zoom(15)
                    .build();

//            addOverlay(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            options = new MarkerOptions();
            options.position(new LatLng(lat, longt));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_ride_point_xhdpi);
            options.icon(icon);
            mMap.addMarker(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDriverLocationAndDraw(String driverId) {

        try {

            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i("", "Driver ID is: " + driverId);

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.getUserLocation(TagALongPreferenceManager.getToken(this), driverId).enqueue(new Callback<ModelGetUserLocationResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetUserLocationResponse> call, Response<ModelGetUserLocationResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();
                            if (response.body() != null && response.body().getStatus() == 1) {

                                showDriver(response.body().getCoordinates().get(1), response.body().getCoordinates().get(0));

                            } else if (response.body() != null && response.body().getStatus() == 0) {
//                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetUserLocationResponse> call, Throwable t) {
                            ProgressDialogLoader.progressDialogDismiss();
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "Some error occurred!! Please try again!", Toast.LENGTH_LONG).show();
                            Log.e("", "FAILURE ");
                        }
                    });
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ProgressDialogLoader.progressDialogDismiss();
            Toast.makeText(this, "Some error occurs.Please try again!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void showDriver(Double lat, Double longt) {

        addAvailableDriversCarMarker(lat, longt);

    }

    private void addAvailableDriversCarMarker(Double lat, Double lont) {

        try {

            if (mMap != null) {
                mMap.clear();
                addCurrentLocationMarker(this.lat, this.longt);
            }
//
            double degrees = getBearing(new LatLng(lat, lont), new LatLng(this.lat, this.longt));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lont))
                    .zoom(13)
                    .build();

//            addOverlay(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            options = new MarkerOptions();
            options.position(new LatLng(lat, lont));

//            options.position(new LatLng(this.lat, this.longt));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car);
            options.icon(icon);
            options.zIndex(1.0f);
            options.flat(true);
            options.rotation((float) degrees);
            mMap.addMarker(options);

            String url = getDirectionsUrl(new LatLng(lat, lont), new LatLng(this.lat, this.longt));
            // Start downloading json data from Google Directions API
            if (downloadTask != null) {
                downloadTask.cancel(true);
            }

            downloadTask = new DownloadTask();
            downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateCarOnMap(final List<LatLng> latLngs) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        mMap.animateCamera(mCameraUpdate);
//        if (emission == 1) {
        marker = mMap.addMarker(new MarkerOptions().position(latLngs.get(0))
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
//        }
        marker.setPosition(latLngs.get(0));
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                v = valueAnimator.getAnimatedFraction();
                double lng = v * latLngs.get(0).longitude + (1 - v)
                        * latLngs.get(0).longitude;
                double lat = v * latLngs.get(0).latitude + (1 - v)
                        * latLngs.get(0).latitude;
                LatLng newPos = new LatLng(lat, lng);
                marker.setPosition(newPos);
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(getBearing(latLngs.get(0), newPos));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (new CameraPosition.Builder().target(newPos)
                                .zoom(15.5f).build()));
            }
        });
        valueAnimator.start();
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            getDriverTimer.cancel();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(cancelledListener);
        } catch (Exception e) {
            e.printStackTrace();
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

            if (parserTask != null) {
                parserTask.cancel(true);
            }
            parserTask = new ParserTask();
            parserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
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

            drawPolyline(result);

//            points = new ArrayList<LatLng>();
//            PolylineOptions lineOptions = new PolylineOptions();
//            MarkerOptions markerOptions = new MarkerOptions();
//
//            for (int i = 0; i < result.size(); i++) {
//                points = new ArrayList();
//                lineOptions = new PolylineOptions();
//
//                List<HashMap<String, String>> path = result.get(i);
//
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap point = path.get(j);
//                    double lat1 = Double.parseDouble((String) point.get("lat"));
//                    double lng1 = Double.parseDouble((String) point.get("lng"));
//                    LatLng position = new LatLng(lat1, lng1);
//                    points.add(position);
//                }
//
//                lineOptions.addAll(points);
//                lineOptions.width(6);
//                lineOptions.color(Color.BLACK);
//                lineOptions.geodesic(false);
//                lineOptions.jointType(ROUND);
//            }
//
//            // Drawing polyline in the Google Map for the i-th route
//            mMap.addPolyline(lineOptions);
        }
    }
}