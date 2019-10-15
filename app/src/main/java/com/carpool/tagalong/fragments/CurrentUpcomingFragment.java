package com.carpool.tagalong.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.CurrentRideActivity;
import com.carpool.tagalong.activities.CurrentRideActivityDriver;
import com.carpool.tagalong.activities.FreeRoamActivity;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.activities.QuickSearchRideActivity;
import com.carpool.tagalong.adapter.CurrentAndUpcomingRideAdapter;
import com.carpool.tagalong.models.ModelGetAllRidesResponse;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentUpcomingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentUpcomingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentUpcomingFragment extends Fragment implements View.OnClickListener, CurrentAndUpcomingRideAdapter.CurrentUpcomingInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context context;
    private CurrentAndUpcomingRideAdapter mAdapter;
    private RecyclerView currentNdUpcomingrecyclerView;
    private RelativeLayout add_ride_lyt, no_internet;
    private LinearLayout lyt_currnt_upcoming;
    private Button addRideBtn;

    public CurrentUpcomingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentRideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentUpcomingFragment newInstance(String param1, String param2, OnFragmentInteractionListener listener) {
        CurrentUpcomingFragment fragment = new CurrentUpcomingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_current_upcoming, container, false);
        currentNdUpcomingrecyclerView = view.findViewById(R.id.current_upcoming_recycler);
        lyt_currnt_upcoming = view.findViewById(R.id.lyt_list_curUpcoming);
        add_ride_lyt = view.findViewById(R.id.add_ride_post_rel);
        addRideBtn   = view.findViewById(R.id.add_ride_btn);
        no_internet  = view.findViewById(R.id.internet_lost);
        addRideBtn.setOnClickListener(this);
        Utils.clearNotifications(getActivity());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getAllRides() {

        try {
            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.getAllRides(TagALongPreferenceManager.getToken(getActivity())).enqueue(new Callback<ModelGetAllRidesResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetAllRidesResponse> call, Response<ModelGetAllRidesResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                if (response.body().getStatus() == 0 && response.body().getMessage().equalsIgnoreCase("invalid token")) {
                                    ((HomeActivity) getActivity()).handleLogout();
                                    return;
                                }
                                Log.i("Get All rides", "Get all rides RESPONSE " + response.body().toString());
                                no_internet.setVisibility(View.GONE);
                                initAdapter(response.body().getRideData());

                            } else {
                                no_internet.setVisibility(View.VISIBLE);
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetAllRidesResponse> call, Throwable t) {
                            ProgressDialogLoader.progressDialogDismiss();
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            no_internet.setVisibility(View.VISIBLE);
                            Log.e("Get All rides", "FAILURE GETTING ALL RIDES");
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(), "Please check your internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ProgressDialogLoader.progressDialogDismiss();
        }
    }

    @Override
    public void onResume() {
        getAllRides();
        super.onResume();
    }

    private void initAdapter(ModelGetAllRidesResponse.RideData rideData) {

        int ongoingRidesCount = 0;

        if (rideData != null) {

            if (rideData.getCurrentRidesArr() != null && rideData.getUpcomingRidesArr() != null) {

                List<ModelGetAllRidesResponse.Rides> ongongRidesLIst = rideData.getCurrentRidesArr();
                List<ModelGetAllRidesResponse.Rides> upcomingRideList = rideData.getUpcomingRidesArr();

                ongoingRidesCount = ongongRidesLIst.size();

                if (upcomingRideList.size() > 0) {

                    lyt_currnt_upcoming.setVisibility(View.VISIBLE);
                    add_ride_lyt.setVisibility(View.GONE);

                    for (ModelGetAllRidesResponse.Rides ride : rideData.getUpcomingRidesArr()) {
                        ongongRidesLIst.add(ride);
                    }
                }
                if (ongongRidesLIst.size() > 0) {

                    mAdapter = new CurrentAndUpcomingRideAdapter(context, ongongRidesLIst, this, ongoingRidesCount);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    currentNdUpcomingrecyclerView.setLayoutManager(mLayoutManager);
                    currentNdUpcomingrecyclerView.setItemAnimator(new DefaultItemAnimator());
                    currentNdUpcomingrecyclerView.setAdapter(mAdapter);

                } else {
                    lyt_currnt_upcoming.setVisibility(View.GONE);
                    add_ride_lyt.setVisibility(View.VISIBLE);
                }
            } else {
                lyt_currnt_upcoming.setVisibility(View.GONE);
                add_ride_lyt.setVisibility(View.VISIBLE);
            }
        } else {
            lyt_currnt_upcoming.setVisibility(View.GONE);
            add_ride_lyt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(ModelGetAllRidesResponse.Rides ride) {

        if (ride != null) {
            getRideDetails(ride.get_id());
        }
    }

    private void getRideDetails(final String rideId) {

        try {
            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ModelGetRideDetailsRequest modelGetRideDetailsRequest = new ModelGetRideDetailsRequest();
                    modelGetRideDetailsRequest.setRideId(rideId);

                    ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.getRideDetails(TagALongPreferenceManager.getToken(getActivity()), modelGetRideDetailsRequest).enqueue(new Callback<ModelGetCurrentRideResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetCurrentRideResponse> call, Response<ModelGetCurrentRideResponse> response) {

                            //                        ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

//                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                Log.i(CurrentUpcomingFragment.class.getSimpleName(), "Get rides RESPONSE " + response.body().toString());
                                handleRideResponse(response.body(), rideId);

                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetCurrentRideResponse> call, Throwable t) {
                            ProgressDialogLoader.progressDialogDismiss();
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Get All rides", "FAILURE GETTING ALL RIDES");
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(), "Please check your internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ProgressDialogLoader.progressDialogDismiss();
        }
    }

    private void handleRideResponse(ModelGetCurrentRideResponse data, String rideId) {

        if (data != null) {

            if (data.getRideData().isRideShort()) {

                if (data.getRideData().isDrive()) {

                    handleCurrentQuickRideForDriver(data, rideId);

                } else {
                    handleCurrentQuickRideForRider(data, rideId);
                }
            } else {
                if (data.getRideData().isDrive()) {

                    handleCurrentRideForDriver(data, rideId);

                } else {
                    handleCurrentRideForRider(data, rideId);
                }
            }
        }
    }

    private void handleCurrentQuickRideForRider(ModelGetCurrentRideResponse data, String rideId) {

        Intent intent = new Intent(getActivity(), QuickSearchRideActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", data);
        intent.putExtra("rideId", rideId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void handleCurrentQuickRideForDriver(ModelGetCurrentRideResponse data, String rideId) {


        Intent intent = new Intent(getActivity(), FreeRoamActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", data);
        intent.putExtra("rideId", rideId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    private void handleCurrentRideForRider(ModelGetCurrentRideResponse data, String rideId) {

        Intent intent = new Intent(getActivity(), CurrentRideActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", data);
        intent.putExtra("rideId", rideId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void handleCurrentRideForDriver(ModelGetCurrentRideResponse data, String rideId) {

        Intent intent = new Intent(getActivity(), CurrentRideActivityDriver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", data);
        intent.putExtra("rideId", rideId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.add_ride_btn:
                handleAddRide();
                break;
        }
    }

    private void handleAddRide() {
        HomeActivity activity = ((HomeActivity) getActivity());
        activity.handleDrawer();
        activity.handleHomeLayoutClick();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Fragment fragmentName);

        void hideShareIcon();
    }
}