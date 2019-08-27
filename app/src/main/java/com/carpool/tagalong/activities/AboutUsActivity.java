package com.carpool.tagalong.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.carpool.tagalong.R;

public class AboutUsActivity extends AppCompatActivity {

    private WebView aboutUsWebView;
    private String url = "https://www.tagalongride.com/about.html";
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
//        final Activity activity = this;
//        aboutUsWebView = findViewById(R.id.aboutUsWebView);
//
//        aboutUsWebView.getSettings().setJavaScriptEnabled(true); // enable javascript
//
//        toolbarLayout = findViewById(R.id.toolbar_aboutUs);
//        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
//        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
//        toolbar = toolbarLayout.findViewById(R.id.toolbar);
//        title.setText("About Us");
//        title.setVisibility(View.VISIBLE);
//        titleImage.setVisibility(View.GONE);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
//        } else {
//            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
//        }
//
//        aboutUsWebView.setWebViewClient(new WebViewClient() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
//            }
//
//            @TargetApi(android.os.Build.VERSION_CODES.M)
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
//                // Redirect to deprecated method, so you can use it in all SDK versions
//                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
//            }
//        });
//
//        loadUrl();
    }

    private void loadUrl(){
        aboutUsWebView.loadUrl(url);
    }
}