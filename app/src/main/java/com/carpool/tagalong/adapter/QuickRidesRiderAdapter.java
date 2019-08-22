package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.emergencysos.ModelGetEmergencyRidesResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuickRidesRiderAdapter extends RecyclerView.Adapter<QuickRidesRiderAdapter.MyViewHolder> {

    private List<ModelGetCurrentRideResponse.OnBoard> onBoards;
    private Context activity;
    private ridersatusclicklistener ridersatusclicklistener1;


    public QuickRidesRiderAdapter(Context activity, List<ModelGetCurrentRideResponse.OnBoard> onBoards, ridersatusclicklistener ridersatusclicklistener1) {
        this.activity = activity;
        this.onBoards = onBoards;
        this.ridersatusclicklistener1 = ridersatusclicklistener1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quick_ride_driver_lyt_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.riderName.setText(onBoards.get(position).getUserName());
        holder.riderRating.setText(onBoards.get(position).getRating() + "");
        holder.riderFare.setText("$ " + onBoards.get(position).getEstimatedFare() + "");

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        Glide.with(activity).load(onBoards.get(position).getProfile_pic()).apply(options).into(holder.riderImage);

        if(onBoards.get(position).getStatus() == Constants.ACCEPTED){

            holder.riderStatus.setText("PICKUP");
        }else{
            holder.riderStatus.setText("DROP");
        }

        holder.riderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ridersatusclicklistener1.onItemClick(onBoards.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return onBoards.size();
    }

    public interface ridersatusclicklistener {

        void onItemClick(ModelGetCurrentRideResponse.OnBoard onboard);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        com.carpool.tagalong.views.RegularTextView riderName, riderRating, riderFare, riderStatus;
        CircleImageView riderImage;

        public MyViewHolder(View view) {
            super(view);

            riderName = view.findViewById(R.id.riderName);
            riderRating = view.findViewById(R.id.riderRating);
            riderFare = view.findViewById(R.id.estFareForRider);
            riderImage = view.findViewById(R.id.riderImage);
            riderStatus = view.findViewById(R.id.status);
//
//            view.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    emergencyRideInterface.onItemClick(emergencyRides.get(getAdapterPosition()));
//
//                }
//            });
        }
    }
}