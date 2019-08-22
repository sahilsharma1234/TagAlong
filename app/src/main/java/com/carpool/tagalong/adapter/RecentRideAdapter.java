package com.carpool.tagalong.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.glide.GlideApp;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelGetRecentRidesResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentRideAdapter extends RecyclerView.Adapter<RecentRideAdapter.MyViewHolder> {

    private List<ModelGetRecentRidesResponse.RideData> rideList;
    private Activity activity;
    private RecentRideInterface recentRideInterface;

    public RecentRideAdapter(Activity activity, List<ModelGetRecentRidesResponse.RideData> rideList, RecentRideInterface recentRideInterface) {
        this.rideList = rideList;
        this.activity = activity;
        this.recentRideInterface = recentRideInterface;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_rides_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.srcLocName.setText(rideList.get(position).getStartLocation());
        holder.destLocName.setText(rideList.get(position).getEndLocation());
        holder.date.setText(rideList.get(position).getRideDate());
        holder.distance.setText(rideList.get(position).getDistBtwSrcDest());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        if(rideList.get(position).isDrive()){
            GlideApp.with(activity).load(DataManager.getModelUserProfileData().getProfile_pic()).apply(options).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.driverProfileIMage);
        }else{
            GlideApp.with(activity).load(rideList.get(position).getDriverPic()).apply(options).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.driverProfileIMage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentRideInterface.onRecentRideClick(rideList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public interface RecentRideInterface {

        void onRecentRideClick(ModelGetRecentRidesResponse.RideData rideData);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        com.carpool.tagalong.views.RegularTextView srcLocName, destLocName, date, distance;
        CircleImageView driverProfileIMage;

        public MyViewHolder(View view) {
            super(view);

            date = view.findViewById(R.id.dateTime);
            distance = view.findViewById(R.id.rideDistance);
            srcLocName = view.findViewById(R.id.start_point_source_name);
            destLocName = view.findViewById(R.id.end_point_dest_name);
            driverProfileIMage = view.findViewById(R.id.driverImage);
        }
    }
}