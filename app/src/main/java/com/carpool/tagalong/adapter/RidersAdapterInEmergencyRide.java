package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.emergencysos.ModelGetEmergencyRidesResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RidersAdapterInEmergencyRide extends RecyclerView.Adapter<RidersAdapterInEmergencyRide.MyViewHolder> {

    private List<ModelGetEmergencyRidesResponse.RiderList> ridersList;
    private Context activity;
    private RiderInEmergencyInterface emergencyRideInterface;
    private ModelGetEmergencyRidesResponse.Location location;

    public RidersAdapterInEmergencyRide(Context activity,  List<ModelGetEmergencyRidesResponse.RiderList> emergencyRides,  ModelGetEmergencyRidesResponse.Location location,RiderInEmergencyInterface emergencyRideInterface) {
        this.ridersList = emergencyRides;
        this.activity = activity;
        this.emergencyRideInterface = emergencyRideInterface;
        this.location = location;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_riders_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.riderName.setText(ridersList.get(position).getUserName());
        holder.riderNumber.setText(ridersList.get(position).getMobileNo()+"");
        Glide.with(activity)
                .load(ridersList.get(position).getProfile_pic())
                .into(holder.riderImage);

        holder.callUserLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergencyRideInterface.callUser(ridersList.get(position), location);
            }
        });

        holder.navigateLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emergencyRideInterface.navigate(ridersList.get(position), location);

            }
        });
    }

    @Override
    public int getItemCount() {
        return ridersList.size();
    }

    public interface RiderInEmergencyInterface {

        void callUser(ModelGetEmergencyRidesResponse.RiderList ride, ModelGetEmergencyRidesResponse.Location location);

        void navigate(ModelGetEmergencyRidesResponse.RiderList ride, ModelGetEmergencyRidesResponse.Location location);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        com.carpool.tagalong.views.RegularTextView riderName, riderNumber;
        CircleImageView riderImage;
        LinearLayout callUserLyt, navigateLyt;

        public MyViewHolder(View view) {
            super(view);

            riderName   = view.findViewById(R.id.emergency_rider_name);
            riderNumber = view.findViewById(R.id.emergency_rider_number);
            riderImage  = view.findViewById(R.id.emergency_rider_img);
            callUserLyt = view.findViewById(R.id.callUserLyt);
            navigateLyt = view.findViewById(R.id.navigateUserLyt);


         /*   view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    emergencyRideInterface.onItemClick(ridersList.get(getAdapterPosition()));

                }
            });*/
        }
    }
}