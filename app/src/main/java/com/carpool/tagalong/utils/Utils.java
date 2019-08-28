package com.carpool.tagalong.utils;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetCarColorsListResponse;
import com.carpool.tagalong.models.ModelGetCarYearList;
import com.carpool.tagalong.models.ModelUserProfile;
import com.carpool.tagalong.models.emergencysos.ModelUpdateCoordinates;
import com.carpool.tagalong.models.wepay.CreditCards;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.service.JobSchedulerService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sahilsharma on 20/6/18.
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getDateFromDateString(String dateString) {
        String finalString = "";

        try {

            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
            long dateMillis = date.getTime();
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE, MMMM dd, yyyy"); // the day of the week spelled out completely
            finalString = simpleDateformat.format(dateMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalString;
    }

//    public static String getRideDateFromDateString(String dateString) {
//
//        String finalString = "";
//
//        try {
//            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
//            long dateMillis = date.getTime();
//            SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd-MM-yyyy"); // the day of the week spelled out completely
//            finalString = simpleDateformat.format(dateMillis);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return finalString;
//    }

    public static String getRidePostDateFromDateString(String dateString) {

        String finalString = "";

        try {

            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
            long dateMillis = date.getTime();

            SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-MM-dd"); // the day of the week spelled out completely
            finalString = simpleDateformat.format(dateMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalString;
    }

    public static String getRideTimeFromDateString(String dateString) {

        String finalString = "";

        try {

            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US).parse(dateString);
//            long dateMillis = date.getTime();

            SimpleDateFormat simpleDateformat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            finalString = simpleDateformat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalString;
    }

    public static void getYearsList(final Context context) {

        if (Utils.isNetworkAvailable(context)) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                restClientRetrofitService.getYears().enqueue(new Callback<ModelGetCarYearList>() {

                    @Override
                    public void onResponse(Call<ModelGetCarYearList> call, Response<ModelGetCarYearList> response) {

                        if (response.body() != null) {
//                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                            List<Integer> data = response.body().getData();

                            if (data != null && data.size() > 0) {
                                DataManager.setYearsList(data);
                            }

                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelGetCarYearList> call, Throwable t) {

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e(TAG, "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
            Toast.makeText(context, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    public static void getColorList(final Context context) {

        if (Utils.isNetworkAvailable(context)) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                restClientRetrofitService.getColors().enqueue(new Callback<ModelGetCarColorsListResponse>() {

                    @Override
                    public void onResponse(Call<ModelGetCarColorsListResponse> call, Response<ModelGetCarColorsListResponse> response) {

                        if (response.body() != null) {
//                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                            List<String> data = response.body().getData();

                            if (data != null && data.size() > 0) {
                                DataManager.setColorList(data);
                            }

                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelGetCarColorsListResponse> call, Throwable t) {

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e(TAG, "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
            Toast.makeText(context, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    public static void updateCoordinates1(Location location, final Context context) {

        if (Utils.isNetworkAvailable(context)) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                ModelUpdateCoordinates modelUpdateCoordinates = new ModelUpdateCoordinates();
                modelUpdateCoordinates.setLatitude(location.getLatitude());
                modelUpdateCoordinates.setLongitude(location.getLongitude());

                restClientRetrofitService.updateCoordinates(TagALongPreferenceManager.getToken(context), modelUpdateCoordinates).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                        if (response.body() != null) {

                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e(TAG, "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
//            Toast.makeText(context, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

//    public static String getScreenWidthHeightInPixels(Activity context) {
//
//        String coordinates = "";
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int width = displayMetrics.widthPixels;
//        int height = displayMetrics.heightPixels;
//        coordinates = width + "~" + height;
//        return coordinates;
//    }
//
//    public static int getDpAsPixels(Context context, int dp) {
//
//        float scale = context.getResources().getDisplayMetrics().density;
//        int dpAsPixels = (int) (dp * scale + 0.5f);
//        return dpAsPixels;
//    }

    public static void scheduleApplicationPackageJob(Context context) {

        ComponentName serviceComponent = new ComponentName(context, JobSchedulerService.class);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(3, serviceComponent);
        builder.setMinimumLatency(10 * 1000); // wait at least
        builder.setOverrideDeadline(1 * 60 * 1000);
        builder.setPersisted(true);
        jobScheduler.schedule(builder.build());
    }

    public static boolean isJobServiceOn(Context context) {

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;

        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == 3) {
                hasBeenScheduled = true;
                break;
            }
        }
        return hasBeenScheduled;
    }

    public static void getUserProfile(final Context context) {

        if (Utils.isNetworkAvailable(context)) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {
                restClientRetrofitService.getUserProfile(TagALongPreferenceManager.getToken(context)).enqueue(new Callback<ModelUserProfile>() {

                    @Override
                    public void onResponse(Call<ModelUserProfile> call, Response<ModelUserProfile> response) {

                        if (response.body() != null) {

                            if (response.body().getStatus() == 1) {

                                Log.i(TAG, "PROFILE RESPONSE: " + response.body().getData().toString());
                                DataManager.modelUserProfileData = response.body().getData();

                                List<CreditCards> creditCardsList = response.body().getData().getCard();

                                if (creditCardsList != null) {

                                    if (creditCardsList.size() < 1) {
                                        DataManager.ridingstatus = false;
                                    } else if (creditCardsList.size() >= 1) {
                                        DataManager.ridingstatus = true;
                                    }
                                }

                                if (response.body().getData().getDocuments() != null && response.body().getData().getDocuments().size() > 0) {
                                    TagALongPreferenceManager.setDocumentUploadedStatus(context, true);
                                } else {
                                    TagALongPreferenceManager.setDocumentUploadedStatus(context, false);
                                }
                            } else {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelUserProfile> call, Throwable t) {

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e(TAG, "FAILURE verification");
                    }
                });
            }
        } else {
            Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    public static String getDeviceImeiNumber(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Please provide permission from settings to this app", Toast.LENGTH_SHORT).show();
            return "";
        }
        String device_id = tm.getDeviceId();
        return device_id;
    }
}