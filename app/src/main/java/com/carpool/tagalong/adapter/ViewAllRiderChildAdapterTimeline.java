package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelViewAllRidesDriverResponse;

import java.util.List;

public class ViewAllRiderChildAdapterTimeline extends RecyclerView.Adapter<ViewAllRiderChildAdapterTimeline.MyViewHolder> {

    private Context activity;
    private List<ModelViewAllRidesDriverResponse.TimelineData> ridesList;

    public ViewAllRiderChildAdapterTimeline(Context activity, List<ModelViewAllRidesDriverResponse.TimelineData> rides) {
        this.ridesList = rides;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_rides_child_itm_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ModelViewAllRidesDriverResponse.TimelineData s = ridesList.get(position);
//        holder.location.setText(s.getHeading());
        Glide.with(activity).load(s.getPostUrl()).into(holder.tripImage);
    }

    @Override
    public int getItemCount() {
        return ridesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public com.carpool.tagalong.views.RegularTextView location;
        public ImageView tripImage, videoImg;


        public MyViewHolder(View view) {
            super(view);
//            location  = view.findViewById(R.id.rideLoc);
            tripImage = view.findViewById(R.id.iv_trip_img);
            videoImg  = view.findViewById(R.id.iv_play_video);
        }
    }
}