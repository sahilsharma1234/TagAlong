package com.carpool.tagalong.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.RecentRideAdapter;

public class RecentRidesActivity extends AppCompatActivity {

    private RecyclerView recycler_view_recent_rides;
    private RecentRideAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_rides);
        initView();
        initAdapter();
    }

    private void initAdapter() {

//        List<String> sl = new ArrayList<>();
//        sl.add("BC");
//        sl.add("BC");
//        sl.add("BC");
//        sl.add("BC");

//        mAdapter = new RecentRideAdapter(this, sl);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recycler_view_recent_rides.setLayoutManager(mLayoutManager);
//        recycler_view_recent_rides.setItemAnimator(new DefaultItemAnimator());
//        recycler_view_recent_rides.setAdapter(mAdapter);
    }

    private void initView() {
        recycler_view_recent_rides = findViewById(R.id.recycler_view_recent_rides);
    }
}