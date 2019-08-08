package com.carpool.tagalong.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.wepay.ModelGetWePayIframeRequest;
import com.carpool.tagalong.models.wepay.ModelIframeResponse;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KYCActivityWebview extends AppCompatActivity {

    private WebView kycWebview;
    private String url = "https://www.tagalongride.com/wepay_kyc.html?update_uri=";
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kycwebview);

        kycWebview = findViewById(R.id.kyc_webView);

        kycWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        toolbarLayout = findViewById(R.id.toolbar_kyc);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("KYC");
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

        kycWebview.setWebChromeClient(new WebChromeClient());

//        kycWebview.setWebViewClient(new WebViewClient() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
//            }
//            @TargetApi(android.os.Build.VERSION_CODES.M)
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
//                // Redirect to deprecated method, so you can use it in all SDK versions
//                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
//            }
//        });

        ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));
        getIframeURl();
    }

    private void getIframeURl() {

        try {

            if (Utils.isNetworkAvailable(this)) {

                ModelGetWePayIframeRequest modelRegisterMerchantWePayRequest = new ModelGetWePayIframeRequest();
                modelRegisterMerchantWePayRequest.setAccount_id(Long.valueOf(DataManager.getModelUserProfileData().getWepayDetails().getWePayAccountId()));
                modelRegisterMerchantWePayRequest.setMode(Constants.IFRAME_MODE);
                RestClientInterface restClientRetrofitService = new ApiClient().getIframeApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.getIframeUrl("Bearer " + DataManager.getModelUserProfileData().getWepayDetails().getWePayAccessToken(), modelRegisterMerchantWePayRequest).enqueue(new Callback<ModelIframeResponse>() {

                        @Override
                        public void onResponse(Call<ModelIframeResponse> call, Response<ModelIframeResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if(response!= null){
                                if(response.code() == 200){
                                    url = url+ response.body().getUri();
                                    loadUrl();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelIframeResponse> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("SAVE Payment DETAILS", "FAILURE SAVING PROFILE");
                        }
                    });
                }
            } else {
                ProgressDialogLoader.progressDialogDismiss();
                Toast.makeText(this, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUrl(){
        kycWebview.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}