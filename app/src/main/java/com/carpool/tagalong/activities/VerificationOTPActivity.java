package com.carpool.tagalong.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelVerifyOtp;
import com.carpool.tagalong.models.ModelVerifyOtpResponse;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.Utils;
import com.mukesh.OtpView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationOTPActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button verifyButton;
    private OtpView otpView;
    private RelativeLayout progressLyt;
    private Context context;
    private String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        setToolBar();
        context = this;
        verifyButton   = findViewById(R.id.verify);
        otpView        = findViewById(R.id.otp_view);
        progressLyt    = findViewById(R.id.lytBarVerificationOtp);

        mobileNumber = getIntent().getExtras().get("mobilenumber").toString();

        verifyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                handleOtpVerification();
            }
        });
    }

    private void handleOtpVerification() {

        if(otpView.getOTP().equals("")){

            Toast.makeText(this, "Please enter otp first!!", Toast.LENGTH_LONG).show();
            return;
        }
        verifyOtpUser();
    }

    private void setToolBar() {

        toolbar         = findViewById(R.id.toolbar_verification);
        TextView title  = toolbar.findViewById(R.id.title);
        title.setText("Verification");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi,null));
        }else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }
    }

    private void verifyOtpUser(){

        String otp = otpView.getOTP();

        ModelVerifyOtp modelVerifyOtp = new ModelVerifyOtp();
        modelVerifyOtp.setOtp(Integer.parseInt(otp));
        modelVerifyOtp.setMobileNo(mobileNumber);

        if(Utils.isNetworkAvailable(this)){

            progressLyt.setVisibility(View.VISIBLE);

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {
                restClientRetrofitService.verifyOtp(modelVerifyOtp).enqueue(new Callback<ModelVerifyOtpResponse>() {
                    @Override
                    public void onResponse(Call<ModelVerifyOtpResponse> call, Response<ModelVerifyOtpResponse> response) {

                        progressLyt.setVisibility(View.GONE);

                        if (response.body() != null && response.body().getStatus() == 1){

                            Toast.makeText(context, "Successfully Verified!!", Toast.LENGTH_LONG).show();

                            Intent intent;
                            intent = new Intent(VerificationOTPActivity.this, RecoveryActivity.class);
                            intent.putExtra("mobilenumber",mobileNumber);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }else{
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelVerifyOtpResponse> call, Throwable t) {

                        progressLyt.setVisibility(View.GONE);

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("SIGN UP", "FAILURE verification");
                    }
                });
            }
        }
    }
}