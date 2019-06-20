package com.carpool.tagalong.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.RequestRideActivity;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelSearchRideResponseData;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchViewHolder> {

    private Context context;
    private List<ModelSearchRideResponseData> modelSearchRideResponseDataList;

    public SearchResultsAdapter(Context context, List<ModelSearchRideResponseData> modelSearchRideResponseDataList) {
        this.context = context;
        this.modelSearchRideResponseDataList = modelSearchRideResponseDataList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_ride_details_search, viewGroup, false);

        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i) {

        searchViewHolder.name.setText(modelSearchRideResponseDataList.get(i).getUserName());
        searchViewHolder.startLocation.setText(modelSearchRideResponseDataList.get(i).getStartLocation());
        searchViewHolder.endLocation.setText(modelSearchRideResponseDataList.get(i).getEndLocation().trim());
        searchViewHolder.starttime.setText("Ride Created on "+modelSearchRideResponseDataList.get(i).getRideDateTime());
        searchViewHolder.ridestarttime.setText(modelSearchRideResponseDataList.get(i).getRideDateTime());
        Glide.with(context)
                .load(modelSearchRideResponseDataList.get(i).getProfile_pic())
                .into(searchViewHolder.profile_pic);
    }

    @Override
    public int getItemCount() {
        return modelSearchRideResponseDataList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView startLocation;
        TextView endLocation;
        TextView starttime,ridestarttime;
        CircleImageView profile_pic;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.driver_name);
            startLocation = itemView.findViewById(R.id.start_point_source_name);
            endLocation   = itemView.findViewById(R.id.end_point_dest_name);
            starttime     = itemView.findViewById(R.id.ride_timing);
            profile_pic   = itemView.findViewById(R.id.driver_pic);
            ridestarttime     = itemView.findViewById(R.id.strt_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataManager.modelSearchRideResponseData = modelSearchRideResponseDataList.get(getAdapterPosition());
                    Intent intent = new Intent(context, RequestRideActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}
