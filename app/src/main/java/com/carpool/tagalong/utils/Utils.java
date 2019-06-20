package com.carpool.tagalong.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelGetCarColorsListResponse;
import com.carpool.tagalong.models.ModelGetCarYearList;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    public static String getRideDateFromDateString(String dateString) {

        String finalString = "";

        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
            long dateMillis = date.getTime();
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd-MM-yyyy"); // the day of the week spelled out completely
            finalString = simpleDateformat.format(dateMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalString;
    }

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

            Date date = new SimpleDateFormat("hh:mm a").parse(dateString);
            long dateMillis = date.getTime();

            SimpleDateFormat simpleDateformat = new SimpleDateFormat("HH:mm:ss");
            finalString = simpleDateformat.format(dateMillis);
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

                            if( data != null && data.size() > 0){
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
                        Log.e("SAVE DRIVING DETAILS", "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
            Toast.makeText(context, "PLease check your internet connection!!", Toast.LENGTH_LONG).show();
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

                            if( data != null && data.size() > 0){
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
                        Log.e("SAVE DRIVING DETAILS", "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
            Toast.makeText(context, "PLease check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    public static String getScreenWidthHeightInPixels(Activity context) {

        String coordinates = "";
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        coordinates = width + "~" + height;
        return coordinates;
    }

    public static int getDpAsPixels(Context context, int dp) {

        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (dp * scale + 0.5f);
        return dpAsPixels;
    }
}