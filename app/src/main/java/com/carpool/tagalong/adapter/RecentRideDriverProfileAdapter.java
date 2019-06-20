package com.carpool.tagalong.adapter;

import android.app.Activity;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelGetDriverProfileResponse;

import java.util.List;

public class RecentRideDriverProfileAdapter extends RecyclerView.Adapter<RecentRideDriverProfileAdapter.MyViewHolder> {

    private Activity activity;
    private List<ModelGetDriverProfileResponse.Rides> ridesList;

    public RecentRideDriverProfileAdapter(Activity activity, List<ModelGetDriverProfileResponse.Rides> rides) {
        this.ridesList = rides;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_rides_driver_profile_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ModelGetDriverProfileResponse.Rides s = ridesList.get(position);
        holder.location.setText(s.getHeading());
        Glide.with(activity).load(s.getPostUrl()).into(holder.tripImage);
    }

    @Override
    public int getItemCount() {
        return ridesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView location;
        public ImageView tripImage;


        public MyViewHolder(View view) {
            super(view);
            location  = view.findViewById(R.id.rideLoc);
            tripImage = view.findViewById(R.id.iv_trip_img);
        }
    }
}