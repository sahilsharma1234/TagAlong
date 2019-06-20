package com.carpool.tagalong.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.TabAdapter;
import com.carpool.tagalong.tabsfragments.DrivingProfileFragment;
import com.carpool.tagalong.tabsfragments.PaymentProfileFragment;
import com.carpool.tagalong.tabsfragments.PersonalProfileFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int ID_PERSONAL=101,ID_DRIVING=102,ID_PAYMENT=103;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProfileFragment newInstance(int tabId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt("tab_id",tabId);
        fragment.setArguments(args);
        return fragment;
    }

    private int[] unSelectedtabIcons = {
            R.drawable.ic_profile_profile_off_xxhdpi,
            R.drawable.ic_drive_profile_off_xxhdpi,
            R.drawable.ic_money_profile_off_xxhdpi
    };

    private int[] selectedtabIcons = {
            R.drawable.ic_profile_profile_on_xxhdpi,
            R.drawable.ic_drive_profile_on_xxhdpi,
            R.drawable.ic_money_profile_on_xxhdpi
    };

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
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        viewPager =  view.findViewById(R.id.viewPager);
        tabLayout =  view.findViewById(R.id.tabLayout);

        adapter   =  new TabAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new PersonalProfileFragment(), "Personal");
        adapter.addFragment(new DrivingProfileFragment(), "Driving");
        adapter.addFragment(new PaymentProfileFragment(), "Payment");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(selectedtabIcons[0]);
        tabLayout.getTabAt(1).setIcon(unSelectedtabIcons[1]);
        tabLayout.getTabAt(2).setIcon(unSelectedtabIcons[2]);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition() == 0){
                    tab.setIcon(selectedtabIcons[0]);
                    tabLayout.getTabAt(1).setIcon(unSelectedtabIcons[1]);
                    tabLayout.getTabAt(2).setIcon(unSelectedtabIcons[2]);

                }else if(tab.getPosition() == 1){
                    tab.setIcon(selectedtabIcons[1]);
                    tabLayout.getTabAt(0).setIcon(unSelectedtabIcons[0]);
                    tabLayout.getTabAt(2).setIcon(unSelectedtabIcons[2]);

                }else{
                    tab.setIcon(selectedtabIcons[2]);
                    tabLayout.getTabAt(0).setIcon(unSelectedtabIcons[0]);
                    tabLayout.getTabAt(1).setIcon(unSelectedtabIcons[1]);
                }
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        int tabId=getArguments().getInt("tab_id");
        toggleTabBar(tabId);

        // Inflate the layout for this fragment
        return view;
    }

    private void toggleTabBar(int tabId) {

        switch (tabId){
            case ID_DRIVING:
                viewPager.setCurrentItem(1);
                break;
            case ID_PAYMENT:
                viewPager.setCurrentItem(2);
                break;
            case ID_PERSONAL:
                viewPager.setCurrentItem(0);
                break;
        }
    }

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