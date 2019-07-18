package com.carpool.tagalong.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        String header[] = s.getHeading().split(" to ");
        holder.fromlocation.setText(header[0]);
        holder.toLocation.setText(header[1]);
//        Glide.with(activity).load(s.getPostUrl()).into(holder.tripImage);
    }

    @Override
    public int getItemCount() {
        return ridesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public com.carpool.tagalong.views.RegularTextView fromlocation, toLocation;
        public ImageView tripImage;

        public MyViewHolder(View view) {
            super(view);
            fromlocation  = view.findViewById(R.id.from_loc);
            toLocation    = view.findViewById(R.id.to_loc);
            tripImage     = view.findViewById(R.id.iv_trip_img);
        }
    }
}