package com.carpool.tagalong.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.JoinedRidersAdapter;
import com.carpool.tagalong.adapter.OnBoardRidersAdapter;
import com.carpool.tagalong.adapter.TimelineAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.fragments.CurrentRideFragmentDriver;
import com.carpool.tagalong.fragments.CurrentUpcomingFragment;
import com.carpool.tagalong.glide.GlideApp;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelAcceptRideRequest;
import com.carpool.tagalong.models.ModelCancelOwnRideRequest;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetAccessTokenResponse;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsRequest;
import com.carpool.tagalong.models.ModelGetRiderCreditCardDetails;
import com.carpool.tagalong.models.ModelGetRiderCreditCardDetailsResponse;
import com.carpool.tagalong.models.ModelGetTimelineRequest;
import com.carpool.tagalong.models.ModelGetTimelineResponse;
import com.carpool.tagalong.models.ModelInitializePayementTransaction;
import com.carpool.tagalong.models.ModelPickupRider;
import com.carpool.tagalong.models.ModelRateRiderequest;
import com.carpool.tagalong.models.ModelStartRideRequest;
import com.carpool.tagalong.models.emergencysos.ModelSendEmergencySOSRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.BitmapUtils;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.UIUtils;
import com.carpool.tagalong.utils.Utils;
import com.carpool.tagalong.views.RegularEditText;
import com.carpool.tagalong.views.RegularTextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentRideActivityDriver extends AppCompatActivity implements View.OnClickListener, JoinedRidersAdapter.joinriderlistener, OnBoardRidersAdapter.OnBoardRidersInterface, RatingBar.OnRatingBarChangeListener {

    private static final int MY_PERMISSIONS_REQUEST = 132;
    private static final int IMAGE_PICK_REQUEST = 134;
    private static final int FACEBOOK_SHARE_REQUEST_CODE = 107;
    private static String postPath = "";
    private LinearLayout uploadPicLytBtn;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private RegularTextView recent_ride_txt, userName, startLocationName, endLocationName, startRideTime;
    private CircleImageView profilePic, postPic;
    // TODO: Rename and change types of parameters
    private Context context;
    private Button postImage, button_ride, navigate;
    private CurrentRideFragmentDriver.OnFragmentInteractionListener mListener;
    private ModelGetCurrentRideResponse modelGetRideDetailsResponse;
    private RegularTextView cancelRideDriver, notStarted;
    private RecyclerView joinRequestRecyclerView;
    private RecyclerView onBoardRecyclerView;
    private RecyclerView timeLineRecView;
    private JoinedRidersAdapter joinedRidersAdapter;
    private OnBoardRidersAdapter onBoardRidersAdapter;
    private TimelineAdapter timelineAdapter;
    private ImageView selectImageForPost, selectedImageForPost, shareIcon, emergency_icon;
    private List<ModelGetCurrentRideResponse.Timeline> timelineData = new ArrayList<>();
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private String rideID;
    private TextToSpeech textToSpeech;
    private BroadcastReceiver cancelledListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private String ratingFinal;
    private Charge charge;
    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_ride_demo2);

        toolbarLayout = findViewById(R.id.toolbar_current_ride_driver);
        RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        shareIcon = toolbarLayout.findViewById(R.id.share);
        emergency_icon = toolbarLayout.findViewById(R.id.emergency);
        shareIcon.setVisibility(View.VISIBLE);
        emergency_icon.setVisibility(View.VISIBLE);

        PaystackSdk.setPublicKey(Constants.PAYSTACK_PUBLIC_KEY);

        PaystackSdk.initialize(getApplicationContext());

        title.setText("Current Ride");
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
        modelGetRideDetailsResponse = (ModelGetCurrentRideResponse) getIntent().getExtras().getSerializable("data");
        rideID = getIntent().getStringExtra(Constants.RIDEID);
        timelineData = modelGetRideDetailsResponse.getRideData().getTimeline();

        initializeViews();
        Utils.clearNotifications(context);
    }

    private void initializeViews() {

        context = this;

        uploadPicLytBtn = findViewById(R.id.post_image_layout);
        postImage = findViewById(R.id.post_image_btn);
        recent_ride_txt = findViewById(R.id.ride_timing);
        userName = findViewById(R.id.user_name);
        startLocationName = findViewById(R.id.start_point_dest_name);
        endLocationName = findViewById(R.id.end_point_dest_name);
        startRideTime = findViewById(R.id.ride_time_driver);
        profilePic = findViewById(R.id.image_user);
        postPic = findViewById(R.id.image_user_1);
        cancelRideDriver = findViewById(R.id.cancel_ride_txt);
        cancelRideDriver.setOnClickListener(this);
        uploadPicLytBtn = findViewById(R.id.post_image_layout);
        joinRequestRecyclerView = findViewById(R.id.joinReqRecyView);
        onBoardRecyclerView = findViewById(R.id.onBoardRecView);
        timeLineRecView = findViewById(R.id.timeline_recView);
        selectImageForPost = findViewById(R.id.select_image_post);
        selectedImageForPost = findViewById(R.id.selected_image);
        button_ride = findViewById(R.id.button_ride);
        navigate = findViewById(R.id.start_navigation);
        button_ride.setOnClickListener(this);
        navigate.setOnClickListener(this);
        notStarted = findViewById(R.id.not_started);

        setUIData();

        handleCurrentRideForDriver();

        uploadPicLytBtn.setOnClickListener(this);
        postImage.setOnClickListener(this);

        uploadPicLytBtn.setOnClickListener(this);
        shareIcon.setOnClickListener(this);
        emergency_icon.setOnClickListener(this);

        shareIcon.setVisibility(View.GONE);
        emergency_icon.setVisibility(View.GONE);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    private void setUIData() {

        timelineAdapter = new TimelineAdapter(context, timelineData);
        joinedRidersAdapter = new JoinedRidersAdapter(context, modelGetRideDetailsResponse.getRideData().getJoinRequest(), this);
        onBoardRidersAdapter = new OnBoardRidersAdapter(context, modelGetRideDetailsResponse.getRideData().getOnBoard(), this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        joinRequestRecyclerView.setLayoutManager(mLayoutManager);
        joinRequestRecyclerView.setItemAnimator(new DefaultItemAnimator());
        joinRequestRecyclerView.setAdapter(joinedRidersAdapter);

        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        onBoardRecyclerView.setLayoutManager(mLayoutManager);
        onBoardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        onBoardRecyclerView.setAdapter(onBoardRidersAdapter);

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        timeLineRecView.setLayoutManager(mLayoutManager1);
        timeLineRecView.setItemAnimator(new DefaultItemAnimator());
        timeLineRecView.setAdapter(timelineAdapter);

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
                                Log.i(CurrentUpcomingFragment.class.getSimpleName(), "Get rides RESPONSE " + response.body().toString());
                                modelGetRideDetailsResponse = response.body();
                                setUIData();
                                handleCurrentRideForDriver();

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

    private void handleCurrentRideForDriver() {

        String startLocName = modelGetRideDetailsResponse.getRideData().getStartLocation();
        String endLocation = modelGetRideDetailsResponse.getRideData().getEndLocation();
        String rideTime = modelGetRideDetailsResponse.getRideData().getRideDateTime();
        userName.setText(modelGetRideDetailsResponse.getRideData().getUserName());
        recent_ride_txt.setText("Ride created at " + rideTime);

        if (modelGetRideDetailsResponse.getRideData().getStatus() == Constants.DRIVER_PENDING) {
            button_ride.setVisibility(View.GONE);
            cancelRideDriver.setVisibility(View.VISIBLE);
            notStarted.setVisibility(View.VISIBLE);
            navigate.setVisibility(View.GONE);
        } else if (modelGetRideDetailsResponse.getRideData().getStatus() == Constants.DRIVER_SCHEDULED) {
            button_ride.setVisibility(View.VISIBLE);
            notStarted.setVisibility(View.GONE);
            cancelRideDriver.setVisibility(View.VISIBLE);
            navigate.setVisibility(View.GONE);
        } else if (modelGetRideDetailsResponse.getRideData().getStatus() == Constants.STARTED) {
            notStarted.setVisibility(View.GONE);
            cancelRideDriver.setVisibility(View.GONE);
            navigate.setVisibility(View.VISIBLE);
            button_ride.setVisibility(View.GONE);
        }

        startLocationName.setText(startLocName);
        endLocationName.setText(endLocation);
        startRideTime.setText(rideTime);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        GlideApp.with(context).load(modelGetRideDetailsResponse.getRideData().getProfile_pic()).diskCacheStrategy(DiskCacheStrategy.ALL).apply(options).into(profilePic);
        GlideApp.with(context).load(modelGetRideDetailsResponse.getRideData().getProfile_pic()).diskCacheStrategy(DiskCacheStrategy.ALL).apply(options).into(postPic);
    }

    private void uploadPic() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("video/* image/*");
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    private boolean checkAndRequestPermissions() {

        int permissionStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        try {
            switch (requestCode) {

                case MY_PERMISSIONS_REQUEST:

                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        uploadPic();
                    } else {
                        showAlertDialog("This app needs storage permission", "Need Storage Permission", false, 1);
                    }
                    break;
            }
            if (ActivityCompat.checkSelfPermission(CurrentRideActivityDriver.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String message, String title, boolean cancelable, int code) {

        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentRideActivityDriver.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);

        if (code == 1) {
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(CurrentRideActivityDriver.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            if (uri.toString().toLowerCase().contains("images/media")) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("image_uri", uri.toString());
                startActivityForResult(intent, Constants.IMAGE_FILTER_CODE);
            } else {
                selectImageForPost.setVisibility(View.GONE);
                selectedImageForPost.setVisibility(View.VISIBLE);
                selectedImageForPost.setImageBitmap(getThumbnailFromVideoUri(uri));
                postPath = getPath(uri);
            }

        } else if (requestCode == Constants.IMAGE_FILTER_CODE && resultCode == RESULT_OK && data != null) {

            if (data.getExtras() != null) {

                String path = data.getStringExtra("result_image");
                selectImageForPost.setVisibility(View.GONE);
                selectedImageForPost.setVisibility(View.VISIBLE);
                postPath = path;
                reduceImageAndSet();
            }
        }
    }

    private void reduceImageAndSet() {
        // we'll start with the original picture already open to a file
        try {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(context, Uri.parse(postPath), 100, 100);
            selectedImageForPost.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.cancel_ride_txt:
                showCancelAlert();
                break;

            case R.id.post_image_layout:

                if (checkAndRequestPermissions()) {
                    uploadPic();
                }
                break;

            case R.id.post_image_btn:
                handlePostImage();
                break;

            case R.id.button_ride:
                handleStartRide();
                break;

            case R.id.start_navigation:
                handleStartNavigation();
                break;

            case R.id.share:
                handleShareRide();
                break;

            case R.id.emergency:
                handleEmergencySituation();
                break;
        }
    }

    private void handleShareRide() {
        showShareAlert();
    }

    private void handleEmergencySituation() {

        try {

            ModelSendEmergencySOSRequest modelSendEmergencySOSRequest = new ModelSendEmergencySOSRequest();
            modelSendEmergencySOSRequest.setRideId(modelGetRideDetailsResponse.getRideData().get_id());

            if (TagALongPreferenceManager.getUserLocationLatitude(this) != null) {
                modelSendEmergencySOSRequest.setLatitude(Double.valueOf(TagALongPreferenceManager.getUserLocationLatitude(this)));
                modelSendEmergencySOSRequest.setLongitude(Double.valueOf(TagALongPreferenceManager.getUserLocationLongitude(this)));
            }

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.pressPanicButton(TagALongPreferenceManager.getToken(context), modelSendEmergencySOSRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {

                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    UIUtils.alertBox(context, context.getString(R.string.emergency_send));
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
                            Log.e("Failure sending SOS", "FAILURE verification");
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

    private void showShareAlert() {

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.layout_social_preference_new, null);
            builder.setView(dialogLayout);
            builder.setTitle("Share Ride");
            builder.setMessage("You want to share this ride on:");
            builder.setCancelable(true);

            final AlertDialog alert = builder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            ImageView facebook = dialogLayout.findViewById(R.id.facebookLogo);
            facebook.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    shareRideOnFacebook();
                    alert.cancel();
                }
            });
            alert.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void shareRideOnFacebook() {

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#ConnectTheWorld")
                        .build()).build();

        if (shareDialog == null)

            shareDialog = new ShareDialog(this);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(context, "You shared this post", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "You cancel this post", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        }, FACEBOOK_SHARE_REQUEST_CODE);
        shareDialog.show(content, ShareDialog.Mode.NATIVE);
    }

    private void handleStartNavigation() {

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + modelGetRideDetailsResponse.getRideData().getStartLocation() + "&daddr=" + modelGetRideDetailsResponse.getRideData().getEndLocation()));
        startActivity(intent);
    }

    private void handleStartRide() {

        try {

            ModelStartRideRequest modelStartRideRequest = new ModelStartRideRequest();
            modelStartRideRequest.setRideId(modelGetRideDetailsResponse.getRideData().get_id());
            modelStartRideRequest.setStartedDate(Utils.getCurrentDateTimeAndSet());

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.startRide(TagALongPreferenceManager.getToken(context), modelStartRideRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                if (response.body().getStatus() == 1) {
                                    getRideDetails(rideID);
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
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
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

    private void handlePostImage() {
        addPost(postPath);
    }

    private void showCancelAlert() {
        showCustomCancelRideDialog(this);
    }

    private void handleCancelRide(String reason) {

        try {

            ModelCancelOwnRideRequest modelCancelOwnRideRequest = new ModelCancelOwnRideRequest();
            modelCancelOwnRideRequest.setRideId(modelGetRideDetailsResponse.getRideData().get_id());
            modelCancelOwnRideRequest.setCancelReason(reason);

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.cancelRide(TagALongPreferenceManager.getToken(context), modelCancelOwnRideRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                if (response.body().getStatus() == 1) {
                                    DataManager.setStatus(0);
                                    finish();
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
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
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

    private void pickupRider(final ModelGetCurrentRideResponse.OnBoard onBoard, String otp) {

        try {

            final ModelPickupRider modelPickupRider = new ModelPickupRider();

            if (TagALongPreferenceManager.getUserLocationLatitude(this) != null) {
                modelPickupRider.setPickupLat(Double.valueOf(TagALongPreferenceManager.getUserLocationLatitude(this)));
                modelPickupRider.setPickupLong(Double.valueOf(TagALongPreferenceManager.getUserLocationLongitude(this)));
                modelPickupRider.setPickupVerificationCode(otp);
            }

            if (onBoard != null)
                modelPickupRider.setRequestId(onBoard.get_id());
            else
                return;

            modelPickupRider.setRideId(modelGetRideDetailsResponse.getRideData().get_id());
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

                                if (response.body().getStatus() == 1) {
                                    textToSpeech.speak("Welcome" + onBoard.getUserName() + "in TagAlong Ride. Have a happy journey!!", TextToSpeech.QUEUE_FLUSH, null);
                                    getRideDetails(rideID);
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

            modelPickupRider.setRideId(modelGetRideDetailsResponse.getRideData().get_id());
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
                                    startAFreshCharge(true, onBoard);

//                                    textToSpeech.speak("Thanks" + onBoard.getUserName() + "for using TagAlong Ride.  Have a good time ahead!!", TextToSpeech.QUEUE_FLUSH, null);
//                                    showSubmitReviewDialog(onBoard);
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

        RegularTextView iv_userName;
        final RatingBar ratingBar;
        final EditText feedBackComments;
        Button submitFeedback;
        CircleImageView user_image;
//        final float rating;
        final float[] finalRating = new float[1];

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

            GlideApp.with(context).load(onBoard.getProfile_pic()).diskCacheStrategy(DiskCacheStrategy.ALL).apply(options).into(user_image);

            iv_userName.setText(onBoard.getUserName());

            alertDialog = dialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;

            submitFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rateRider(onBoard, feedBackComments.getText().toString(), String.valueOf(ratingBar.getRating()));
                    finalAlertDialog.cancel();
                }
            });

            alertDialog.show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void rateRider(final ModelGetCurrentRideResponse.OnBoard onBoard, String comments, String rating) {

        try {

            ModelRateRiderequest modelRateRiderequest = new ModelRateRiderequest();
            modelRateRiderequest.setRateTo(onBoard.getUserId());
            modelRateRiderequest.setRideId(onBoard.get_id());
            modelRateRiderequest.setRating(Double.valueOf(ratingFinal));
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

                                    if (modelGetRideDetailsResponse.getRideData().onBoard.size() > 1) {
                                        getRideDetails(rideID);
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

    private void showPickupCancelAlert(ModelGetCurrentRideResponse.JoinRequest joinRequest, ModelGetCurrentRideResponse.OnBoard onBoard) {
        showCustomCancelPickupDialog(joinRequest, onBoard);
    }

    private void handleCancelPickup(ModelGetCurrentRideResponse.JoinRequest joinRequest, ModelGetCurrentRideResponse.OnBoard onBoard, String reason) {

        try {

            ModelCancelOwnRideRequest modelCancelOwnRideRequest = new ModelCancelOwnRideRequest();

            if (joinRequest != null)
                modelCancelOwnRideRequest.setRequestId(joinRequest.get_id());
            else
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
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    getRideDetails(rideID);
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

    private void handleAcceptRejectRide(ModelGetCurrentRideResponse.JoinRequest joinRequest, ModelGetCurrentRideResponse.OnBoard onBoard, boolean status) {

        try {

            ModelAcceptRideRequest modelAcceptRideRequest = new ModelAcceptRideRequest();

            if (joinRequest != null) {
                modelAcceptRideRequest.setRequestId(joinRequest.get_id());
            } else
                modelAcceptRideRequest.setRequestId(onBoard.get_id());

            modelAcceptRideRequest.setRideId(modelGetRideDetailsResponse.getRideData().get_id());
            modelAcceptRideRequest.setAccepted(status);

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.acceptRejectRide(TagALongPreferenceManager.getToken(context), modelAcceptRideRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    getRideDetails(rideID);
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

    private void addPost(String mediaPath) {

        if (!mediaPath.equals("")) {

            try {
                File file = new File(getPath(Uri.parse(mediaPath)));

                RequestBody type = RequestBody.create(MediaType.parse("text/plain"), Constants.TYPE_IMAGE);
                RequestBody rideId = RequestBody.create(MediaType.parse("text/plain"), modelGetRideDetailsResponse.getRideData().get_id());

                RequestBody fileReqBody =
                        RequestBody.create(
                                MediaType.parse(getContentResolver().getType(Uri.parse(mediaPath))),
                                file
                        );
//
//                if(mediatype.equalsIgnoreCase("video")){
//                    fileReqBody = RequestBody.create(MediaType.parse("video/*"), file);
//                }
//                else {
//                    fileReqBody = RequestBody.create(MediaType.parse("image/png"), file);
//                }

                // Create MultipartBody.Part using file request-body,file name and part name
                MultipartBody.Part part = MultipartBody.Part.createFormData("media", file.getName(), fileReqBody);

                if (Utils.isNetworkAvailable(context)) {

                    RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                    if (restClientRetrofitService != null) {

                        ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));
                        restClientRetrofitService.addPost(TagALongPreferenceManager.getToken(context), rideId, type, part).enqueue(new Callback<ModelDocumentStatus>() {

                            @Override
                            public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                                ProgressDialogLoader.progressDialogDismiss();
                                postPath = "";

                                if (response.body() != null) {

                                    if (response.body().getStatus() == 1) {

                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        selectImageForPost.setVisibility(View.VISIBLE);
                                        selectedImageForPost.setVisibility(View.GONE);
                                        getPost();
                                    } else {
                                        Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {
                                getPost();

                                ProgressDialogLoader.progressDialogDismiss();
                                if (t != null && t.getMessage() != null) {
                                    t.printStackTrace();
                                }
                                Log.e("Upload Document", "FAILURE verification");
                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ProgressDialogLoader.progressDialogDismiss();
                Toast.makeText(context, "Error uploading Image!! Please try again!!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Please select a media!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(cancelledListener,
                new IntentFilter(Constants.LAUNCH_CURRENT_RIDE_FRAGMENT));
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void getPost() {

        try {

            postPath = "";
            ModelGetTimelineRequest modelGetTimelineRequest = new ModelGetTimelineRequest();
            modelGetTimelineRequest.setRideId(modelGetRideDetailsResponse.getRideData().get_id());

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.getPost(TagALongPreferenceManager.getToken(context), modelGetTimelineRequest).enqueue(new Callback<ModelGetTimelineResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetTimelineResponse> call, Response<ModelGetTimelineResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    timelineData = response.body().getData();
                                    timelineAdapter = new TimelineAdapter(context, timelineData);
                                    timeLineRecView.setAdapter(timelineAdapter);
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetTimelineResponse> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
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
    public void onBoardRiderClick(ModelGetCurrentRideResponse.OnBoard onBoardRider) {
        showDialogAlert(null, onBoardRider);
    }

    private void showDialogAlert(final ModelGetCurrentRideResponse.JoinRequest joinRequest, final ModelGetCurrentRideResponse.OnBoard onBoard) {

        final Dialog delayDialog = new Dialog(this);
        delayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        delayDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        delayDialog.setContentView(R.layout.accept_ride_dialog_layout);
        delayDialog.setCancelable(true);
        delayDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        HashMap<Integer, String> seatMap = new HashMap<>();
        seatMap.put(1, "One");
        seatMap.put(2, "Two");
        seatMap.put(3, "Three");
        seatMap.put(4, "Four");

        RegularTextView name = delayDialog.findViewById(R.id.tv_driver_name);
        CircleImageView profilePic = delayDialog.findViewById(R.id.iv_driver_profile_image);

        RegularTextView source_loc = delayDialog.findViewById(R.id.tv_source_address);
        RegularTextView dest_loc = delayDialog.findViewById(R.id.tv_dest_address);
        RegularTextView time = delayDialog.findViewById(R.id.tv_date);
        RegularTextView fare_amount = delayDialog.findViewById(R.id.tv_payment_amount);
//        RegularTextView payment_status = delayDialog.findViewById(R.id.tv_payment_status);
//        RegularTextView payment_status_notpaid = delayDialog.findViewById(R.id.tv_payment_status1);

        RegularTextView seats_selected = delayDialog.findViewById(R.id.tv_seat_selected);
        ImageView carrying_bags = delayDialog.findViewById(R.id.tv_bags);
        ImageView kids_allowed = delayDialog.findViewById(R.id.tv_traveling_with_children);

        RegularTextView tv_message = delayDialog.findViewById(R.id.tv_message);

        RegularTextView accept = delayDialog.findViewById(R.id.tv_acept);
        RegularTextView accepted = delayDialog.findViewById(R.id.tv_acepted);
        RegularTextView cancel = delayDialog.findViewById(R.id.tv_cancel);
        RegularTextView reject = delayDialog.findViewById(R.id.tv_Reject);
        RegularTextView driver_address = delayDialog.findViewById(R.id.tv_driver_address);
        RegularTextView rating = delayDialog.findViewById(R.id.tv_driver_Ratings);

        final RegularEditText otpView = delayDialog.findViewById(R.id.otp_pickup);
        final SlideToActView slideToActView = delayDialog.findViewById(R.id.tv_slider);

        slideToActView.setVisibility(View.GONE);
        otpView.setVisibility(View.GONE);

        tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);

                if (joinRequest != null) {
                    intent.putExtra("receiverId", joinRequest.getUserId());
                    intent.putExtra("userName", joinRequest.getUserName());
                } else if (onBoard != null) {
                    intent.putExtra("receiverId", onBoard.getUserId());
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

        if (joinRequest != null) {

            if (joinRequest.getBags() == 1) {
                carrying_bags.setVisibility(View.VISIBLE);
            } else {
                carrying_bags.setVisibility(View.GONE);
            }

            if (joinRequest.isAllowKids()) {

                kids_allowed.setVisibility(View.VISIBLE);
            } else {
                kids_allowed.setVisibility(View.GONE);
            }

            seats_selected.setText(seatMap.get(joinRequest.getNoOfSeats()));
            driver_address.setText(joinRequest.getAddress());

            GlideApp.with(context)
                    .load(joinRequest.getProfile_pic())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profilePic);
            rating.setText(joinRequest.getRating() + "");

            name.setText(joinRequest.getUserName());
            source_loc.setText(joinRequest.getStartLocation());
            dest_loc.setText(joinRequest.getEndLocation());
            time.setText(joinRequest.getRideDateTime());
            fare_amount.setText("$ " + joinRequest.getEstimatedFare());

            slideToActView.setVisibility(View.GONE);

            if (joinRequest.getStatus() == Constants.ACCEPTED) {

                accept.setVisibility(View.GONE);
                accepted.setVisibility(View.VISIBLE);
                reject.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                otpView.setVisibility(View.VISIBLE);
                slideToActView.setVisibility(View.VISIBLE);
                slideToActView.setText("SLIDE TO PICKUP");

            } else if (joinRequest.getStatus() == Constants.REQUESTED) {
                accept.setVisibility(View.VISIBLE);
                accepted.setVisibility(View.GONE);
                reject.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                otpView.setVisibility(View.GONE);
                slideToActView.setVisibility(View.GONE);
            }

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delayDialog.dismiss();
                    handleAcceptRejectRide(joinRequest, onBoard, true);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delayDialog.dismiss();
                    showPickupCancelAlert(joinRequest, onBoard);
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delayDialog.dismiss();
                    handleAcceptRejectRide(joinRequest, onBoard, false);
                }
            });

        } else {

            if (onBoard.getBags() == 1) {
                carrying_bags.setVisibility(View.VISIBLE);
            } else {
                carrying_bags.setVisibility(View.GONE);
            }

            if (onBoard.isAllowKids()) {

                kids_allowed.setVisibility(View.VISIBLE);
            } else {
                kids_allowed.setVisibility(View.GONE);
            }

            seats_selected.setText(seatMap.get(onBoard.getNoOfSeats()));
            driver_address.setText(onBoard.getAddress());

            GlideApp.with(context)
                    .load(onBoard.getProfile_pic())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profilePic);

            rating.setText(onBoard.getRating() + "");

            name.setText(onBoard.getUserName());
            source_loc.setText(onBoard.getStartLocation());
            dest_loc.setText(onBoard.getEndLocation());
            time.setText(onBoard.getRideDateTime());
            fare_amount.setText(onBoard.getEstimatedFare() + "");

            accept.setVisibility(View.GONE);
            accepted.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            slideToActView.setVisibility(View.VISIBLE);

            if (onBoard.getStatus() == Constants.PICKUP) {
                slideToActView.setText("SLIDE TO DROP");
                otpView.setVisibility(View.GONE);
            } else if (onBoard.getStatus() == Constants.ACCEPTED) {
                slideToActView.setText("SLIDE TO PICKUP");
                otpView.setVisibility(View.VISIBLE);
                slideToActView.setVisibility(View.GONE);
            } else if (onBoard.getStatus() == Constants.DROP) {
                slideToActView.setVisibility(View.GONE);
                otpView.setVisibility(View.GONE);
            }

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delayDialog.dismiss();
                    handleAcceptRejectRide(joinRequest, onBoard, true);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delayDialog.dismiss();
                    showPickupCancelAlert(joinRequest, onBoard);
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delayDialog.dismiss();
                    handleAcceptRejectRide(joinRequest, onBoard, false);
                }
            });
        }

        delayDialog.show();

        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {

            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {

                if (slideToActView.getText().toString().equalsIgnoreCase("Slide to pickup")) {

                    pickupRider(onBoard, otpView.getText().toString());

                } else {
                    dropRider(onBoard);
                }
                delayDialog.dismiss();
            }
        });

//        iv_drop_slider.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                int action = event.getAction();
//    /*            switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());*/
//                final int x = (int) event.getRawX();
//                final int y = (int) event.getRawY();
//
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//                    case MotionEvent.ACTION_DOWN:
//                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
//                                view.getLayoutParams();
//
//                        xDelta = x - lParams.leftMargin;
//                        yDelta = y - lParams.topMargin;
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        int width = ll_slider_parent.getWidth() - view.getWidth();
//                        if ((x - xDelta) >= 0 && (x - xDelta) <= width) {
//                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
//                                    .getLayoutParams();
//
//                            if (x - xDelta <= width / 2) {
//                                layoutParams.leftMargin = 0;
//                                layoutParams.topMargin = 0;
//                                layoutParams.rightMargin = 0;
//                                layoutParams.bottomMargin = 0;
//                                view.setLayoutParams(layoutParams);
//
//                            } else if (x - xDelta > width / 2) {
//
//                                layoutParams.leftMargin = width;
//                                layoutParams.topMargin = 0;
//                                layoutParams.rightMargin = 0;
//                                layoutParams.bottomMargin = 0;
//                                view.setLayoutParams(layoutParams);
//                                hitAPI();
//                            }
//                        }
//                        break;
//                    case MotionEvent.ACTION_HOVER_EXIT:
//
//                        Log.e("@@parent wi", "---- Exit");
//
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//
//                        Log.e("@@parent wi", "---- Cancel");
//
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//
//                        int width2 = ll_slider_parent.getWidth() - view.getWidth();
///*                        Log.e("@@parent wi", "----" + width2);
//                        Log.e("@@parent (x - xDelta)", "----" + (x - xDelta));*/
//
//                        if ((x - xDelta) >= 0 && (x - xDelta) <= width2) {
//                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
//                                    .getLayoutParams();
//
//                            Log.e("@@parent x", "---- " + x);
//                            Log.e("@@parent xD", "---- " + xDelta);
////
////                            Log.e("@@parent x-xD", "---- "+(x - xDelta) );
//
//                            Log.e("@@parent x-xD", "---- " + x / 10);
//                            String color = "#00008000";
//                            if (x / 10 < 10) {
//                                color = "#0" + x / 10 + "008000";
//                            } else {
//                                color = "#" + x / 10 + "008000";
//                            }
//
//                            ll_slider_parent.getBackground().setColorFilter(Color.parseColor(color), PorterDuff.Mode.DARKEN);
//
//                            layoutParams.leftMargin = x - xDelta;
//                            layoutParams.topMargin = 0;
//                            layoutParams.rightMargin = 0;
//                            layoutParams.bottomMargin = 0;
//                            view.setLayoutParams(layoutParams);
//                        }
//                        break;
//                }
////                iv_drop_slider.invalidate();
//                return true;
//            }
//        });
    }

    private void showCustomCancelRideDialog(Activity context) {

        com.carpool.tagalong.views.RegularTextView buttonPositive;
        com.carpool.tagalong.views.RegularTextView buttonNegative;
        AlertDialog alertDialog = null;
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
                    handleCancelRide(reasonText.getText().toString());
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

    private void showCustomCancelPickupDialog(ModelGetCurrentRideResponse.JoinRequest joinRequest, final ModelGetCurrentRideResponse.OnBoard onBoard) {

        com.carpool.tagalong.views.RegularTextView buttonPositive;
        com.carpool.tagalong.views.RegularTextView buttonNegative;
        AlertDialog alertDialog;
        final ModelGetCurrentRideResponse.JoinRequest mjoinRequest = joinRequest;
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
                    handleCancelPickup(mjoinRequest, onBoard, reasonText.getText().toString());
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

    @Override
    public void onJoinRiderClick(ModelGetCurrentRideResponse.JoinRequest joinReqModel) {

        if (joinReqModel != null) {
            handleJoinRiderClick(joinReqModel);
        }
    }

    private void handleJoinRiderClick(ModelGetCurrentRideResponse.JoinRequest joinReqModel) {

        showDialogAlert(joinReqModel, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cancelledListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cancelledListener);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        ratingFinal = String.valueOf(rating);
    }

    private Bitmap getThumbnailFromVideoUri(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND);
        return bitmap;
    }

    private void startAFreshCharge(boolean local, ModelGetCurrentRideResponse.OnBoard onBoard) {
        // initialize the charge
        charge = new Charge();

        ProgressDialogLoader.progressDialogCreation(this, "Performing payment!! Please wait...");

        if (local) {
            // Set transaction params directly in app (note that these params
            // are only used if an access_code is not set. In debug mode,
            // setting them after setting an access code would throw an exception

            charge.setCard(loadCardFromForm());
            charge.setAmount(100);
            charge.setEmail(DataManager.getModelUserProfileData().getEmail());
            charge.setReference("ChargedFromAndroid_" + Calendar.getInstance().getTimeInMillis());
            try {
                charge.putCustomField("Charged From", "Android SDK");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chargeCard(onBoard);
        } else {

            loadCardFromForm(onBoard);
            // Perform transaction/initialize on our server to get an access code
            // documentation: https://developers.paystack.co/reference#initialize-a-transaction
//            new fetchAccessCodeFromServer().execute(backend_url + "/new-access-code");
        }
    }

    private void initializePaymentTransaction(final ModelGetCurrentRideResponse.OnBoard onBoard) {

        try {

            ModelInitializePayementTransaction modelInitializePayementTransaction = new ModelInitializePayementTransaction();
            modelInitializePayementTransaction.setAmount(Integer.valueOf(String.valueOf(onBoard.getEstimatedFare())));
            modelInitializePayementTransaction.setEmail(onBoard.getEmail());
            modelInitializePayementTransaction.setReference(onBoard.getUserId() + "_" + Calendar.getInstance().getTimeInMillis());

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

//                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.initializeTransaction(TagALongPreferenceManager.getToken(context), modelInitializePayementTransaction).enqueue(new Callback<ModelGetAccessTokenResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetAccessTokenResponse> call, Response<ModelGetAccessTokenResponse> response) {

//                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    chargeCard(onBoard);
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetAccessTokenResponse> call, Throwable t) {

//                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
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

    private Card loadCardFromForm() {
        //validate fields
        Card card;

        String cardNum = "507850785078507812";
        String sMonth = "05";
        String cvc = "081";

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        //update the cvc field of the card
        card.setCvc(cvc);

        //validate expiry month;
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = "2026";
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

        return card;
    }

    private void loadCardFromForm(final ModelGetCurrentRideResponse.OnBoard onBoard) {

        try {

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.getRiderCreditCard(TagALongPreferenceManager.getToken(context), onBoard.getUserId()).enqueue(new Callback<ModelGetRiderCreditCardDetails>() {

                        @Override
                        public void onResponse(Call<ModelGetRiderCreditCardDetails> call, Response<ModelGetRiderCreditCardDetails> response) {

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    prepareCard(response.body().getData(), onBoard);
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetRiderCreditCardDetails> call, Throwable t) {

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
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

    private void prepareCard(ModelGetRiderCreditCardDetailsResponse data, ModelGetCurrentRideResponse.OnBoard onBoard) {

        //build card object with ONLY the number, update the other fields later
        //validate fields
        Card card;

        String cardNum = data.getNumber().trim();

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        card.setCvc("202");

        //validate expiry month;
        String sMonth = data.getExp_month();
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = data.getExp_year();
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

        charge.setCard(card);

        initializePaymentTransaction(onBoard);

    }

    private void chargeCard(final ModelGetCurrentRideResponse.OnBoard onBoard) {

        transaction = null;
        PaystackSdk.chargeCard(CurrentRideActivityDriver.this, charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {

                CurrentRideActivityDriver.this.transaction = transaction;
//                ProgressDialogLoader.progressDialogDismiss();

                Toast.makeText(context, transaction.getReference(), Toast.LENGTH_LONG).show();
                textToSpeech.speak("Thanks" + onBoard.getUserName() + "for using TagAlong Ride.  Have a good time ahead!!", TextToSpeech.QUEUE_FLUSH, null);
                showSubmitReviewDialog(onBoard);
//                updateTextViews();
//                new verifyOnServer().execute(transaction.getReference());
                verfifyTransaction(transaction.getReference());
            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(Transaction transaction) {

                CurrentRideActivityDriver.this.transaction = transaction;
                Toast.makeText(context, transaction.getReference(), Toast.LENGTH_LONG).show();
//                updateTextViews();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                CurrentRideActivityDriver.this.transaction = transaction;
                if (error instanceof ExpiredAccessCodeException) {
//                    startAFreshCharge(false);
//                    MainActivity.this.chargeCard();
                    return;
                }

                ProgressDialogLoader.progressDialogDismiss();

                if (transaction.getReference() != null) {
                    Toast.makeText(context, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                    mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getReference(), error.getClass().getSimpleName(), error.getMessage()));
//                    new verifyOnServer().execute(transaction.getReference());
                    verfifyTransaction(transaction.getReference());
                } else {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
//                    mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                }
//                updateTextViews();
            }
        });
    }

    private void verfifyTransaction(String referenceNumber) {

        try {

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.verifyTransaction(TagALongPreferenceManager.getToken(context), referenceNumber).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    Toast.makeText(context, "Payment is successful and verified!!!", Toast.LENGTH_LONG).show();
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
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
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
}