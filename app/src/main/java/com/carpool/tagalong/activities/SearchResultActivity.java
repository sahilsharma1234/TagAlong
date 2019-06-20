package com.carpool.tagalong.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.SearchResultsAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView searchResultRecyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private TextView dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        toolbarLayout = findViewById(R.id.toolbar_search_result);
        TextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        dateTime = findViewById(R.id.ride_details_text);
        title.setText("Search results");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().get(Constants.DATETIME) != null) {

                dateTime.setText("Showing results for " + getIntent().getExtras().get(Constants.DATETIME).toString());
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        searchResultRecyclerView = findViewById(R.id.search_result_recyclerView);
        searchResultsAdapter = new SearchResultsAdapter(this, DataManager.modelSearchRideResponseDataLIst);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchResultRecyclerView.setLayoutManager(mLayoutManager);
        searchResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchResultRecyclerView.setAdapter(searchResultsAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("launchCurrentRideFragment"));
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }
}