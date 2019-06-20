package com.carpool.tagalong.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.JoinedRidersAdapter;
import com.carpool.tagalong.adapter.OnBoardRidersAdapter;
import com.carpool.tagalong.adapter.TimelineAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.fragments.CurrentRideFragmentDriver;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelAcceptRideRequest;
import com.carpool.tagalong.models.ModelCancelOwnRideRequest;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetTimelineRequest;
import com.carpool.tagalong.models.ModelGetTimelineResponse;
import com.carpool.tagalong.models.ModelPickupRider;
import com.carpool.tagalong.models.ModelRateRiderequest;
import com.carpool.tagalong.models.ModelStartRideRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.BitmapUtils;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;
import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentRideActivityDriver extends AppCompatActivity implements LocationListener, View.OnClickListener, JoinedRidersAdapter.joinriderlistener, OnBoardRidersAdapter.OnBoardRidersInterface {

    private static final int MY_PERMISSIONS_REQUEST = 132;
    private static final int IMAGE_PICK_REQUEST = 134;
    private static String postPath = "";
    public Location location;
    private LinearLayout uploadPicLytBtn;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private TextView recent_ride_txt, userName, startLocationName, endLocationName, startRideTime, estimatedCostOfRide;
    private CircleImageView profilePic, postPic;
    // TODO: Rename and change types of parameters
    private Context context;
    private Button postImage, button_ride, navigate;
    private CurrentRideFragmentDriver.OnFragmentInteractionListener mListener;
    private ModelGetCurrentRideResponse modelGetRideDetailsResponse;
    private TextView cancelRideDriver, notStarted;
    private RecyclerView joinRequestRecyclerView;
    private RecyclerView onBoardRecyclerView;
    private RecyclerView timeLineRecView;
    private JoinedRidersAdapter joinedRidersAdapter;
    private OnBoardRidersAdapter onBoardRidersAdapter;
    private TimelineAdapter timelineAdapter;
    private ImageView selectImageForPost, selectedImageForPost;
    private List<ModelGetCurrentRideResponse.Timeline> timelineData = new ArrayList<>();
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_ride_demo2);

        toolbarLayout = findViewById(R.id.toolbar_current_ride_driver);
        TextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
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
        timelineData = modelGetRideDetailsResponse.getRideData().getTimeline();

        initializeViews();
        getCurrentLatLong();
    }

    private void initializeViews() {

        context = this;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        postImage = findViewById(R.id.post_image_btn);
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

        uploadPicLytBtn.setOnClickListener(this);
        postImage.setOnClickListener(this);
        handleCurrentRideForDriver();

        uploadPicLytBtn.setOnClickListener(this);
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

        Glide.with(context).load(modelGetRideDetailsResponse.getRideData().getProfile_pic()).apply(options).into(profilePic);
        Glide.with(context).load(modelGetRideDetailsResponse.getRideData().getProfile_pic()).apply(options).into(postPic);
    }

    private void uploadPic() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("image_uri", uri.toString());
            startActivityForResult(intent, Constants.IMAGE_FILTER_CODE);
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
        }
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
//                                    HomeActivity activity = ((HomeActivity) getActivity());
//                                    activity.handleCurrentRideLayoutClick();
                                    getCurrentRide();
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

    private void pickupRider(ModelGetCurrentRideResponse.OnBoard onBoard) {

        try {

            ModelPickupRider modelPickupRider = new ModelPickupRider();
//            Location location = ((HomeActivity) getActivity()).location;
            if (location != null) {
                modelPickupRider.setPickupLat(location.getLatitude());
                modelPickupRider.setPickupLong(location.getLongitude());
            }

            if (onBoard != null)
                modelPickupRider.setRequestId(onBoard.get_id());
            else
                return;

            modelPickupRider.setRideId(modelGetRideDetailsResponse.getRideData().get_id());

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

            if (location != null) {
                modelPickupRider.setDropLat(location.getLatitude());
                modelPickupRider.setDropLong(location.getLongitude());
            }

            if (onBoard != null)
                modelPickupRider.setRequestId(onBoard.get_id());
            else
                return;

            modelPickupRider.setRideId(modelGetRideDetailsResponse.getRideData().get_id());

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

        TextView iv_userName;
        RatingBar ratingBar;
        final EditText feedBackComments;
        Button submitFeedback;
        CircleImageView user_image;
        final float rating;

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

            rating = ratingBar.getRating();

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

                    rateRider(onBoard, feedBackComments.getText().toString(), rating);
                    finalAlertDialog.cancel();
                }
            });

            alertDialog.show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void rateRider(final ModelGetCurrentRideResponse.OnBoard onBoard, String comments, float rating) {

        try {

            ModelRateRiderequest modelRateRiderequest = new ModelRateRiderequest();
            modelRateRiderequest.setRateTo(onBoard.getUserId());
            modelRateRiderequest.setRideId(onBoard.get_id());
            modelRateRiderequest.setRating(Double.valueOf(String.valueOf(rating)));
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

                                if (response.body().getStatus() == 1) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    HomeActivity activity = HomeActivity.getInstance();

                                    if (modelGetRideDetailsResponse.getRideData().onBoard.size() > 1) {
                                        getCurrentRide();
                                    } else {
                                        activity.handleDrawer();
                                        activity.handleHomeLayoutClick();
                                        finish();
                                    }
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

                                    getCurrentRide();
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

                                    getCurrentRide();
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

        if (mediaPath != null) {

            try {
                File file = new File(getPath(Uri.parse(mediaPath)));

                RequestBody type = RequestBody.create(MediaType.parse("text/plain"), Constants.TYPE_IMAGE);

                RequestBody rideId = RequestBody.create(MediaType.parse("text/plain"), modelGetRideDetailsResponse.getRideData().get_id());

                // Create a request body with file and image/video media type

                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/png"), file);
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

                                if (response.body() != null) {

                                    if (response.body().getStatus() == 1) {

                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        selectImageForPost.setVisibility(View.VISIBLE);
                                        selectedImageForPost.setVisibility(View.GONE);
                                        postPath = null;
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
                Toast.makeText(context, "Error uploading Image!! Please try again", Toast.LENGTH_LONG).show();
            }
        }
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
        showDialog(null, onBoardRider);
    }

    private void showDialog(final ModelGetCurrentRideResponse.JoinRequest joinRequest, final ModelGetCurrentRideResponse.OnBoard onBoard) {

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

        TextView name = delayDialog.findViewById(R.id.tv_driver_name);
        CircleImageView profilePic = delayDialog.findViewById(R.id.iv_driver_profile_image);

        TextView source_loc = delayDialog.findViewById(R.id.tv_source_address);
        TextView dest_loc = delayDialog.findViewById(R.id.tv_dest_address);
        TextView time = delayDialog.findViewById(R.id.tv_date);
        TextView fare_amount = delayDialog.findViewById(R.id.tv_payment_amount);
        TextView payment_status = delayDialog.findViewById(R.id.tv_payment_status);
        TextView payment_status_notpaid = delayDialog.findViewById(R.id.tv_payment_status1);

        TextView seats_selected = delayDialog.findViewById(R.id.tv_seat_selected);
        ImageView carrying_bags = delayDialog.findViewById(R.id.tv_bags);
        ImageView kids_allowed = delayDialog.findViewById(R.id.tv_traveling_with_children);

        TextView accept = delayDialog.findViewById(R.id.tv_acept);
        TextView accepted = delayDialog.findViewById(R.id.tv_acepted);
        TextView cancel = delayDialog.findViewById(R.id.tv_cancel);
        TextView reject = delayDialog.findViewById(R.id.tv_Reject);
        TextView driver_address = delayDialog.findViewById(R.id.tv_driver_address);
        final SlideToActView slideToActView = delayDialog.findViewById(R.id.tv_slider);

//        final TextView tv_slide = delayDialog.findViewById(R.id.tv_slide);
//        final ImageView iv_drop_slider = delayDialog.findViewById(R.id.iv_drop_slider);
//        final RelativeLayout ll_slider_parent = delayDialog.findViewById(R.id.ll_slider_parent);

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

            Glide.with(context)
                    .load(joinRequest.getProfile_pic())
                    .into(profilePic);

            name.setText(joinRequest.getUserName());
            source_loc.setText(joinRequest.getStartLocation());
            dest_loc.setText(joinRequest.getEndLocation());
            time.setText(joinRequest.getRideDateTime());
            fare_amount.setText(joinRequest.getEstimatedFare() + "");

            if (!joinRequest.isPayStatus()) {
                payment_status_notpaid.setVisibility(View.VISIBLE);
                payment_status.setVisibility(View.GONE);
            } else {
                payment_status_notpaid.setVisibility(View.GONE);
                payment_status.setVisibility(View.VISIBLE);
            }

            slideToActView.setVisibility(View.GONE);

            if (joinRequest.getStatus() == Constants.ACCEPTED) {

                accept.setVisibility(View.GONE);
                accepted.setVisibility(View.VISIBLE);
                reject.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                slideToActView.setVisibility(View.VISIBLE);
                slideToActView.setText("SLIDE TO PICKUP");

            } else if (joinRequest.getStatus() == Constants.REQUESTED) {
                accept.setVisibility(View.VISIBLE);
                accepted.setVisibility(View.GONE);
                reject.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
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

            Glide.with(context)
                    .load(onBoard.getProfile_pic())
                    .into(profilePic);

            name.setText(onBoard.getUserName());
            source_loc.setText(onBoard.getStartLocation());
            dest_loc.setText(onBoard.getEndLocation());
            time.setText(onBoard.getRideDateTime());
            fare_amount.setText(onBoard.getEstimatedFare() + "");

            if (!onBoard.isPayStatus()) {
                payment_status_notpaid.setVisibility(View.VISIBLE);
                payment_status.setVisibility(View.GONE);
            } else {
                payment_status_notpaid.setVisibility(View.GONE);
                payment_status.setVisibility(View.VISIBLE);
            }

            accept.setVisibility(View.GONE);
            accepted.setVisibility(View.VISIBLE);
            reject.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            slideToActView.setVisibility(View.VISIBLE);

            if (onBoard.getStatus() == Constants.PICKUP) {
                slideToActView.setText("SLIDE TO DROP");
            } else if (onBoard.getStatus() == Constants.ACCEPTED) {
                slideToActView.setText("SLIDE TO PICKUP");
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

                    pickupRider(onBoard);

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

        TextView buttonPositive;
        TextView buttonNegative;
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

        TextView buttonPositive;
        TextView buttonNegative;
        AlertDialog alertDialog = null;
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
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onJoinRiderClick(ModelGetCurrentRideResponse.JoinRequest joinReqModel) {

        if (joinReqModel != null) {
//            Toast.makeText(getActivity(), "Name is: " + joinReqModel.getUserName(), Toast.LENGTH_LONG).show();
            handleJoinRiderClick(joinReqModel);
        }

    }

    private void handleJoinRiderClick(ModelGetCurrentRideResponse.JoinRequest joinReqModel) {

        showDialog(joinReqModel, null);
    }

    public void getCurrentLatLong() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    private void getCurrentRide() {

        try {

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.getCurrentRide(TagALongPreferenceManager.getToken(context)).enqueue(new Callback<ModelGetCurrentRideResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetCurrentRideResponse> call, Response<ModelGetCurrentRideResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                int status = response.body().getStatus();

                                if (status == 1) {

                                    modelGetRideDetailsResponse = response.body();
                                    handleCurrentRideForDriver();
                                } else {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetCurrentRideResponse> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            Log.e("Current Ride", "FAILURE verification");
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
}