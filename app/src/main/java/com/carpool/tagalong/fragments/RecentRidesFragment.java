package com.carpool.tagalong.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.RecentRideAdapter;
import com.carpool.tagalong.models.ModelGetRecentRidesResponse;
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
 * {@link RecentRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecentRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentRidesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recycler_view_recent_rides;
    private RecentRideAdapter mAdapter;
    private com.carpool.tagalong.views.RegularTextView  norideTxt;
    private ImageView noRideImg;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecentRidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentRidesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentRidesFragment newInstance(String param1, String param2) {
        RecentRidesFragment fragment = new RecentRidesFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recent_rides, container, false);

        recycler_view_recent_rides = view.findViewById(R.id.recycler_view_recent_rides);
        noRideImg = view.findViewById(R.id.norideImg);
        norideTxt = view.findViewById(R.id.no_recent_rides);

        getRecentRides();

        // Inflate the layout for this fragment
        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initAdapter(List<ModelGetRecentRidesResponse.RideData> rideData) {

        mAdapter = new RecentRideAdapter(getActivity(), rideData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view_recent_rides.setLayoutManager(mLayoutManager);
        recycler_view_recent_rides.setItemAnimator(new DefaultItemAnimator());
        recycler_view_recent_rides.setAdapter(mAdapter);
    }

    private void getRecentRides() {

        if (Utils.isNetworkAvailable(getActivity())) {

            Log.i("Recent ride", "Recent Ride REQUEST ");

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                restClientRetrofitService.getRecentRides(TagALongPreferenceManager.getToken(getActivity())).enqueue(new Callback<ModelGetRecentRidesResponse>() {

                    @Override
                    public void onResponse(Call<ModelGetRecentRidesResponse> call, Response<ModelGetRecentRidesResponse> response) {
                        ProgressDialogLoader.progressDialogDismiss();

                        if (response.body() != null) {

                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            Log.i("Recent ride", "Recent Ride RESPONSE " + response.body().toString());

                            List<ModelGetRecentRidesResponse.RideData> rideData = response.body().getRideData();

                            if (rideData != null && rideData.size() > 0) {

                                noRideImg.setVisibility(View.GONE);
                                norideTxt.setVisibility(View.GONE);
                                recycler_view_recent_rides.setVisibility(View.VISIBLE);

                                handleRecentRideResposne(rideData);
                            }else{

                                noRideImg.setVisibility(View.VISIBLE);
                                norideTxt.setVisibility(View.VISIBLE);
                                recycler_view_recent_rides.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                            noRideImg.setVisibility(View.VISIBLE);
                            norideTxt.setVisibility(View.VISIBLE);
                            recycler_view_recent_rides.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelGetRecentRidesResponse> call, Throwable t) {
                        ProgressDialogLoader.progressDialogDismiss();
                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        noRideImg.setVisibility(View.VISIBLE);
                        norideTxt.setVisibility(View.VISIBLE);
                        recycler_view_recent_rides.setVisibility(View.GONE);

                        Log.e("SAVE DRIVING DETAILS", "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "PLease check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    private void handleRecentRideResposne(List<ModelGetRecentRidesResponse.RideData> rideData) {
        initAdapter(rideData);
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
        void onFragmentInteraction(Fragment uri);
    }
}