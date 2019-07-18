package com.carpool.tagalong.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelSignInRequest;
import com.carpool.tagalong.models.ModelSignInResponse;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.hbb20.CountryCodePicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Sign IN";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Context context;
    private LinearLayout facebookSignin, twitterSignIn, login;
    private TextInputEditText userName, password;
    private String usernameString, passwordString;
    private com.carpool.tagalong.views.RegularTextView signUpText, forgotPassword;
    private RelativeLayout progressBarLayout;
    private CountryCodePicker countryCodePickerSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        context = this;
//        callbackManager   = CallbackManager.Factory.create();
//        facebookSignin    = findViewById(R.id.facebookLogin);
//        twitterSignIn     = findViewById(R.id.twitterLogin);
        loginButton = new LoginButton(context);
        userName = findViewById(R.id.usernameSignIn);
        password = findViewById(R.id.passwordSignIn);
        login = findViewById(R.id.login);
        signUpText = findViewById(R.id.signUpText);
        forgotPassword = findViewById(R.id.forgotPassword);
        progressBarLayout = findViewById(R.id.lytBarSignIn);
        countryCodePickerSignIn = findViewById(R.id.countryCodeSignIn);
//
//        countryCodePickerSignIn.setAutoDetectedCountry(true);
//        countryCodePickerSignIn.detectLocaleCountry(true);
        countryCodePickerSignIn.setCustomMasterCountries("us");

        login.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        makeSignUpTextSpannable();

//        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

//        callbackManager = CallbackManager.Factory.create();

//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // here write code When Login successfully
//                Toast.makeText(context, "Successful Login", Toast.LENGTH_LONG).show();
//
//                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
//
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(
//                                    final JSONObject object,
//                                    GraphResponse response) {
//                                Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show();
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
//                Toast.makeText(context, "Error Login With Facebook", Toast.LENGTH_LONG).show();
//            }
//        });

//        facebookSignin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loginButton.performClick();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.login:
                handleLogin();
                break;

            case R.id.forgotPassword:
                handleForgotPassword();
                break;
        }
    }

    private void handleForgotPassword() {
        Intent intent;
        intent = new Intent(this, RecoveryMobileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void handleLogin() {

        if (validateLoginData()) {
            signIn();
        }
    }

    private boolean validateLoginData() {

        usernameString = userName.getText().toString();
        passwordString = password.getText().toString();
        if (usernameString.equals("")) {
            userName.setError("Please enter username");
            return false;
        }
        if (passwordString.equals("")) {
            password.setError("Please enter password");
            return false;
        }
        return true;
    }

    private void makeSignUpTextSpannable() {

        SpannableString spannableString = new SpannableString("Not a member yet? Register here");

        ClickableSpan gmail = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                launchSignUpActivity();
                signUpText.setSelected(false);
            }
        };

        spannableString.setSpan(gmail, 18, 31, 0); // to make link
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), 18, 31, 0); // for coloring purpose

        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
        signUpText.setText(spannableString, com.carpool.tagalong.views.RegularTextView.BufferType.SPANNABLE);
        signUpText.setHighlightColor(Color.TRANSPARENT);
        stripUnderlines(signUpText);
    }

    private void launchSignUpActivity() {

        Intent intent;
        intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void signIn() {

        ModelSignInRequest modelSignInRequest = new ModelSignInRequest();
        modelSignInRequest.setMobileNo(countryCodePickerSignIn.getSelectedCountryCode() + "" + usernameString);
        modelSignInRequest.setPassword(passwordString);
        modelSignInRequest.setDeviceToken(TagALongPreferenceManager.getDeviceToken(context));
        modelSignInRequest.setDeviceType(Integer.parseInt(Constants.DEVICE_TYPE));

        if (Utils.isNetworkAvailable(context)) {

            progressBarLayout.setVisibility(View.VISIBLE);

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            Log.i(TAG, "Sign IN request is: " + modelSignInRequest.toString());

            if (restClientRetrofitService != null) {

                restClientRetrofitService.signIn(modelSignInRequest).enqueue(new Callback<ModelSignInResponse>() {

                    @Override
                    public void onResponse(Call<ModelSignInResponse> call, Response<ModelSignInResponse> response) {

                        progressBarLayout.setVisibility(View.GONE);

                        if (response.body() != null && response.body().getStatus() == 1) {

                            Log.i(TAG, "Sign IN response is: " + response.body().toString());

                            Toast.makeText(context, "Login Successful!!", Toast.LENGTH_LONG).show();

                            // this is done here and to be used in driving fragment to get year list once the user login into app
                            Utils.getYearsList(context);

                            // this is done here and to be used in driving fragment to get color list once the user login into app
                            Utils.getColorList(context);

                            Intent intent;
                            intent = new Intent(context, HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            TagALongPreferenceManager.saveToken(context, response.body().getToken());
                            TagALongPreferenceManager.saveDeviceProfile(context, response.body().getData());
                            isDocumentUploaded();
                            finish();

                        } else if (response.body() != null && response.body().getStatus() == 0) {

                            if (response.body().getData() != null) {

                                if (response.body().getData().verifyMobile) {

                                    Intent intent;
                                    intent = new Intent(context, VerificationOTPActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                } else {
                                    Log.i(TAG, response.body().toString());
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {

                                Log.i(TAG, response.body().toString());
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelSignInResponse> call, Throwable t) {
                        progressBarLayout.setVisibility(View.GONE);

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }

                        Toast.makeText(context, "Some error occurred!! Please try again!", Toast.LENGTH_LONG).show();
                        Log.e("SIGN IN", "FAILURE Login");
                    }
                });
            }
        } else {
            Toast.makeText(context, "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    private void stripUnderlines(com.carpool.tagalong.views.RegularTextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(20, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end   = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private void isDocumentUploaded() {

        try {

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.checkDocument(TagALongPreferenceManager.getToken(context)).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    TagALongPreferenceManager.setDocumentUploadedStatus(context, true);
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
                            Log.e("SIGN UP", "FAILURE verification");
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

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}