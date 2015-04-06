package com.mikerinehart.rideguide.main_fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.SimpleDividerItemDecoration;
import com.mikerinehart.rideguide.activities.MainActivity;
import com.mikerinehart.rideguide.adapters.UpcomingReservationsAdapter;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Ride;
import com.mikerinehart.rideguide.models.User;

import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomePageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private User me;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance(User param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.drawerAdapter.selectPosition(1);
        if (getArguments() != null) {
            me = getArguments().getParcelable("USER");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.toolbar.setTitle("Home");
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);

        return v;
    }

    private List<Reservation> createReservationList() {
        List<Reservation> result;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Type listType = new TypeToken<List<Ride>>() {
        }.getType();

        String testJson = "[{\"id\":\"2\",\"user_id\":\"2\",\"seats\":\"4\",\"start\":\"2015-02-09 18:00:00\",\"end\":\"2015-02-10 02:00:00\",\"user\":{\"id\":\"2\",\"fb_uid\":\"1493344104\",\"first_name\":\"Edward\",\"last_name\":\"Liu\"}},{\"id\":\"3\",\"user_id\":\"4\",\"seats\":\"3\",\"start\":\"2015-02-09 17:00:00\",\"end\":\"2015-02-10 03:00:00\",\"user\":{\"id\":\"4\",\"fb_uid\":\"1302213537\",\"first_name\":\"Cole\",\"last_name\":\"Menzel\"}}]";
        result = (List<Reservation>) gson.fromJson(testJson, listType);

        return result;
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

}
