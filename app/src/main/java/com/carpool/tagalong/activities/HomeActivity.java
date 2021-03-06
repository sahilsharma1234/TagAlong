package com.carpool.tagalong.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.fragments.AboutUsFragment;
import com.carpool.tagalong.fragments.CurrentRideFragment;
import com.carpool.tagalong.fragments.CurrentUpcomingFragment;
import com.carpool.tagalong.fragments.EmergencyRidesFragment;
import com.carpool.tagalong.fragments.HelpAndSupportFragment;
import com.carpool.tagalong.fragments.HomeFragment;
import com.carpool.tagalong.fragments.ProfileFragment;
import com.carpool.tagalong.fragments.RecentRidesFragment;
import com.carpool.tagalong.fragments.TimelineFragment;
import com.carpool.tagalong.listeners.AlertDialogPermissionBoxClickInterface;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelLogoutResponse;
import com.carpool.tagalong.models.ModelSignInResponseData;
import com.carpool.tagalong.models.ModelUserProfile;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.service.SinchService;
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
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivityCalling implements SinchService.StartFailedListener, View.OnClickListener, HomeFragment.OnFragmentInteractionListener, TimelineFragment.OnFragmentInteractionListener, CurrentRideFragment.OnFragmentInteractionListener, RecentRidesFragment.OnFragmentInteractionListener
        , ProfileFragment.OnFragmentInteractionListener {

    private static final int FACEBOOK_SHARE_REQUEST_CODE = 106;
    private static final int LOCATION_REQUEST_CODE = 178;
    public static SinchClient sinchClient;
    private static HomeActivity homeActivity;
    boolean doubleBackToExitPressedOnce = false;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout homeLayout, recentRidesLayout, currentRideLayout, profileLayout, hepSupportLyt, aboutUsLyt, rides_emergency_lyt, nameLayout;
    private Fragment fragment;
    private RegularTextView userName, address;
    private Button logoutButton;
    private Activity context;
    private CircleImageView userImage;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleDrawer();
            handleCurrentAndUpcomingRideLayoutClick();
        }
    };

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static HomeActivity getInstance() {

        if (homeActivity == null) {
            homeActivity = new HomeActivity();
        }

        return homeActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;
        if (checkAndRequestPermissions()) {
            if (!Utils.isJobServiceOn(this)) {
                Utils.scheduleApplicationPackageJob(this);
            }
        }

        toolbarLayout = findViewById(R.id.toolbar_home);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        homeLayout = headerView.findViewById(R.id.home_layout);
        recentRidesLayout = headerView.findViewById(R.id.recent_ride_layout);
        currentRideLayout = headerView.findViewById(R.id.current_ride_layout);
        nameLayout = headerView.findViewById(R.id.linear);
//        filterLayout     =  headerView.findViewById(R.id.filter);
        profileLayout = headerView.findViewById(R.id.profile_layout);
        userName = headerView.findViewById(R.id.user_name);
        address = headerView.findViewById(R.id.user_address);
        logoutButton = headerView.findViewById(R.id.log_out);
        hepSupportLyt = headerView.findViewById(R.id.help_support_layout);
        aboutUsLyt = headerView.findViewById(R.id.about_us);
        userImage = headerView.findViewById(R.id.user_image);
        rides_emergency_lyt = headerView.findViewById(R.id.emergency_rides_lyt);

        drawer = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                try {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.avatar_avatar_12)
                            .error(R.drawable.avatar_avatar_12);

                    if (DataManager.getModelUserProfileData() != null)
                        Glide.with(context).load(DataManager.getModelUserProfileData().getProfile_pic()).apply(options).into(userImage);
                    else
                        Glide.with(context).load(TagALongPreferenceManager.getDeviceProfile(context).getProfile_pic()).apply(options).into(userImage);

                    hideKeyboard(HomeActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        if (TagALongPreferenceManager.getDeviceProfile(this) != null) {
            userName.setText(TagALongPreferenceManager.getDeviceProfile(context).getUserName());
            address.setText(TagALongPreferenceManager.getDeviceProfile(context).getAddress());

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.avatar_avatar_12)
                    .error(R.drawable.avatar_avatar_12);

            Glide.with(context).load(TagALongPreferenceManager.getDeviceProfile(this).getProfile_pic()).apply(options).into(userImage);
        }

        nameLayout.setOnClickListener(this);
        homeLayout.setOnClickListener(this);
        recentRidesLayout.setOnClickListener(this);
        currentRideLayout.setOnClickListener(this);
        profileLayout.setOnClickListener(this);
        hepSupportLyt.setOnClickListener(this);
        aboutUsLyt.setOnClickListener(this);
        rides_emergency_lyt.setOnClickListener(this);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogOutAction();
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(listener,
                new IntentFilter(Constants.LAUNCH_CURRENT_RIDE_FRAGMENT));

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey(Constants.START_RIDE)) {

                if (getIntent().getExtras().getString(Constants.START_RIDE).equalsIgnoreCase("Current Ride")) {

                } else {
                    Toast.makeText(context, getIntent().getExtras().getString(Constants.START_RIDE), Toast.LENGTH_SHORT).show();
                }
                handleCurrentAndUpcomingRideLayoutClick();
                return;
            }
        } else {
            fragment = HomeFragment.newInstance();
            RegularTextView homeText = homeLayout.findViewById(R.id.homeTextView);
            Drawable img = getResources().getDrawable(R.drawable.ic_home_off_sidebar_xxhdpi);
            homeText.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            homeText.setTextColor(getResources().getColor(R.color.black));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.flContent, fragment);
            ft.commit();
        }

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(context);
        Utils.clearNotifications(context);
    }

    private void registerSinchClient(ModelSignInResponseData deviceProfile) {

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(deviceProfile.get_id());
        }
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        registerSinchClient(TagALongPreferenceManager.getDeviceProfile(context));
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        getUserProfile();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DataManager.getYearsList() == null) {
            Utils.getYearsList(context);
        }

        if (DataManager.getColorList() == null) {
            Utils.getColorList(context);
        }
    }

   /* private void getCurrentRide() {

        try {

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.getCurrentRide(TagALongPreferenceManager.getToken(context)).enqueue(new Callback<ModelGetCurrentRideResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetCurrentRideResponse> call, Response<ModelGetCurrentRideResponse> response) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                DataManager.status = response.body().getStatus();

                            } else {
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetCurrentRideResponse> call, Throwable t) {

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
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
    }*/

    @Override
    public void onClick(View v) {

        int id = v.getId();

        drawer.closeDrawer(GravityCompat.START);

        handleDrawer();

        switch (id) {

            case R.id.linear:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        handleProfileLayoutClick(ProfileFragment.ID_PERSONAL);
                    }
                });
                break;

            case R.id.home_layout:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        handleHomeLayoutClick();
                    }
                });
                break;

            case R.id.recent_ride_layout:

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        handleRecentRideLAyoutClick();
                    }
                });

                break;

            case R.id.current_ride_layout:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        handleCurrentAndUpcomingRideLayoutClick();
                    }
                });
                break;

            case R.id.profile_layout:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        handleProfileLayoutClick(ProfileFragment.ID_PERSONAL);
                    }
                });
                break;

            case R.id.emergency_rides_lyt:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        handleEmergencyRides();
                    }
                });
                break;

            case R.id.help_support_layout:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        handleHelpAndSupportLyt();
                    }
                });
                break;

            case R.id.about_us:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        handleAboutUsLyt();
                    }
                });
                break;
        }
    }

    private void handleEmergencyRides() {

        fragment = EmergencyRidesFragment.newInstance();

        RegularTextView profileTextView = rides_emergency_lyt.findViewById(R.id.emergency_rides);
        Drawable img3 = getResources().getDrawable(R.drawable.ic_emergency_on_sidebar);
        profileTextView.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);
        profileTextView.setTextColor(getResources().getColor(R.color.black));

        RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("Rides in Emergency");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);

        loadFragment("Emergency Rides");
    }

    public void handleLogout() {

        try {
            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(context, getString(R.string.please_wait));

                    restClientRetrofitService.logout(TagALongPreferenceManager.getToken(this)).enqueue(new Callback<ModelLogoutResponse>() {

                        @Override
                        public void onResponse(Call<ModelLogoutResponse> call, Response<ModelLogoutResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                TagALongPreferenceManager.saveDeviceProfile(HomeActivity.this, null);
                                TagALongPreferenceManager.saveToken(HomeActivity.this, "");
                                Toast.makeText(context, "Logged out Successfully", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finishAffinity();

                                if (getSinchServiceInterface() != null) {
                                    getSinchServiceInterface().stopClient();
                                }

                                DataManager.setStatus(1);
                                DataManager.setIsDocumentUploaded(false);
                                DataManager.setModelUserProfileData(null);
                                DataManager.setModelSearchRideRequest(null);
                                TagALongPreferenceManager.setDocumentUploadedStatus(context, false);
                                if (Utils.isJobServiceOn(context)) {
                                    JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                                    jobScheduler.cancel(3);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelLogoutResponse> call, Throwable t) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Home", "FAILURE logout");
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleProfileLayoutClick(int tabId) {

        fragment = ProfileFragment.newInstance(tabId);

        RegularTextView profileTextView = profileLayout.findViewById(R.id.profile_textView);
        Drawable img3 = getResources().getDrawable(R.drawable.ic_profile_on_sidebar_xxhdpi);
        profileTextView.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);
        profileTextView.setTextColor(getResources().getColor(R.color.black));

        RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("Profile");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);

        loadFragment("Profile");
    }

    public void handleCurrentRideLayoutClick() {

        fragment = new CurrentRideFragment();
        RegularTextView currentRidetext = currentRideLayout.findViewById(R.id.current_ride_textView);
        Drawable img1 = getResources().getDrawable(R.drawable.ic_current_ride_on_sidebar_xxhdpi);
        currentRidetext.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
        currentRidetext.setTextColor(getResources().getColor(R.color.black));

        RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.VISIBLE);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                inflatePreferenceSettings();
                showShareAlert();
            }
        });

        title.setVisibility(View.VISIBLE);
        title.setText("Current Ride");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);

        loadFragment("Current Ride");
    }

    public void handleCurrentAndUpcomingRideLayoutClick() {

        fragment = new CurrentUpcomingFragment();
        RegularTextView currentRideTextView = currentRideLayout.findViewById(R.id.current_ride_textView);
        Drawable img1 = getResources().getDrawable(R.drawable.ic_current_ride_on_sidebar_xxhdpi);
        currentRideTextView.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
        currentRideTextView.setTextColor(getResources().getColor(R.color.black));

        RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);

        title.setVisibility(View.VISIBLE);
        title.setText("Current & upcoming");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);

        loadFragment("Current Upcoming");
    }

