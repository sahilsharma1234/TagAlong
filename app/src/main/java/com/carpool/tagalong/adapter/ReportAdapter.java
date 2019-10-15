package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.SendReportActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ravi on 23/10/17.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

    private ThumbnailsAdapterListener listener;
    private Context mContext;
    private int selectedIndex = 0;
    private List<SendReportActivity.ModelReport> modelReportList;

    public ReportAdapter(Context context, List<SendReportActivity.ModelReport> modelReportList, ThumbnailsAdapterListener listener) {
        mContext = context;
        this.listener = listener;
        this.modelReportList = modelReportList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnail_report_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.thumbnail.setImageResource(modelReportList.get(position).getDrawable());
        holder.itemName.setText(modelReportList.get(position).getActionName());
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onFilterSelected(modelReportList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelReportList.size();
    }

    public interface ThumbnailsAdapterListener {
        void onFilterSelected(SendReportActivity.ModelReport modelReport);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        CircleImageView thumbnail;

        @BindView(R.id.item_name)
        com.carpool.tagalong.views.RegularTextView itemName;

        public MyViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}