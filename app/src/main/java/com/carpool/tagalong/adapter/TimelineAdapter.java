package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpool.tagalong.R;
import com.carpool.tagalong.glide.GlideApp;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsResponse;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.MyViewHolder> {

    private List<ModelGetCurrentRideResponse.Timeline> timelines;
    private Context context;

    public TimelineAdapter(Context context, List<ModelGetCurrentRideResponse.Timeline> timelines) {

        this.timelines = timelines;
        this.context = context;
    }

    @NonNull
    @Override
    public TimelineAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_post, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.nameUser.setText(timelines.get(i).getUserName());
        myViewHolder.time.setText(timelines.get(i).getCreatedAt());

        GlideApp.with(context)
                .load(timelines.get(i).getProfile_pic())
                .into(myViewHolder.profile_image);

        GlideApp.with(context)
                .load(timelines.get(i).getPostUrl())
                .into(myViewHolder.posted_image);
    }

    @Override
    public int getItemCount() {
        return timelines.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nameUser, time;
        public ImageView posted_image,profile_image;

        public MyViewHolder(View view) {
            super(view);
            // title = (TextView) view.findViewById(R.id.title);
            profile_image = view.findViewById(R.id.userImage);
            posted_image  = view.findViewById(R.id.posted_image);
            nameUser = view.findViewById(R.id.userName);
            time = view.findViewById(R.id.postTime);

        }
    }
}