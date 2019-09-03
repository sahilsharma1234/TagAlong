package com.carpool.tagalong.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.QuickRidesRiderAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelCancelOwnRideRequest;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsRequest;
import com.carpool.tagalong.models.ModelPickupRider;
import com.carpool.tagalong.models.ModelRateRiderequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.UIUtils;
import com.carpool.tagalong.utils.Utils;
import com.carpool.tagalong.views.RegularEditText;
import com.google.android.gms.maps.SupportMapFragment;
import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeRoamActivity extends BaseActivity implements View.OnClickListener, QuickRidesRiderAdapter.ridersatusclicklistener, RatingBar.OnRatingBarChangeListener {

    private static final String ANIM_DOWN_SETTINGS = "anim_request_selection";
    private static final int CALL_PHONE_CODE = 265;
    private static boolean isFreeRoamEnabled = false;
    private static Handler handler;
    private static Runnable runnable;
    private static String rideId;
    private final String TAG = FreeRoamActivity.this.getClass().getSimpleName();
    @BindView(R.id.rootll)
    LinearLayout rootll;
    ArgbEvaluator argbEvaluator;
    TextView txt;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private Context context;
    private Button disableFreeRoam;
    private List<ModelGetCurrentRideResponse.OnBoard> currentRiderList = new ArrayList<>();
    private RecyclerView quickRideRidersList;
    private QuickRidesRiderAdapter quickRidesRiderAdapter;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private RelativeLayout riderDtlsLyt;
    private ModelGetCurrentRideResponse.RideData rideData = null;
    private BroadcastReceiver riderListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {

                if (intent.getExtras() != null) {
                    if (intent.getExtras().containsKey("rideId")) {
                        String rideId = intent.getExtras().getString("rideId");
                        if (!rideId.equals("")) {
                            isFreeRoamEnabled = false;
                            disableFreeRoam.setVisibility(View.GONE);
                            getRideDetails(rideId);
                        }
                    }
                }
            }
        }
    };

    private String finalRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressDialogLoader.progressDialogCreation(this,getString(R.string.please_wait));

        setContentView(R.layout.activity_free_roam);

        ButterKnife.bind(this);
        context = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(FreeRoamActivity.this);

        argbEvaluator = new ArgbEvaluator();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        disableFreeRoam = findViewById(R.id.disableFreeRoam);
        disableFreeRoam.setOnClickListener(this);
        toolbarLayout = findViewById(R.id.toolbarFreeRoaming);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);

        ImageView share = toolbarLayout.findViewById(R.id.share);
        riderDtlsLyt = findViewById(R.id.rider_dtls_lyt_roam);
        txt = findViewById(R.id.waiting_txt);
        share.setImageResource(R.drawable.ic_support);
        share.setVisibility(View.VISIBLE);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Free Roam");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (isFreeRoamEnabled) {
            disableFreeRoam.setVisibility(View.VISIBLE);
        } else
            disableFreeRoam.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("launchCurrentRideFragment"));

        LocalBroadcastManager.getInstance(this).registerReceiver(riderListener, new IntentFilter("riderListener"));

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CODE);
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.RIDEID)) {

            isFreeRoamEnabled = false;
            disableFreeRoam.setVisibility(View.GONE);
            rideId = getIntent().getExtras().getString(Constants.RIDEID);
            getRideDetails(rideId);
        } else {
            ProgressDialogLoader.progressDialogDismiss();
            if (!isFreeRoamEnabled) {
                enableFreeRoam();
                if (handler != null && runnable != null)
                    blink();
            }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        try {

            switch (requestCode) {

                case CALL_PHONE_CODE:

                    if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    } else {
                        Toast.makeText(context, "This app needs phone permission", Toast.LENGTH_SHORT).show();
                    }
                    break;
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
                            disableFreeRoam.setVisibility(View.GONE);
                            Toast.makeText(context, "Free roaming disabled", Toast.LENGTH_SHORT).show();
                            finish();
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

        switch (v.getId()) {
            case R.id.disableFreeRoam:
                disableFreeRoam();
                break;
        }
    }

    private void blink() {

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                txt = findViewById(R.id.waiting_txt);
                if (txt.getVisibility() == View.VISIBLE) {
                    txt.setVisibility(View.INVISIBLE);
                } else {
                    txt.setVisibility(View.VISIBLE);
                }
                blink();
            }
        };

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

                    handler.post(runnable);