//    public void handleCurrentRideDriver(ModelGetCurrentRideResponse modelGetCurrentRideResponse) {
//
//        fragment = CurrentRideFragmentDriver.newInstance(modelGetCurrentRideResponse);
//        RegularTextView currentRidecom.carpool.tagalong.views.RegularTextView = currentRideLayout.findViewById(R.id.current_ride_com.carpool.tagalong.views.RegularTextView);
//        Drawable img1 = getResources().getDrawable(R.drawable.ic_current_ride_on_sidebar_xxhdpi);
//        currentRidecom.carpool.tagalong.views.RegularTextView.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
//        currentRidecom.carpool.tagalong.views.RegularTextView.setTextColor(getResources().getColor(R.color.black));
//
//        com.carpool.tagalong.views.RegularTextView title  = toolbar.findViewById(R.id.toolbar_title);
//        ImageView share = toolbar.findViewById(R.id.share);
//        share.setVisibility(View.INVISIBLE);
//        title.setVisibility(View.VISIBLE);
//        title.setText("Current Ride");
//        toolbar.findViewById(R.id.title).setVisibility(View.GONE);
//
//        loadFragment("Current Ride");
//    }

    private void handleRecentRideLAyoutClick() {

        fragment = new RecentRidesFragment();
        com.carpool.tagalong.views.RegularTextView recentRideTextView = recentRidesLayout.findViewById(R.id.recent_rides_textView);
        Drawable img2 = getResources().getDrawable(R.drawable.ic_recent_ride_on_sidebar_xxhdpi);
        recentRideTextView.setCompoundDrawablesWithIntrinsicBounds(img2, null, null, null);
        recentRideTextView.setTextColor(getResources().getColor(R.color.black));
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);

        com.carpool.tagalong.views.RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setVisibility(View.VISIBLE);
        title.setText("Recent Ride");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);

        loadFragment("Recent Ride");
    }

    public void handleHomeLayoutClick() {

        fragment = new HomeFragment();
        com.carpool.tagalong.views.RegularTextView homeText = homeLayout.findViewById(R.id.homeTextView);
        Drawable img = getResources().getDrawable(R.drawable.ic_home_off_sidebar_xxhdpi);
        homeText.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        homeText.setTextColor(getResources().getColor(R.color.black));

        com.carpool.tagalong.views.RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setVisibility(View.GONE);
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);
        toolbar.findViewById(R.id.title).setVisibility(View.VISIBLE);

        loadFragment("Home");
    }

    private void handleAboutUsLyt() {

        fragment = new AboutUsFragment();
        com.carpool.tagalong.views.RegularTextView aboutUSText = aboutUsLyt.findViewById(R.id.about_us_textview);
        Drawable img = getResources().getDrawable(R.drawable.ic_about_us_on_sidebar_xxhdpi);
        aboutUSText.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);
        aboutUSText.setTextColor(getResources().getColor(R.color.black));

        com.carpool.tagalong.views.RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setVisibility(View.VISIBLE);
        title.setText("About Us");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);

        loadFragment("About");
    }

    private void handleHelpAndSupportLyt() {

        fragment = new HelpAndSupportFragment();
        com.carpool.tagalong.views.RegularTextView aboutUSText = hepSupportLyt.findViewById(R.id.help_and_supportTextView);
        Drawable img = getResources().getDrawable(R.drawable.ic_help_support_on_sidebar_xxhdpi);
        aboutUSText.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);
        aboutUSText.setTextColor(getResources().getColor(R.color.black));

        com.carpool.tagalong.views.RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setVisibility(View.VISIBLE);
        title.setText("Help & Support");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);

        loadFragment("Help");
    }

    private void loadFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void handleDrawer() {

        RegularTextView homeText = homeLayout.findViewById(R.id.homeTextView);
        RegularTextView currentRideTextView = currentRideLayout.findViewById(R.id.current_ride_textView);
        RegularTextView recentRideTextView = recentRidesLayout.findViewById(R.id.recent_rides_textView);
        RegularTextView profileTextView = profileLayout.findViewById(R.id.profile_textView);
        RegularTextView helpSupportTextView = hepSupportLyt.findViewById(R.id.help_and_supportTextView);
        RegularTextView aboutUscomTextView = aboutUsLyt.findViewById(R.id.about_us_textview);
        RegularTextView emergencyTextView = rides_emergency_lyt.findViewById(R.id.emergency_rides);

        Drawable img0 = getResources().getDrawable(R.drawable.ic_emergency_off_sidebar);
        emergencyTextView.setCompoundDrawablesWithIntrinsicBounds(img0, null, null, null);
        emergencyTextView.setTextColor(getResources().getColor(R.color.drawer_text_color));

        Drawable img = getResources().getDrawable(R.drawable.ic_home_on_sidebar_xxhdpi);
        homeText.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        homeText.setTextColor(getResources().getColor(R.color.drawer_text_color));

        Drawable img1 = getResources().getDrawable(R.drawable.ic_current_ride_off_sidebar_xxhdpi);
        currentRideTextView.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
        currentRideTextView.setTextColor(getResources().getColor(R.color.drawer_text_color));

        Drawable img2 = getResources().getDrawable(R.drawable.ic_recent_ride_off_sidebar_xxhdpi);
        recentRideTextView.setCompoundDrawablesWithIntrinsicBounds(img2, null, null, null);
        recentRideTextView.setTextColor(getResources().getColor(R.color.drawer_text_color));

        Drawable img3 = getResources().getDrawable(R.drawable.ic_profile_off_sidebar_xxhdpi);
        profileTextView.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);
        profileTextView.setTextColor(getResources().getColor(R.color.drawer_text_color));

        Drawable img4 = getResources().getDrawable(R.drawable.ic_help_support_off_sidebar_xxhdpi);
        helpSupportTextView.setCompoundDrawablesWithIntrinsicBounds(img4, null, null, null);
        helpSupportTextView.setTextColor(getResources().getColor(R.color.drawer_text_color));

        Drawable img5 = getResources().getDrawable(R.drawable.ic_about_us_off_sidebar_xxhdpi);
        aboutUscomTextView.setCompoundDrawablesWithIntrinsicBounds(img5, null, null, null);
        aboutUscomTextView.setTextColor(getResources().getColor(R.color.drawer_text_color));

    }

    @Override
    public void onFragmentInteraction(Fragment fragmentName) {

        if (fragmentName instanceof CurrentRideFragment) {
            com.carpool.tagalong.views.RegularTextView title = toolbar.findViewById(R.id.toolbar_title);
            title.setVisibility(View.VISIBLE);
            title.setText("Current Ride");
        }
    }

    @Override
    public void hideShareIcon() {
        ImageView share = toolbar.findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    private void handleLogOutAction() {
        OnLogoutActionClickListener alertDialogLogoutClickListener = new OnLogoutActionClickListener(context);
        UIUtils.showCustomConfirmDialog(context, context.getResources().getString(R.string.are_you_sure_to_logout), 2, "Yes", "No", alertDialogLogoutClickListener);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void getUserProfile() {

        if (Utils.isNetworkAvailable(context)) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                ProgressDialogLoader.progressDialogCreation(context, getString(R.string.please_wait));

                restClientRetrofitService.getUserProfile(TagALongPreferenceManager.getToken(context)).enqueue(new Callback<ModelUserProfile>() {

                    @Override
                    public void onResponse(Call<ModelUserProfile> call, Response<ModelUserProfile> response) {


                        if (response.body() != null) {

                            if (response.body().getStatus() == 1) {

                                Log.i("PERSONAL DETAILS", "PROFILE RESPONSE: " + response.body().getData().toString());
                                DataManager.modelUserProfileData = response.body().getData();

                                if (response.body().getData().getWepayDetails().getWePayVerificationStatus().equals("registered")) {
                                    DataManager.bookingStatus = true;
                                }

                                if (response.body().getData().getCard() != null) {

                                    if (response.body().getData().getCard().size() < 1) {
                                        DataManager.ridingstatus = false;
                                    } else if (response.body().getData().getCard().size() >= 1) {
                                        DataManager.ridingstatus = true;
                                    }
                                }

                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.avatar_avatar_12)
                                        .error(R.drawable.avatar_avatar_12);

                                if (DataManager.getModelUserProfileData() != null)
                                    Glide.with(context).load(DataManager.getModelUserProfileData().getProfile_pic()).apply(options).into(userImage);

                                if (response.body().getData().getDocuments() != null && response.body().getData().getDocuments().size() > 0) {
                                    TagALongPreferenceManager.setDocumentUploadedStatus(context, true);
                                } else {
                                    TagALongPreferenceManager.setDocumentUploadedStatus(context, false);
                                }
                            } else if (response.body().getStatus() == 0 && response.body().getMessage().equalsIgnoreCase("invalid token")) {
                                handleLogout();
                                return;
                            } else {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                        }

                        ProgressDialogLoader.progressDialogDismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelUserProfile> call, Throwable t) {
                        ProgressDialogLoader.progressDialogDismiss();
                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("SIGN UP", "FAILURE verification");
                    }
                });
            }
        } else {
            Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void refresh() {

        android.app.Fragment currentFragment = getFragmentManager().findFragmentByTag("Current Ride");
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
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

    private boolean checkAndRequestPermissions() {

        int permissionStorage = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionlocationGPS = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionrecordAudio = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO);

        int permissionReadPhoneState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionlocationGPS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionrecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        try {

            switch (requestCode) {

                case LOCATION_REQUEST_CODE:

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
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
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
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {
        Log.d("Calling homeActivity", "Successful connection");
    }

    /**
     * Dialog interface class where we handle the callback of logout alert dialog positive button click.
     */
    private class OnLogoutActionClickListener implements AlertDialogPermissionBoxClickInterface {
        Activity activityContext;

        public OnLogoutActionClickListener(Activity activityContext) {
            this.activityContext = activityContext;
        }

        @Override
        public void onButtonClicked(boolean isPositiveButtonClicked) {
            if (isPositiveButtonClicked) {
                handleLogout();
            }
        }
    }
}