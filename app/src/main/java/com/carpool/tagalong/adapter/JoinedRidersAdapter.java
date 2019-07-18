package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinedRidersAdapter extends RecyclerView.Adapter<JoinedRidersAdapter.MyViewHolder> {

    private List<ModelGetCurrentRideResponse.JoinRequest> joinedRidersList;
    private Context context;
    private joinriderlistener listener;

    public JoinedRidersAdapter(Context context, List<ModelGetCurrentRideResponse.JoinRequest> joinedRidersList, joinriderlistener listener) {

        this.joinedRidersList = joinedRidersList;
        this.context = context;
        this.listener  = listener;
    }

    @NonNull
    @Override
    public JoinedRidersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_profile_pic, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull JoinedRidersAdapter.MyViewHolder myViewHolder, int i) {

        final int position = i;
        myViewHolder.nameUser.setText(joinedRidersList.get(i).getUserName());
        myViewHolder.rating_user.setText(joinedRidersList.get(i).getRating()+"");

        Glide.with(context)
                .load(joinedRidersList.get(i).getProfile_pic())
                .into(myViewHolder.rider_image);

        myViewHolder.rider_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onJoinRiderClick(joinedRidersList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return joinedRidersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public com.carpool.tagalong.views.RegularTextView nameUser, rating_user;
        public CircleImageView rider_image;

        public MyViewHolder(View view) {
            super(view);
            // title = (com.carpool.tagalong.views.RegularTextView) view.findViewById(R.id.title);
            rider_image = view.findViewById(R.id.image_user_profile);
            nameUser = view.findViewById(R.id.name_user);
            rating_user = view.findViewById(R.id.rider_rating);

        }
    }

    public interface joinriderlistener{

        void onJoinRiderClick(ModelGetCurrentRideResponse.JoinRequest modelJoinRequest);

    }
}