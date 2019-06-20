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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelGetDriverProfileResponse;
import com.carpool.tagalong.models.ModelViewAllRidesDriverRequest;
import com.carpool.tagalong.models.ModelViewAllRidesDriverResponse;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carpool.tagalong.managers.DataManager.modelSearchRideRequest;

public class ViewAllRidesDriverActivity extends AppCompatActivity {

    private String TAG = ViewAllRidesDriverActivity.this.getClass().getSimpleName();
    private RecyclerView driverRidesRecyclerView;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private Context context;
    private String driverId;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_rides_driver);
        initView();
    }

    private void initView() {

        context = this;
        toolbarLayout = findViewById(R.id.toolbar_driverprofile);
        TextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Driver");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        driverRidesRecyclerView = findViewById(R.id.driverViewRidesRecyclerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(listener,
                new IntentFilter("launchCurrentRideFragment"));

        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("driverId")){

            driverId = getIntent().getExtras().getString("driverId");
        }

        getAllRidesOfDriver();
    }

    private void getAllRidesOfDriver() {

        try {
            if (Utils.isNetworkAvailable(this)) {

                ModelViewAllRidesDriverRequest modelViewAllRidesDriverRequest = new ModelViewAllRidesDriverRequest();
                modelViewAllRidesDriverRequest.setUserId(driverId);

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.i(TAG, "Request Get Driver All rides is: " + modelSearchRideRequest.toString());

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getResources().getString(R.string.please_wait));

                    restClientRetrofitService.viewAllRidesOfDriver(TagALongPreferenceManager.getToken(context), modelViewAllRidesDriverRequest).enqueue(new Callback<ModelViewAllRidesDriverResponse>() {

                        @Override
                        public void onResponse(Call<ModelViewAllRidesDriverResponse> call, Response<ModelViewAllRidesDriverResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null && response.body().getStatus() == 1) {

                                Log.i(TAG, "Response Driver Get All rides is: " + response.body().toString());

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
//                                handleResponse(response.body().getData());

                            } else if (response.body() != null && response.body().getStatus() == 0) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelViewAllRidesDriverResponse> call, Throwable t) {

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
    }
}