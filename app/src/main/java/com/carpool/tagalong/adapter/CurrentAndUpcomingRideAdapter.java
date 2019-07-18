package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelGetAllRidesResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentAndUpcomingRideAdapter extends RecyclerView.Adapter<CurrentAndUpcomingRideAdapter.MyViewHolder> {

    private List<ModelGetAllRidesResponse.Rides> modelGetAllRidesResponsesList;
    private Context activity;
    private CurrentUpcomingInterface currentUpcomingInterface;
    private int  onGoingRideCount;

    public CurrentAndUpcomingRideAdapter(Context activity, List<ModelGetAllRidesResponse.Rides> commonlist, CurrentUpcomingInterface currentUpcomingInterface, int onGoingRideCount) {
        this.modelGetAllRidesResponsesList = commonlist;
        this.activity = activity;
        this.currentUpcomingInterface = currentUpcomingInterface;
        this.onGoingRideCount = onGoingRideCount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_rides_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.srcLocName.setText(modelGetAllRidesResponsesList.get(position).getStartLocation());
        holder.destLocName.setText(modelGetAllRidesResponsesList.get(position).getEndLocation());
        holder.date.setText(modelGetAllRidesResponsesList.get(position).getRideDate());

        if(position < onGoingRideCount){
            holder.distance.setText("OnGoing");
            holder.distance.setTextColor(activity.getResources().getColor(R.color.colorGold));
        }else{
            holder.distance.setText("Upcoming");
        }

//        if(!allUpcomingRides) {
//            if (position == 0) {
//                holder.distance.setText("OnGoing");
//                holder.distance.setTextColor(activity.getResources().getColor(R.color.colorGold));
//            } else
//                holder.distance.setText("Upcoming");
//        }else{
//            holder.distance.setText("Upcoming");
//        }
    }

    @Override
    public int getItemCount() {
        return modelGetAllRidesResponsesList.size();
    }

    public interface CurrentUpcomingInterface {

        void onItemClick(ModelGetAllRidesResponse.Rides ride);
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
            driverProfileIMage.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    currentUpcomingInterface.onItemClick(modelGetAllRidesResponsesList.get(getAdapterPosition()));
                }
            });
        }
    }
}