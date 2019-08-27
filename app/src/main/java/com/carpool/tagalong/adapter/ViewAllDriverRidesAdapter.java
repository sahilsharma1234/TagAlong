package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelViewAllRidesDriverResponse;
import com.carpool.tagalong.views.RegularTextView;

import java.util.List;

public class ViewAllDriverRidesAdapter extends RecyclerView.Adapter<ViewAllDriverRidesAdapter.MyViewHolder> {

    private List<ModelViewAllRidesDriverResponse.Data> data;
    private Context activity;
    private RecyclerView recyclerView ;

    public ViewAllDriverRidesAdapter(Context activity, List<ModelViewAllRidesDriverResponse.Data> data) {
        this.data = data;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_all_rides_driver_parent, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.headerAddress.setText(data.get(position).getHeading());

        ViewAllRiderChildAdapterTimeline mAdapter = new ViewAllRiderChildAdapterTimeline(activity, data.get(position).getTimelineData());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RegularTextView headerAddress;

        public MyViewHolder(View view) {
            super(view);

            headerAddress = view.findViewById(R.id.dateTime);
            recyclerView = view.findViewById(R.id.ridesDriverListChild);

//            view.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    currentUpcomingInterface.onItemClick(data.get(getAdapterPosition()));
//                }
//            });
        }
    }
}