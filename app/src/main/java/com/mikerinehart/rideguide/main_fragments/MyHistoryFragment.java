package com.mikerinehart.rideguide.main_fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.activities.MainActivity;
import com.mikerinehart.rideguide.models.User;
import com.mikerinehart.rideguide.page_fragments.ReservationsHistoryPageFragment;
import com.mikerinehart.rideguide.page_fragments.ShiftsHistoryPageFragment;


public class MyHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private User me;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static MyHistoryFragment newInstance(User param1, String param2) {
        MyHistoryFragment fragment = new MyHistoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.toolbar.setTitle("My History");
        MainActivity.drawerAdapter.selectPosition(5);
        if (getArguments() != null) {
            me = getArguments().getParcelable("USER");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_history, container, false);

        ViewPager pager = (ViewPager) v.findViewById(R.id.home_pager);
        pager.setAdapter(new MyHistoryViewPagerAdapter(getActivity().getSupportFragmentManager()));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class MyHistoryViewPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = {"Reservations", "Shifts"};

        public MyHistoryViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position == 0) {
                return ReservationsHistoryPageFragment.newInstance(me, "ReservationsHistory");
            } else if (position == 1) {
                return ShiftsHistoryPageFragment.newInstance(me, "ShiftsHistory");
            } return new MyHistoryFragment();
        }

        public int getCount() {
            return TITLES.length;
        }
    }

}
