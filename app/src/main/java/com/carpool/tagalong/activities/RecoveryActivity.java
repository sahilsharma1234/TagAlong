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
import android.widget.TextView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelUpdatePasswordRequest;
import com.carpool.tagalong.models.ModelUpdatePasswordResponse;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.Utils;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoveryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button  savePassword;
    private String mobileNumber;
    private TextInputEditText enterPasssword, renterpassword;
    private RelativeLayout progressLyt;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);
        setToolBar();
        context = this;
        mobileNumber =  getIntent().getExtras().get("mobilenumber").toString();

        enterPasssword = findViewById(R.id.enterPassword);
        renterpassword = findViewById(R.id.reenterPsswrd);
        progressLyt    = findViewById(R.id.lytBarRecovery);

        savePassword = findViewById(R.id.savePassword);
        savePassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(handlePasswordChange()){
                    updatePassword();
                }
            }
        });
    }

    private boolean handlePasswordChange(){

        String passwordString = enterPasssword.getText().toString();
        String reneterPasswordString = renterpassword.getText().toString();

        if(passwordString.equals("")){
            enterPasssword.setError("Please enter password");
            return false;
        }
        if(!isValidPassword(passwordString)){
            enterPasssword.setError("Password must be minimum 8 and max 18 characters including numbers,special characters and capital alphabets");
            return false;
        }
        if(reneterPasswordString.equals("")){
            renterpassword.setError("Please re-enter password");
            return false;
        }
        if(!passwordString.equals(reneterPasswordString)){
            Toast.makeText(this,"Please enter same passwords!!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {

        return Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,18})").matcher(password).matches();
    }

    private void setToolBar() {

        toolbar = findViewById(R.id.toolbar_recovery);
        TextView title  = toolbar.findViewById(R.id.title);
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

    private void updatePassword(){

        String password = enterPasssword.getText().toString();

        ModelUpdatePasswordRequest modelUpdatePasswordRequest = new ModelUpdatePasswordRequest();
        modelUpdatePasswordRequest.setMobileNo(mobileNumber);
        modelUpdatePasswordRequest.setPassword(password);
        if(Utils.isNetworkAvailable(this)){

            progressLyt.setVisibility(View.VISIBLE);

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {
                restClientRetrofitService.updatePasssword(modelUpdatePasswordRequest).enqueue(new Callback<ModelUpdatePasswordResponse>() {
                    @Override
                    public void onResponse(Call<ModelUpdatePasswordResponse> call, Response<ModelUpdatePasswordResponse> response) {

                        progressLyt.setVisibility(View.GONE);

                        if (response.body() != null && response.body().getStatus() == 1){

                            Toast.makeText(context, "Password Successfully Updated!!", Toast.LENGTH_LONG).show();

                            Intent intent;
                            intent = new Intent(RecoveryActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finishAffinity();

                        }else{
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelUpdatePasswordResponse> call, Throwable t) {

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