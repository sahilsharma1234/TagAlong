package com.carpool.tagalong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.ReportAdapter;
import com.carpool.tagalong.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendReportActivity extends AppCompatActivity implements ReportAdapter.ThumbnailsAdapterListener {

    private static final int REQUEST_CODE = 123;
    public static int RESULT_CODE = 321;
    @BindView(R.id.report_recyclerview)
    RecyclerView reportRecyclerView;
    private List<ModelReport> modelReportList = new ArrayList<>();
    private int[] drawableArray = new int[]{R.drawable.ic_ic_traffic_jam, R.drawable.ic_ic_police, R.drawable.ic_ic_accident, R.drawable.ic_ic_caution, R.drawable.ic_ic_road_closer, R.drawable.ic_ic_roadside_help, R.drawable.ic_ic_info};
    private String[] reportStrings = new String[]{"Traffic", "Police", "Crash", "Hazard", "Closure", "Roadside\n help", "Info"};
    String startLoc, endLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);
        ButterKnife.bind(this);

        for (int i = 0; i < drawableArray.length; i++) {

            ModelReport modelReport = new ModelReport();
            modelReport.setActionName(reportStrings[i]);
            modelReport.setDrawable(drawableArray[i]);
            modelReportList.add(modelReport);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        reportRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

        ReportAdapter reportAdapter = new ReportAdapter(this, modelReportList, this);
        reportRecyclerView.setAdapter(reportAdapter);

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey("startLoc")) {

                startLoc = getIntent().getExtras().getString("startLoc");
            }

            if (getIntent().getExtras().containsKey("endLoc")) {

                endLoc = getIntent().getExtras().getString("endLoc");
            }
        }
    }

    @Override
    public void onFilterSelected(ModelReport modelReport) {

        Toast.makeText(this, modelReport.getActionName(), Toast.LENGTH_LONG).show();
        if (modelReport.getActionName().equals("Traffic") || modelReport.getActionName().equals("Police")) {

            Intent intent = new Intent(this, SendReportDetailActivity.class);
            intent.putExtra("type", modelReport.getActionName());
            intent.putExtra("startLoc", startLoc);
            intent.putExtra("endLoc", endLoc);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUEST_CODE);
        } else
            Utils.sendReportToServer(SendReportActivity.this, startLoc, endLoc, modelReport.getActionName(), "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            finish();
        }
    }

    public class ModelReport {

        private String actionName;
        private int drawable;

        public String getActionName() {
            return actionName;
        }

        public void setActionName(String actionName) {
            this.actionName = actionName;
        }

        public int getDrawable() {
            return drawable;
        }

        public void setDrawable(int drawable) {
            this.drawable = drawable;
        }

    }
}