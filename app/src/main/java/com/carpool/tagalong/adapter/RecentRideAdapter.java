package com.carpool.tagalong.adapter;

import android.app.Activity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelGetRecentRidesResponse;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecentRideAdapter extends RecyclerView.Adapter<RecentRideAdapter.MyViewHolder> {

    private List<ModelGetRecentRidesResponse.RideData> rideList;
    private Activity activity;

    public RecentRideAdapter(Activity activity, List<ModelGetRecentRidesResponse.RideData> rideList) {
        this.rideList = rideList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_rides_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//
//        holder.title.setText(rideList.get(position).getHeading());
//        holder.date.setText(rideList.get(position).getRideDate());
//        holder.distance.setText(rideList.get(position).getDistBtwSrcDest());
//
//        List<ModelGetRecentRidesResponse.TimelineData> timelineData = rideList.get(position).getTimelineData();
//
//        if(timelineData != null && timelineData.size() > 0) {
//            RecentRideChaildAdapter mAdapter = new RecentRideChaildAdapter(timelineData);
//
//            LinearLayoutManager mLayoutManager
//                    = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
//            holder.recycler_view_item_images.setLayoutManager(mLayoutManager);
//            holder.recycler_view_item_images.setItemAnimator(new DefaultItemAnimator());
//            holder.recycler_view_item_images.setAdapter(mAdapter);
//        }

        holder.srcLocName.setText(rideList.get(position).getStartLocation());
        holder.destLocName.setText(rideList.get(position).getEndLocation());
        holder.date.setText(rideList.get(position).getRideDate());
        holder.distance.setText(rideList.get(position).getDistBtwSrcDest());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        Glide.with(activity).load(rideList.get(position).getDriverPic()).apply(options).into(holder.driverProfileIMage);

//        if(rideList.get(position).getRidersList() != null && rideList.get(position).getRidersList().size() >0) {
//
//            for (int i = 0; i < rideList.get(position).getRidersList().size(); i++) {
//
//                CircleImageView circleImageView = new CircleImageView(activity);
//
//                RelativeLayout.LayoutParams imParams =
//                        new RelativeLayout.LayoutParams(((int) (activity.getResources().getDimension(R.dimen.imageViewSize))), ((int) activity.getResources().getDimension(R.dimen.imageViewSize)));
//                circleImageView.setId(i);
//                int margin = (int) ((activity.getResources().getDimension(R.dimen.imageViewSize) / 2) * i);
//                imParams.leftMargin = margin;
//                holder.rl_rider_parent.addView(circleImageView, imParams);
//
//                Glide.with(activity).load(rideList.get(position).getRidersList().get(i).getProfile_pic()).apply(options).into(holder.driverProfileIMage);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

//        public TextView title, date, distance;
//        public RecyclerView recycler_view_item_images;
//        public RelativeLayout rl_rider_parent;
//        private CircleImageView driverProfileIMage;

        TextView srcLocName, destLocName, date, distance;
        CircleImageView driverProfileIMage;

        public MyViewHolder(View view) {
            super(view);
//            title = view.findViewById(R.id.tv_ride_title);
//            date = view.findViewById(R.id.tv_ride_date);
//            distance = view.findViewById(R.id.tv_ride_distance);
//
//            recycler_view_item_images = view.findViewById(R.id.recycler_view_item_images);
//            rl_rider_parent = view.findViewById(R.id.rl_rider_parent);
//            driverProfileIMage = view.findViewById(R.id.iv_driver_profile_image);

            date = view.findViewById(R.id.dateTime);
            distance = view.findViewById(R.id.rideDistance);
            srcLocName  = view.findViewById(R.id.start_point_source_name);
            destLocName = view.findViewById(R.id.end_point_dest_name);
            driverProfileIMage = view.findViewById(R.id.driverImage);
        }
    }
}