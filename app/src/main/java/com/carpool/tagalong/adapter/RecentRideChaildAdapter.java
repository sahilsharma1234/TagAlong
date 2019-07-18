package com.carpool.tagalong.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelGetRecentRidesResponse;

import java.util.List;

public class RecentRideChaildAdapter extends RecyclerView.Adapter<RecentRideChaildAdapter.MyViewHolder> {

    private List<ModelGetRecentRidesResponse.TimelineData> timelineData;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public com.carpool.tagalong.views.RegularTextView title, year, genre;
        public ImageView iv_play_video,iv_trip_img;

        public MyViewHolder(View view) {
            super(view);
            // title = (com.carpool.tagalong.views.RegularTextView) view.findViewById(R.id.title);
            iv_play_video  = view.findViewById(R.id.iv_play_video);
            iv_trip_img  = view.findViewById(R.id.iv_trip_img);
        }
    }

    public RecentRideChaildAdapter(List<ModelGetRecentRidesResponse.TimelineData> timelineData) {
        this.timelineData = timelineData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_rides_child_itm_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        Glide.with(activity).load(timelineData.get(position).getPostUrl()).apply(options).into(holder.iv_trip_img);

    }

    @Override
    public int getItemCount() {
        return timelineData.size();
    }
}