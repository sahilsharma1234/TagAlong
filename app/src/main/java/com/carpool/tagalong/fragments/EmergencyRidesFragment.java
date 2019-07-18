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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.EmergencyRideActivity;
import com.carpool.tagalong.adapter.EmergencyRidesAdapter;
import com.carpool.tagalong.models.emergencysos.ModelGetEmergencyRidesResponse;
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
 * {@link EmergencyRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmergencyRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencyRidesFragment extends Fragment  implements View.OnClickListener, EmergencyRidesAdapter.EmergencyRideInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAg = EmergencyRidesFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private RecyclerView emergencyListRecyclerView;
    private RelativeLayout lytEmptyEmergencyRides;
    private com.carpool.tagalong.views.RegularTextView sosTxt;

    public EmergencyRidesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmergencyRidesFragment newInstance() {
        EmergencyRidesFragment fragment = new EmergencyRidesFragment();
      /*  Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
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

        View view =  inflater.inflate(R.layout.fragment_emergency_rides, container, false);
        emergencyListRecyclerView =  view.findViewById(R.id.emergency_rides_recyclerview);
        lytEmptyEmergencyRides    =  view.findViewById(R.id.lytNoEmergencyRides);
        sosTxt                    =  view.findViewById(R.id.title_text_home);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onResume() {
        super.onResume();

        getEmergencyRidesForUser();
    }

    private void getEmergencyRidesForUser() {

        try {
            if (Utils.isNetworkAvailable(getActivity())) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(getActivity(),getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.getEmergencyRides(TagALongPreferenceManager.getToken(getActivity())).enqueue(new Callback<ModelGetEmergencyRidesResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetEmergencyRidesResponse> call, Response<ModelGetEmergencyRidesResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {

                                    handleEmergencyRideResposne(response.body());

                                } else {
                                    lytEmptyEmergencyRides.setVisibility(View.VISIBLE);
//                                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Some error occured on server side!!", Toast.LENGTH_LONG).show();
                                lytEmptyEmergencyRides.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetEmergencyRidesResponse> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e(TAg, "FAILURE verification");
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(), "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }

    private void handleEmergencyRideResposne(ModelGetEmergencyRidesResponse data) {

        List<ModelGetEmergencyRidesResponse.EmergencyRides> emergencyRides = data.getEmergencyRides();

        if(emergencyRides != null && emergencyRides.size() > 0 ){
            lytEmptyEmergencyRides.setVisibility(View.GONE);
            sosTxt.setVisibility(View.VISIBLE);
            handleEmergencyList(emergencyRides);

        }else{
            lytEmptyEmergencyRides.setVisibility(View.VISIBLE);
            sosTxt.setVisibility(View.GONE);
        }
    }

    private void handleEmergencyList(List<ModelGetEmergencyRidesResponse.EmergencyRides> emergencyRides) {

        EmergencyRidesAdapter emergencyRidesAdapter = new EmergencyRidesAdapter(getActivity(),emergencyRides,this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        emergencyListRecyclerView.setLayoutManager(mLayoutManager);
        emergencyListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        emergencyListRecyclerView.setAdapter(emergencyRidesAdapter);
    }

    @Override
    public void onItemClick(ModelGetEmergencyRidesResponse.EmergencyRides ride) {

        if(ride!= null){

            if(ride.getDriverDetail() != null) {

                Intent intent = new Intent(getActivity(), EmergencyRideActivity.class);
                intent.putExtra("emergency_ride", ride);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
            }else
                Toast.makeText(getActivity(),"There is some issue pending in backend side when driver taps on SOS", Toast.LENGTH_LONG).show();
        }
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