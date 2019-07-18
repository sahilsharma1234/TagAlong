package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.emergencysos.ModelGetEmergencyRidesResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmergencyRidesAdapter extends RecyclerView.Adapter<EmergencyRidesAdapter.MyViewHolder> {

    private List<ModelGetEmergencyRidesResponse.EmergencyRides> emergencyRides;
    private Context activity;
    private EmergencyRideInterface emergencyRideInterface;


    public EmergencyRidesAdapter(Context activity, List<ModelGetEmergencyRidesResponse.EmergencyRides> emergencyRides, EmergencyRideInterface emergencyRideInterface) {
        this.emergencyRides = emergencyRides;
        this.activity = activity;
        this.emergencyRideInterface = emergencyRideInterface;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_rides_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.srcLocName.setText(emergencyRides.get(position).getStartLocation());
        holder.destLocName.setText(emergencyRides.get(position).getEndLocation());
        holder.date.setText(emergencyRides.get(position).getRideDateTime());
        holder.distance.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return emergencyRides.size();
    }

    public interface EmergencyRideInterface {

        void onItemClick(ModelGetEmergencyRidesResponse.EmergencyRides ride);
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

                    emergencyRideInterface.onItemClick(emergencyRides.get(getAdapterPosition()));

                }
            });
        }
    }
}