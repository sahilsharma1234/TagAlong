package com.carpool.tagalong.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.OnBoardRidersAdapter;
import com.carpool.tagalong.adapter.TimelineAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.Contact;
import com.carpool.tagalong.models.ModelCancelOwnRideRequest;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetTimelineRequest;
import com.carpool.tagalong.models.ModelGetTimelineResponse;
import com.carpool.tagalong.models.ModelRateRiderequest;
import com.carpool.tagalong.models.ModelTagUsers;
import com.carpool.tagalong.models.emergencysos.ModelSendEmergencySOSRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.BitmapUtils;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.UIUtils;
import com.carpool.tagalong.utils.Utils;
import com.carpool.tagalong.views.RegularTextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentRideActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST = 132;
    private static final int IMAGE_PICK_REQUEST = 134;
    private static final int FACEBOOK_SHARE_REQUEST_CODE = 106;
    private static String postPath = "";
    private static ModelGetCurrentRideResponse modelGetRideDetailsResponse;
    private String rideID;
    private LinearLayout uploadPicLytBtn;
    private Button postImage;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private com.carpool.tagalong.views.RegularTextView recent_ride_txt, userName, startLocationName, endLocationName, startRideTime, estimatedCostOfRide;
    private CircleImageView profilePic, postPic;
    private Button cancelButton, requestedBtn;
    private RecyclerView onBoardRecyclerView;
    private RecyclerView timeLineRecView;
    private OnBoardRidersAdapter onBoardRidersAdapter;
    private TimelineAdapter timelineAdapter;
    private List<ModelGetCurrentRideResponse.Timeline> timelineData = new ArrayList<>();
    private ImageView selectImageForPost, selectedImageForPost, shareIcon, emergency_icon;
    private RelativeLayout dropmessage1;
    private Context context;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private ArrayList<String> invitedGuestList;
    private RegularTextView rideDetailsText;

    private BroadcastReceiver listener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_current_ride2);

        toolbarLayout = findViewById(R.id.toolbar_current_ride);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);

        shareIcon = toolbarLayout.findViewById(R.id.share);
        emergency_icon = toolbarLayout.findViewById(R.id.emergency);
        shareIcon.setVisibility(View.VISIBLE);
        emergency_icon.setVisibility(View.GONE);
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

        if (getIntent().getExtras() != null) {
            modelGetRideDetailsResponse = (ModelGetCurrentRideResponse) getIntent().getExtras().getSerializable("data");
            rideID = getIntent().getStringExtra("rideId");

        }
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initializeViews();
        LocalBroadcastManager.getInstance(this).registerReceiver(listener,
                new IntentFilter("endRide"));
    }

    private void initializeViews() {

        context = this;
        recent_ride_txt = findViewById(R.id.ride_timing);
        userName = findViewById(R.id.userName);
        startLocationName = findViewById(R.id.start_point_source_name);
        endLocationName = findViewById(R.id.end_point_dest_name);
        startRideTime = findViewById(R.id.startRideTime);
        estimatedCostOfRide = findViewById(R.id.estimated_cost);
        profilePic = findViewById(R.id.user_image);
        postPic = findViewById(R.id.image_user_1);
        cancelButton = findViewById(R.id.cancel_ride_txt);
        requestedBtn = findViewById(R.id.button_ride);
//        paynow       = findViewById(R.id.button_payNow);
        uploadPicLytBtn = findViewById(R.id.post_image_layout);
        postImage = findViewById(R.id.post_image_btn);
        rideDetailsText = findViewById(R.id.ride_details_text);

//        paynow.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        requestedBtn.setOnClickListener(this);

        onBoardRecyclerView = findViewById(R.id.onBoardRecView);
        timeLineRecView = findViewById(R.id.timeline_recView);
        selectImageForPost = findViewById(R.id.select_image_post);
        selectedImageForPost = findViewById(R.id.selected_image);
//        dropmessage  = findViewById(R.id.drop_message);
        dropmessage1 = findViewById(R.id.drop_message1);

        dropmessage1.setOnClickListener(this);

        shareIcon.setOnClickListener(this);
        emergency_icon.setOnClickListener(this);

        uploadPicLytBtn.setOnClickListener(this);
        postImage.setOnClickListener(this);
        handleCurrentRideForRider();
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

//    private void getRideDetails(final String rideId) {
//
//        try {
//            if (Utils.isNetworkAvailable(context)) {
//
//                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();
//
//                if (restClientRetrofitService != null) {
//
//                    ModelGetRideDetailsRequest modelGetRideDetailsRequest = new ModelGetRideDetailsRequest();
//                    modelGetRideDetailsRequest.setRideId(rideId);
//
//                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));
//
//                    restClientRetrofitService.getRideDetails(TagALongPreferenceManager.getToken(this), modelGetRideDetailsRequest).enqueue(new Callback<ModelGetCurrentRideResponse>() {
//
//                        @Override
//                        public void onResponse(Call<ModelGetCurrentRideResponse> call, Response<ModelGetCurrentRideResponse> response) {
//
//                            ProgressDialogLoader.progressDialogDismiss();
//
//                            if (response.body() != null) {
//
//                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
//                                Log.i(CurrentUpcomingFragment.class.getSimpleName(), "Get rides RESPONSE " + response.body().toString());
//                                handleCurrentRideForRider();
//
//                                modelGetRideDetailsResponse = response.body();
//
//                            } else {
//                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ModelGetCurrentRideResponse> call, Throwable t) {
//                            ProgressDialogLoader.progressDialogDismiss();
//                            if (t != null && t.getMessage() != null) {
//                                t.printStackTrace();
//                            }
//                            Log.e("Get All rides", "FAILURE GETTING ALL RIDES");
//                        }
//                    });
//                }
//            } else {
//                Toast.makeText(this, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            ProgressDialogLoader.progressDialogDismiss();
//        }
//    }

    private void handleCurrentRideForRider() {

        recent_ride_txt.setVisibility(View.GONE);

        ModelGetCurrentRideResponse.DriverDetails driverDetails = modelGetRideDetailsResponse.getRideData().getDriverDetails();

        if (driverDetails != null) {
            String name = driverDetails.getUserName();
            userName.setText(name);
        }

        String startLocName = modelGetRideDetailsResponse.getRideData().getStartLocation();
        String endLocation = modelGetRideDetailsResponse.getRideData().getEndLocation();
        String rideTime = modelGetRideDetailsResponse.getRideData().getRideDateTime();
        String estimatedcost = String.valueOf(modelGetRideDetailsResponse.getRideData().getEstimatedFare());

        startLocationName.setText(startLocName);
        endLocationName.setText(endLocation);
        startRideTime.setText(rideTime);
        estimatedCostOfRide.setText("$" + estimatedcost);

        if (modelGetRideDetailsResponse.getRideData().getStatus() == Constants.REQUESTED) {
            requestedBtn.setVisibility(View.VISIBLE);
            dropmessage1.setVisibility(View.GONE);
        } else if (modelGetRideDetailsResponse.getRideData().getStatus() == Constants.ACCEPTED) {
            requestedBtn.setVisibility(View.GONE);
            dropmessage1.setVisibility(View.VISIBLE);
            rideDetailsText.setText("Ride Details");
//            if (modelGetRideDetailsResponse.getRideData().isPayStatus()) {
//
////                dropmessage.setVisibility(View.GONE);
//                dropmessage1.setVisibility(View.VISIBLE);
////                paynow.setVisibility(View.GONE);
//
//            } else {
//                dropmessage1.setVisibility(View.GONE);
////                dropmessage.setVisibility(View.VISIBLE);
////                paynow.setVisibility(View.VISIBLE);
//            }
        }
        if (modelGetRideDetailsResponse.getRideData().getStatus() == Constants.PICKUP) {
            emergency_icon.setVisibility(View.VISIBLE);
        }

        timelineAdapter = new TimelineAdapter(context, timelineData);
        onBoardRidersAdapter = new OnBoardRidersAdapter(this, modelGetRideDetailsResponse.getRideData().getOnBoard(), null);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        onBoardRecyclerView.setLayoutManager(mLayoutManager);
        onBoardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        onBoardRecyclerView.setAdapter(onBoardRidersAdapter);

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        timeLineRecView.setLayoutManager(mLayoutManager1);
        timeLineRecView.setItemAnimator(new DefaultItemAnimator());
        timeLineRecView.setAdapter(timelineAdapter);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        Glide.with(context)
                .load(modelGetRideDetailsResponse.getRideData().getDriverDetails().getProfile_pic())
                .apply(options)
                .into(profilePic);

        // profile image in the upload timeline section
        Glide.with(context)
                .load(modelGetRideDetailsResponse.getRideData().getProfile_pic())
                .apply(options)
                .into(postPic);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.cancel_ride_txt:
                showCancelAlert();
                break;

            case R.id.add_ride_btn:
//                handleAddRide();
                break;

            case R.id.post_image_layout:
                if (checkAndRequestPermissions()) {
                    uploadPic();
                }
                break;

            case R.id.post_image_btn:
                handlePostImage();
                break;

            case R.id.share:
//                launchContacts();
                handleShareRide();
                break;


            case R.id.emergency:
                handleEmergencySituation();
                break;

            case R.id.drop_message1:
                handleChat();
                break;
        }
    }

    private void handleChat() {

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("receiverId", modelGetRideDetailsResponse.getRideData().getDriverDetails().getUserId());
        intent.putExtra("userName", modelGetRideDetailsResponse.getRideData().getDriverDetails().getUserName());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void handleShareRide() {
        showShareAlert();
    }

    private void handleEmergencySituation() {

        try {

            ModelSendEmergencySOSRequest modelSendEmergencySOSRequest = new ModelSendEmergencySOSRequest();
            modelSendEmergencySOSRequest.setRideId(modelGetRideDetailsResponse.getRideData().getDriverDetails().get_id());

            if (TagALongPreferenceManager.getUserLocationLatitude(this) != null) {
                modelSendEmergencySOSRequest.setLatitude(Double.valueOf(TagALongPreferenceManager.getUserLocationLatitude(this)));
                modelSendEmergencySOSRequest.setLongitude(Double.valueOf(TagALongPreferenceManager.getUserLocationLongitude(this)));
            }

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                Log.e("EMERGENCY REQUEST IS:", "****************************************" + modelSendEmergencySOSRequest.toString());

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
                            Toast.makeText(context, "Failure sending SOS", Toast.LENGTH_LONG).show();
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

    private void launchContacts() {
        Intent intent = new Intent(context, ContactsActivity.class);
        intent.putExtra("from", "event");
        startActivityForResult(intent, Constants.INVITE_GUEST_REQUEST_CODE);
    }

    private void handleGuestInvitation(Intent data) {

        ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");

        if (selectedContacts != null) {

            if (selectedContacts.size() > 0) {

                invitedGuestList = new ArrayList<>(selectedContacts.size());

                if (selectedContacts.size() > 3) {

                    for (int i = 0; i < 3; i++) {
                        try {
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(context);
                            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(selectedContacts.get(i).getPhone(), "");
                            invitedGuestList.add(String.valueOf(numberProto.getNationalNumber()));
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        try {
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(context);
                            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(selectedContacts.get(i).getPhone(), "");
                            invitedGuestList.add(String.valueOf(numberProto.getNationalNumber()));
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                handletagUsers();
            }
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
            ImageView contact  = dialogLayout.findViewById(R.id.contacts);
            facebook.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    shareRideOnFacebook();
                    alert.cancel();
                }
            });

            contact.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    launchContacts();
                    alert.cancel();
                }
            });
            alert.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

//    private void showEmergencyShareAlert() {
//
//        try {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
////            LayoutInflater inflater = getLayoutInflater();
////            View dialogLayout = inflater.inflate(R.layout.submit_review_dialog_layout, null);
////            builder.setView(dialogLayout);
//            builder.setTitle("Share Ride");
//            builder.setMessage("You want to share this ride on:");
//            builder.setCancelable(true);
//
//            final AlertDialog alert = builder.create();
//            alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
//            alert.show();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }

    private void shareRideOnFacebook() {

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.tagalongride.com/"))
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("Hey I am travelling from "+modelGetRideDetailsResponse.getRideData().getStartLocation()+" to "+modelGetRideDetailsResponse.getRideData().getEndLocation()+"!!!! Come and join me!!!! It will be fun!!")
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

    private void uploadPic() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    private boolean checkAndRequestPermissions() {

        int permissionStorage = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            }
            return false;
        }
        return true;
    }

    private void handlePostImage() {
        addPost(postPath);
    }

    private void handlePayNow() {

//        Intent intent = new Intent(context, StripePaymentActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        ModelPaymentRequest modelPaymentRequest = new ModelPaymentRequest();
//        modelPaymentRequest.setAmount(modelGetCurrentRideResponse.getRideData().getEstimatedFare());
//        modelPaymentRequest.setDriverId(modelGetCurrentRideResponse.getRideData().getDriverDetails().getUserId());
//        modelPaymentRequest.setRideId(modelGetCurrentRideResponse.getRideData().getDriverDetails().get_id());
//        modelPaymentRequest.setRequestId(modelGetCurrentRideResponse.getRideData().get_id());
//        intent.putExtra(getString(R.string.order_key), modelPaymentRequest);
//
//        startActivity(intent);
    }

    private void showCancelAlert() {
        showCustomCancelRideDialog(this);
    }

    private void handleCancelRide(String reason) {

        try {

            ModelCancelOwnRideRequest modelCancelOwnRideRequest = new ModelCancelOwnRideRequest();
            modelCancelOwnRideRequest.setRequestId(modelGetRideDetailsResponse.getRideData().get_id());
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
                                    DataManager.setStatus(0);
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
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
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

    private void handletagUsers() {

        try {

            ModelTagUsers modelTagUsers = new ModelTagUsers();
            modelTagUsers.setRequestId(modelGetRideDetailsResponse.getRideData().get_id());
            modelTagUsers.setUserContacts(invitedGuestList);

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {
                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.tagUsers(TagALongPreferenceManager.getToken(context), modelTagUsers).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            ProgressDialogLoader.progressDialogDismiss();
                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
//                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    UIUtils.alertBox(context, response.body().getMessage());
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showCustomCancelRideDialog(Activity context) {

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

    private void addPost(String mediaPath) {

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

                ProgressDialogLoader.progressDialogCreation(this, context.getString(R.string.please_wait));

                restClientRetrofitService.addPost(TagALongPreferenceManager.getToken(context), rideId, type, part).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                        ProgressDialogLoader.progressDialogDismiss();

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        } else if (requestCode == Constants.INVITE_GUEST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            handleGuestInvitation(data);
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
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String message, String title, boolean cancelable, int code) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);

        if (code == 1) {
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(CurrentRideActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSubmitReviewDialog() {

        com.carpool.tagalong.views.RegularTextView iv_userName;
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

            Glide.with(context).load(modelGetRideDetailsResponse.getRideData().getDriverDetails().getProfile_pic()).apply(options).into(user_image);

            iv_userName.setText(modelGetRideDetailsResponse.getRideData().getDriverDetails().getUserName());

            alertDialog = dialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;

            submitFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rateRider(feedBackComments.getText().toString(), rating);
                    finalAlertDialog.cancel();
                }
            });

            alertDialog.show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void rateRider( String comments, float rating) {

        try {

            ModelRateRiderequest modelRateRiderequest = new ModelRateRiderequest();
            modelRateRiderequest.setRateTo(modelGetRideDetailsResponse.getRideData().getDriverDetails().get_id());
            modelRateRiderequest.setRideId(modelGetRideDetailsResponse.getRideData().get_id());
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
}