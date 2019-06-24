package com.carpool.tagalong.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.activities.SearchRideActivity;
import com.carpool.tagalong.activities.StartRideActivity;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmergencyRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmergencyRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencyRidesFragment extends Fragment  implements View.OnClickListener, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private RecyclerView emergencyListRecyclerView;

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
       emergencyListRecyclerView = view.findViewById(R.id.emergency_rides_recyclerview);
        // Inflate the layout for this fragment
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

        int id = v.getId();

        switch (id){

            case R.id.card_driving:
                isDocumentUploaded();
                break;

            case R.id.card_searching:
                handleCardSearchingClick();
                break;
        }
    }

    private void handleCardSearchingClick() {

        Intent intent;
        intent = new Intent(getActivity(), SearchRideActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void isDocumentUploaded() {

        if(TagALongPreferenceManager.getDocumentUploadedStatus(getActivity())){

            Intent intent = new Intent(getActivity(), StartRideActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }else{
            showDocumentAlert();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        GoogleMap map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
//
//        map.addMarker(new MarkerOptions()
//                .title(address)
//                .position(new LatLng(latitude, longitude))
//                .snippet(time));
    }

    private void showDocumentAlert(){

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater     = getLayoutInflater();
            View dialogLayout           = inflater.inflate(R.layout.document_upload_alert, null);
            builder.setView(dialogLayout);
            builder.setCancelable(true);

            final AlertDialog alert     = builder.create();
            Button upload_docu = dialogLayout.findViewById(R.id.upload_doc_btn);
            upload_docu.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alert.cancel();
                    HomeActivity activity = ((HomeActivity) getActivity());
                    activity.handleDrawer();
                    activity.handleProfileLayoutClick(ProfileFragment.ID_DRIVING);
                }
            });
            alert.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(getSourceLatLng())
//                .zoom(17)
//                .build();
//
//        addOverlay(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//        MarkerOptions options = new MarkerOptions();
//        options.position(getSourceLatLng());
//        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_ride_point_xhdpi);
//        options.icon(icon);
//        mMap.addMarker(options);
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