//
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            txt = findViewById(R.id.waiting_txt);
//                            if (txt.getVisibility() == View.VISIBLE) {
//                                txt.setVisibility(View.INVISIBLE);
//                            } else {
//                                txt.setVisibility(View.VISIBLE);
//                            }
//                            blink();
//                        }
//                    });
                } else {

                    handler.post(runnable);
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            txt = findViewById(R.id.waiting_txt);
//                            if (txt.getVisibility() == View.VISIBLE) {
//                                txt.setVisibility(View.INVISIBLE);
//                            } else {
//                                txt.setVisibility(View.VISIBLE);
//                            }
//                            blink();
//                        }
//                    });
                }
            }
        }).start();
    }

    private void handleRiderDetailsLayout(ModelGetCurrentRideResponse.RideData rideData) {

        txt.setVisibility(View.GONE);
        disableFreeRoam.setVisibility(View.GONE);

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        riderDtlsLyt.removeAllViews();
        this.rideData = rideData;

        LayoutInflater inflater = (LayoutInflater) getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.quick_ride_driver_layout, null);

        quickRideRidersList = view.findViewById(R.id.quick_ride_riders_list);
        ImageView navigateIcon = view.findViewById(R.id.ic_navigate_quickRide);
        View handleIcon = view.findViewById(R.id.ic_navigate_quickRide);

        handleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        navigateIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        try {
            if (rideData != null) {

                if (rideData.getOnBoard() != null && rideData.getOnBoard().size() > 0) {
                    currentRiderList = rideData.getOnBoard();
                    quickRidesRiderAdapter = new QuickRidesRiderAdapter(context, currentRiderList, this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    quickRideRidersList.setLayoutManager(linearLayoutManager);
                    quickRideRidersList.setAdapter(quickRidesRiderAdapter);

                    riderDtlsLyt.addView(view);
                    animate(ANIM_DOWN_SETTINGS);
                    ProgressDialogLoader.progressDialogDismiss();
                } else {
                    UIUtils.alertBox(context, "No Riders in this ride!!");
                }
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

        if (onboard != null) {

            showDialogAlert(onboard);

        }
    }

    @Override
    public void onCallUserClick(ModelGetCurrentRideResponse.OnBoard onboard) {

        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + onboard.getMobileNo()));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(call);
    }

    private void showDialogAlert(final ModelGetCurrentRideResponse.OnBoard onBoard) {

        final Dialog delayDialog = new Dialog(this);
        delayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        delayDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        delayDialog.setContentView(R.layout.accept_ride_dialog_layout_quick_ride);
        delayDialog.setCancelable(true);
        delayDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        HashMap<Integer, String> seatMap = new HashMap<>();
        seatMap.put(1, "One");
        seatMap.put(2, "Two");
        seatMap.put(3, "Three");
        seatMap.put(4, "Four");

        com.carpool.tagalong.views.RegularTextView name = delayDialog.findViewById(R.id.tv_driver_name);
        CircleImageView profilePic = delayDialog.findViewById(R.id.iv_driver_profile_image);

        com.carpool.tagalong.views.RegularTextView source_loc = delayDialog.findViewById(R.id.tv_source_address);
        com.carpool.tagalong.views.RegularTextView dest_loc = delayDialog.findViewById(R.id.tv_dest_address);
        com.carpool.tagalong.views.RegularTextView time = delayDialog.findViewById(R.id.tv_date);
        com.carpool.tagalong.views.RegularTextView fare_amount = delayDialog.findViewById(R.id.tv_payment_amount);
        com.carpool.tagalong.views.RegularTextView tv_message = delayDialog.findViewById(R.id.tv_message);
        com.carpool.tagalong.views.RegularTextView cancel = delayDialog.findViewById(R.id.tv_cancel);
        com.carpool.tagalong.views.RegularTextView driver_address = delayDialog.findViewById(R.id.tv_driver_address);

        final RegularEditText otpView = delayDialog.findViewById(R.id.otp_pickup);
        final SlideToActView slideToActView = delayDialog.findViewById(R.id.tv_slider);

        slideToActView.setVisibility(View.VISIBLE);
        otpView.setVisibility(View.GONE);

        tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);

                if (onBoard != null) {
                    intent.putExtra("receiverId", onBoard.get_id());
                    intent.putExtra("userName", onBoard.getUserName());
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });

        otpView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (otpView.getText().toString().length() < 1) {
                    slideToActView.setVisibility(View.GONE);
                } else {
                    slideToActView.setVisibility(View.VISIBLE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayDialog.dismiss();
                showPickupCancelAlert(onBoard);
            }
        });
        driver_address.setText(onBoard.getAddress());

        Glide.with(context)
                .load(onBoard.getProfile_pic())
                .into(profilePic);

        name.setText(onBoard.getUserName());
        source_loc.setText(onBoard.getStartLocation());
        dest_loc.setText(onBoard.getEndLocation());
        time.setText(onBoard.getRideDateTime());
        fare_amount.setText("$ " + onBoard.getEstimatedFare());

        cancel.setVisibility(View.VISIBLE);
        slideToActView.setVisibility(View.VISIBLE);

        if (onBoard.getStatus() == Constants.PICKUP) {
            slideToActView.setText("SLIDE TO DROP");
            otpView.setVisibility(View.GONE);
        } else if (onBoard.getStatus() == Constants.ACCEPTED) {
            slideToActView.setText("SLIDE TO PICKUP");
            otpView.setVisibility(View.VISIBLE);
            slideToActView.setVisibility(View.GONE);
        }

        delayDialog.show();

        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {

            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {

                if (onBoard.getStatus() == Constants.ACCEPTED) {

                    pickupRider(onBoard, otpView.getText().toString());

                } else if (onBoard.getStatus() == Constants.PICKUP) {
                    dropRider(onBoard);
                }
                delayDialog.dismiss();
            }
        });
    }

    private void showPickupCancelAlert(ModelGetCurrentRideResponse.OnBoard onBoard) {
        showCustomCancelPickupDialog(onBoard);
    }

    private void showCustomCancelPickupDialog(final ModelGetCurrentRideResponse.OnBoard onBoard) {

        com.carpool.tagalong.views.RegularTextView buttonPositive;
        com.carpool.tagalong.views.RegularTextView buttonNegative;
        AlertDialog alertDialog;
        final ModelGetCurrentRideResponse.OnBoard onBoard1 = onBoard;
        final EditText reasonText;

        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.cancel_pickup_alert_doalog_lyt, null);
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
                    handleCancelPickup(onBoard, reasonText.getText().toString());
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

    private void handleCancelPickup(ModelGetCurrentRideResponse.OnBoard onBoard, String reason) {

        try {

            ModelCancelOwnRideRequest modelCancelOwnRideRequest = new ModelCancelOwnRideRequest();

            modelCancelOwnRideRequest.setRequestId(onBoard.get_id());

            modelCancelOwnRideRequest.setCancelReason(reason);

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));
                    restClientRetrofitService.cancelPickup(TagALongPreferenceManager.getToken(context), modelCancelOwnRideRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    if (rideData.getOnBoard() != null && rideData.getOnBoard().size() > 0) {

                                    } else {
                                        finish();
                                    }
                                }
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Accept/Reject Ride", "FAILURE verification");
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

    private void pickupRider(ModelGetCurrentRideResponse.OnBoard onBoard, String otp) {

        try {

            ModelPickupRider modelPickupRider = new ModelPickupRider();

            if (TagALongPreferenceManager.getUserLocationLatitude(this) != null) {
                modelPickupRider.setPickupLat(Double.valueOf(TagALongPreferenceManager.getUserLocationLatitude(this)));
                modelPickupRider.setPickupLong(Double.valueOf(TagALongPreferenceManager.getUserLocationLongitude(this)));
                modelPickupRider.setPickupVerificationCode(otp);
            }

            if (onBoard != null)
                modelPickupRider.setRequestId(onBoard.get_id());
            else
                return;

            modelPickupRider.setRideId(rideData.get_id());
            modelPickupRider.setStartedDate(Utils.getCurrentDateTimeAndSet());

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.pickupRider(TagALongPreferenceManager.getToken(context), modelPickupRider).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                getRideDetails(rideData.get_id());

                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Accept/Reject Ride", "FAILURE verification");
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

    private void dropRider(final ModelGetCurrentRideResponse.OnBoard onBoard) {

        try {

            ModelPickupRider modelPickupRider = new ModelPickupRider();

            if (TagALongPreferenceManager.getUserLocationLatitude(this) != null) {
                modelPickupRider.setDropLat(Double.valueOf(TagALongPreferenceManager.getUserLocationLatitude(this)));
                modelPickupRider.setDropLong(Double.valueOf(TagALongPreferenceManager.getUserLocationLongitude(this)));
            }

            if (onBoard != null)
                modelPickupRider.setRequestId(onBoard.get_id());
            else
                return;

            modelPickupRider.setRideId(rideData.get_id());
            modelPickupRider.setCompletedDate(Utils.getCurrentDateTimeAndSet());

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.dropRider(TagALongPreferenceManager.getToken(context), modelPickupRider).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    showSubmitReviewDialog(onBoard);
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

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Accept/Reject Ride", "FAILURE verification");
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

    private void showSubmitReviewDialog(final ModelGetCurrentRideResponse.OnBoard onBoard) {

        com.carpool.tagalong.views.RegularTextView iv_userName;
        RatingBar ratingBar;
        final EditText feedBackComments;
        Button submitFeedback;
        CircleImageView user_image;

        AlertDialog alertDialog = null;

        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.submit_review_dialog_layout, null);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(dialogView);

            feedBackComments = dialogView.findViewById(R.id.feedback_comments);
            submitFeedback = dialogView.findViewById(R.id.submitReview);
            user_image = dialogView.findViewById(R.id.iv_user_profile_image);
            iv_userName = dialogView.findViewById(R.id.tv_driver_name);
            ratingBar = dialogView.findViewById(R.id.rating_bar);

            ratingBar.setOnRatingBarChangeListener(this);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.avatar_avatar_12)
                    .error(R.drawable.avatar_avatar_12);

            Glide.with(context).load(onBoard.getProfile_pic()).apply(options).into(user_image);

            iv_userName.setText(onBoard.getUserName());

            alertDialog = dialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;

            submitFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rateRider(onBoard, feedBackComments.getText().toString());
                    finalAlertDialog.cancel();
                }
            });

            alertDialog.show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void rateRider(final ModelGetCurrentRideResponse.OnBoard onBoard, String comments) {

        try {

            ModelRateRiderequest modelRateRiderequest = new ModelRateRiderequest();
            modelRateRiderequest.setRateTo(onBoard.getUserId());
            modelRateRiderequest.setRideId(onBoard.get_id());
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

                                    if (rideData.onBoard.size() > 1) {
                                        getRideDetails(rideData.get_id());
                                    } else {
                                        finish();
                                    }
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
                            Log.e("Rate Rider", "FAILURE Rating rider");
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
}