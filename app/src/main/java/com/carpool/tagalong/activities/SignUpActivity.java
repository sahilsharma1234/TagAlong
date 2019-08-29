package com.carpool.tagalong.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelSignUpResponse;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.UIUtils;
import com.carpool.tagalong.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.hbb20.CountryCodePicker;

import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener{

    private Toolbar           toolbar;
    private Context           context;
    private TextInputEditText userName,       email_address, mobileNumber,  address, password,  reEnterPassword;
    private String            usernameString, emailString,   addressString, mobileNumberString, passwordString, reneterPasswordString;
    private LinearLayout      register;
    private com.carpool.tagalong.views.RegularTextView          signInText;
    private CallbackManager   callbackManager;
    private LoginButton       loginButton;
    private LinearLayout      facebookSignUp,twitterSignUp;
    private RelativeLayout    signupProgressBarLyt;
    private static final String TAG = "Sign Up";
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setToolbar();
        initializeData();
    }

    private void setToolbar() {

        toolbar = findViewById(R.id.toolbar_signup);
        com.carpool.tagalong.views.RegularTextView title  = toolbar.findViewById(R.id.title);
        title.setText("Register");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi,null));
        }else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }
    }

    private void initializeData(){

        context = this;
        userName        = findViewById(R.id.userNameSignUp);
        email_address   = findViewById(R.id.emailSignUp);
        mobileNumber    = findViewById(R.id.mobileNumberSignUp);
        address         = findViewById(R.id.address);
        password        = findViewById(R.id.passwordSignUp);
        reEnterPassword = findViewById(R.id.renterPassword);
        register        = findViewById(R.id.register);
        signInText      = findViewById(R.id.signintext);
        callbackManager = CallbackManager.Factory.create();
//        facebookSignUp  = findViewById(R.id.facebookSignUp);
        loginButton     = new LoginButton(context);
        signupProgressBarLyt    = findViewById(R.id.lytBarSignUp);
        countryCodePicker       = findViewById(R.id.countryCodeSignUp);
        countryCodePicker.setCustomMasterCountries("us");
        register.setOnClickListener(this);
        makeSignInTextSpannable();
//
//        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
//
//        callbackManager = CallbackManager.Factory.create();
//
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // here write code When Login successfully
//                Toast.makeText(context, "Successful SignUp", Toast.LENGTH_LONG).show();
//
//                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
//
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(
//                                    final JSONObject object,
//                                    GraphResponse response) {
//                                Toast.makeText(context,"SignUp Successfull",Toast.LENGTH_LONG).show();
//                            }
//                        });
//
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,about,email,gender,link,first_name,last_name,picture");
//                request.setParameters(parameters);
//                request.executeAsync();
//                AccessToken.getCurrentAccessToken();
//            }
//
//            @Override
//            public void onCancel() {
//            }
//
//            @Override
//            public void onError(FacebookException e) {
//                e.printStackTrace();
//                Toast.makeText(context, "Error SignUp With Facebook", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        facebookSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                loginButton.performClick();
//            }
//        });
    }

    private boolean validateSignUpData() {

        usernameString        =     userName.getText().toString();
        emailString           =     email_address.getText().toString();
        mobileNumberString    =     mobileNumber.getText().toString();
        addressString         =     address.getText().toString();
        passwordString        =     password.getText().toString();
        reneterPasswordString =     reEnterPassword.getText().toString();

        if(usernameString.equals("")){
            userName.setError("Please enter username");
            return false;
        }
        if(emailString.equals("")){
            email_address.setError("Please enter email");
            return false;
        }
        if(!isValidEmaillId(emailString)){
            email_address.setError("Please enter valid email id");
            return false;
        }
        if(mobileNumberString.equals("")){
            mobileNumber.setError("Please enter mobile number");
            return false;
        }
        if(addressString.equals("")){
            address.setError("Please enter address");
            return false;
        }
        if(passwordString.equals("")){
            password.setError("Please enter password");
            return false;
        }
        if(!isValidPassword(passwordString)){
            password.setError("Password must be minimum 8 and max 18 characters including numbers,special characters and capital alphabets");
            return false;
        }
        if(reneterPasswordString.equals("")){
            reEnterPassword.setError("Please re-enter password");
            return false;
        }
        if(!passwordString.equals(reneterPasswordString)){
            Toast.makeText(context,"Please enter same password!!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){

            case R.id.register:
                handleSignUp();
                break;
        }
    }

    private void handleSignUp() {

        UIUtils.hideSoftKeyboard(this);

        if(validateSignUpData()){
            signUp();
        }
    }

    private void makeSignInTextSpannable(){

        SpannableString spannableString = new SpannableString("Already member? Sign in");

        ClickableSpan gmail = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                launchSignInActivity();
                signInText.setSelected(false);
            }
        };

        spannableString.setSpan(gmail, 16, 23, 0); // to make link
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), 16, 23, 0); // for coloring purpose

        signInText.setMovementMethod(LinkMovementMethod.getInstance());
        signInText.setText(spannableString, com.carpool.tagalong.views.RegularTextView.BufferType.SPANNABLE);
        signInText.setHighlightColor(Color.TRANSPARENT);
    }

    private void launchSignInActivity() {

        Intent intent;
        intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void signUp() {

        RequestBody userName     = RequestBody.create(MediaType.parse("text/plain"), usernameString);
        RequestBody email        = RequestBody.create(MediaType.parse("text/plain"), emailString);
        RequestBody mobileNumber = RequestBody.create(MediaType.parse("text/plain"), countryCodePicker.getSelectedCountryCode()+""+mobileNumberString);
        RequestBody address      = RequestBody.create(MediaType.parse("text/plain"), addressString);
        RequestBody password     = RequestBody.create(MediaType.parse("text/plain"), passwordString);
        RequestBody repassword   = RequestBody.create(MediaType.parse("text/plain"), reneterPasswordString);
        RequestBody deviceToken  = RequestBody.create(MediaType.parse("text/plain"), TagALongPreferenceManager.getDeviceToken(context));
        RequestBody deviceType   = RequestBody.create(MediaType.parse("text/plain"), Constants.DEVICE_TYPE);

        if(Utils.isNetworkAvailable(context)){

            signupProgressBarLyt.setVisibility(View.VISIBLE);

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                restClientRetrofitService.registerUser(userName,email,mobileNumber,address,password,repassword,deviceToken,deviceType).enqueue(new Callback<ModelSignUpResponse>() {
                    @Override
                    public void onResponse(Call<ModelSignUpResponse> call, Response<ModelSignUpResponse> response) {

                        signupProgressBarLyt.setVisibility(View.GONE);

                        if (response.body() != null) {
                            if (response.body().getStatus() == 1) {

                                Log.i(TAG, "Sign Up response is: "+response.body().toString());

                                Toast.makeText(context, "Successfully Registered!!", Toast.LENGTH_LONG).show();

                                Intent intent;
                                intent = new Intent(context, VerificationActivity.class);
                                intent.putExtra(Constants.FROM, Constants.SIGNUP);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                TagALongPreferenceManager.saveToken(context, response.body().getToken());
                                finish();

                            } else {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelSignUpResponse> call, Throwable t) {

                        signupProgressBarLyt.setVisibility(View.GONE);

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
//                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(context, getString(R.string.some_error_occurred), Toast.LENGTH_LONG).show();
                        Log.e("SIGN UP", "FAILURE Sign Up");
                    }
                });
            }
        }else{
            Toast.makeText(context,getString(R.string.check_internet),Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private boolean isValidPassword(String password) {

        return Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,18})").matcher(password).matches();
    }
}