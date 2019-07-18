package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnBoardRidersAdapter extends RecyclerView.Adapter<OnBoardRidersAdapter.MyViewHolder> {

    private List<ModelGetCurrentRideResponse.OnBoard> onBoardList;
    private Context context;
    private OnBoardRidersInterface listener;

    public OnBoardRidersAdapter(Context context, List<ModelGetCurrentRideResponse.OnBoard> onBoardList,OnBoardRidersInterface listener) {

        this.onBoardList = onBoardList;
        this.context = context;
        this.listener  = listener;
    }

    @NonNull
    @Override
    public OnBoardRidersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_profile_pic_onboard, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OnBoardRidersAdapter.MyViewHolder myViewHolder, int i) {

        final int position = i;
        myViewHolder.nameUser.setText(onBoardList.get(i).getUserName());
        myViewHolder.rating_user.setText(onBoardList.get(i).getRating()+"");

        if(onBoardList.get(i).isPayStatus()){
            myViewHolder.paymentIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_payment_in_cash_current_ride_xhdpi));

        }else{
            myViewHolder.paymentIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_payment_not_done_current_ride_xhdpi));
        }

        Glide.with(context)
                .load(onBoardList.get(i).getProfile_pic())
                .into(myViewHolder.rider_image);

        myViewHolder.rider_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener!= null)
                listener.onBoardRiderClick(onBoardList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return onBoardList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public com.carpool.tagalong.views.RegularTextView nameUser, rating_user;
        public CircleImageView rider_image;
        public ImageView paymentIcon;

        public MyViewHolder(View view) {
            super(view);
            // title = (com.carpool.tagalong.views.RegularTextView) view.findViewById(R.id.title);
            rider_image = view.findViewById(R.id.image_user_profile);
            nameUser    = view.findViewById(R.id.name_user);
            rating_user = view.findViewById(R.id.rider_rating);
            paymentIcon = view.findViewById(R.id.payment_icon);
        }
    }

    public interface OnBoardRidersInterface{
        void onBoardRiderClick(ModelGetCurrentRideResponse.OnBoard onBoardRider);
    }
}