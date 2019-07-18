package com.carpool.tagalong.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelForgotPasswordRequest;
import com.carpool.tagalong.models.ModelForgotPasswordResponse;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.Utils;
import com.hbb20.CountryCodePicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoveryMobileActivity extends AppCompatActivity {

    private static final String TAG = "RecoveryMobileActivity"  ;
    private Toolbar toolbar;
    private Button sendCode;
    private CountryCodePicker countryCodePicker;
    private TextInputEditText mobileNumber;
    private Context context;
    private RelativeLayout progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_mobile);
        context = this;
        setToolBar();
        sendCode= findViewById(R.id.sendCode);
        countryCodePicker       = findViewById(R.id.countryCodeRecoveryMobile);
        mobileNumber            = findViewById(R.id.mobileNumberRecovery);
        progressBarLayout       = findViewById(R.id.lytBarRecoveryMobile);
        countryCodePicker.setCustomMasterCountries("us");

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleSendButtonClick();
            }
        });
    }

    private void setToolBar() {

        toolbar = findViewById(R.id.toolbar_recovery_mobile);
        com.carpool.tagalong.views.RegularTextView title  = toolbar.findViewById(R.id.title);
        title.setText("Recovery");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi,null));
        }else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }
    }

    private void handleSendButtonClick(){

        if(!mobileNumber.getText().toString().equals("")){

            handleForGotPassword();
        }
    }

    private void handleForGotPassword(){

        ModelForgotPasswordRequest modelForgotPasswordRequest = new ModelForgotPasswordRequest();
        final String value = countryCodePicker.getSelectedCountryCode()+""+mobileNumber.getText().toString();
        modelForgotPasswordRequest.setMobileNo(value);

        if(Utils.isNetworkAvailable(context)){

            progressBarLayout.setVisibility(View.VISIBLE);

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            Log.i(TAG, "Sign IN request is: "+modelForgotPasswordRequest.toString());

            if (restClientRetrofitService != null) {

                restClientRetrofitService.forgotPassword(modelForgotPasswordRequest).enqueue(new Callback<ModelForgotPasswordResponse>() {

                    @Override
                    public void onResponse(Call<ModelForgotPasswordResponse> call, Response<ModelForgotPasswordResponse> response) {

                        progressBarLayout.setVisibility(View.GONE);

                        if (response.body() != null && response.body().getStatus() == 1){

                            Log.i(TAG, "Forgot password response is: "+response.body().toString());

                            Toast.makeText(context, "OTP sent successfully!!", Toast.LENGTH_LONG).show();

                            Intent intent;
                            intent = new Intent(context, VerificationOTPActivity.class);
                            intent.putExtra("mobilenumber",value);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        }else if(response.body() != null && response.body().getStatus() == 0){

                            Log.i(TAG, response.body().toString());
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelForgotPasswordResponse> call, Throwable t) {
                        progressBarLayout.setVisibility(View.GONE);

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("SIGN IN", "FAILURE Login");
                    }
                });
            }
        }
    }
}