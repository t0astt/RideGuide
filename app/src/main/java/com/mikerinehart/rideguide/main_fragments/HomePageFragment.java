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
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.SimpleDividerItemDecoration;
import com.mikerinehart.rideguide.activities.Constants;
import com.mikerinehart.rideguide.activities.MainActivity;
import com.mikerinehart.rideguide.adapters.NotificationsAdapter;
import com.mikerinehart.rideguide.models.Notification;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Ride;
import com.mikerinehart.rideguide.models.User;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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

    SharedPreferences sp;
    private boolean showNotificationShowcase;
    SharedPreferences notificationSP;

    RecyclerView notificationList;
    TextView noNotifications;

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
        MainActivity.toolbar.setTitle("Home");
        if (getArguments() != null) {
            me = getArguments().getParcelable("USER");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        sp = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);

        return v;
    }

//    private void refreshContent() {
//        Log.i("HomePage", "refresh");
//        notificationSP = getActivity().getSharedPreferences(Constants.NOTIFICATIONS, Context.MODE_PRIVATE);
//        ArrayList<Notification> notificationMessageList = new ArrayList<Notification>();
//        Map<String, ?> map = notificationSP.getAll();
//        for (Map.Entry<String, ?> entry : map.entrySet()) {
//            notificationMessageList.add(new Notification(entry.getValue().toString())); // TODO: Check
//        }
//        if (notificationMessageList != null && notificationMessageList.size() > 0) {
//            noNotifications.setVisibility(TextView.GONE);
//            NotificationsAdapter na = new NotificationsAdapter(notificationMessageList);
//            notificationList.setAdapter(na);
//            notificationList.addItemDecoration(new SimpleDividerItemDecoration(notificationList.getContext()));
//        } else {
//            notificationList.setAdapter(null);
//            noNotifications.setVisibility(TextView.VISIBLE);
//        }
//    }

    public void onResume() {
        super.onResume();
        MainActivity.toolbar.setTitle("Home");
        Log.i("HomePage", "onresume");
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
