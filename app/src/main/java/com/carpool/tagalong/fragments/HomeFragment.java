package com.carpool.tagalong.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.FreeRoamActivity;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.activities.QuickSearchRideActivity;
import com.carpool.tagalong.activities.SearchRideActivity;
import com.carpool.tagalong.activities.StartRideActivity;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.utils.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private CardView drivingCardView;
    private CardView searchCardView;

    public HomeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //     * @param param1 Parameter 1.
     * //     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        drivingCardView = view.findViewById(R.id.card_driving);
        searchCardView = view.findViewById(R.id.card_searching);
        drivingCardView.setOnClickListener(this);
        searchCardView.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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

        switch (id) {

            case R.id.card_driving:
                isDocumentUploaded();
                break;

            case R.id.card_searching:
                handleRiding();
                break;
        }
    }

    private void handleRiding() {

        if(DataManager.ridingstatus){
            showRidingAlert();
        }else {
            UIUtils.alertBox(getActivity(),"Please add credit card first in profile!!");
        }
    }

//    private void handleCardSearchingClick() {
//
////        Intent intent;
////        intent = new Intent(getActivity(), SearchRideActivity.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        startActivity(intent);
////        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//
//    }

    private void isDocumentUploaded() {

        if (TagALongPreferenceManager.getDocumentUploadedStatus(getActivity())) {

            if(DataManager.bookingStatus || (!DataManager.getModelUserProfileData().getDriverDetails().getVehicleNumber().equals("") && !DataManager.getModelUserProfileData().getDriverDetails().getVehicle().equals(""))) {
                showRoamingAlert();
            }else {
                UIUtils.alertBox(getActivity(),"Please register as a driver in profile section first!!");
            }
        } else {
            showDocumentAlert();
        }
    }

    private void showDocumentAlert() {

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.document_upload_alert, null);
            builder.setView(dialogLayout);
            builder.setCancelable(true);

            final AlertDialog alert = builder.create();
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

    private void showRidingAlert() {

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.rider_roam_dialog_lyt, null);
            builder.setView(dialogLayout);
            builder.setCancelable(true);

            final AlertDialog alert = builder.create();

            RelativeLayout quick_ride_rider = dialogLayout.findViewById(R.id.quick_rider_lyt);
            RelativeLayout schedule_rider_trip_lyt = dialogLayout.findViewById(R.id.schedule_rider_trip_lyt);

            quick_ride_rider.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alert.cancel();
                    Intent intent = new Intent(getActivity(), QuickSearchRideActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("quickride", true);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            schedule_rider_trip_lyt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    alert.cancel();

                    Intent intent = new Intent(getActivity(), SearchRideActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            alert.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void showRoamingAlert() {

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.driver_roam_dialog_lyt, null);
            builder.setView(dialogLayout);
            builder.setCancelable(true);

            final AlertDialog alert = builder.create();

            RelativeLayout free_roam_lyt = dialogLayout.findViewById(R.id.free_roam_lyt);
            RelativeLayout schedule_trip_lyt = dialogLayout.findViewById(R.id.schedule_trip_lyt);

            free_roam_lyt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alert.cancel();
                    Intent intent = new Intent(getActivity(), FreeRoamActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            schedule_trip_lyt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    alert.cancel();

                    Intent intent = new Intent(getActivity(), StartRideActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            alert.show();
        } catch (Exception exception) {
            exception.printStackTrace();
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