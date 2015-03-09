package com.mikerinehart.rideguide.page_fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.SimpleDividerItemDecoration;
import com.mikerinehart.rideguide.adapters.AvailableRidesTimeSlotsAdapter;
import com.mikerinehart.rideguide.adapters.MyShiftsAdapter;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AvailableRidesPageFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private User me;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView noRides;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView ridesList;

    private String TAG = "AvailableRidesPageFragment";


    public static AvailableRidesPageFragment newInstance(User param1, String param2) {
        AvailableRidesPageFragment fragment = new AvailableRidesPageFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AvailableRidesPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            me = getArguments().getParcelable("USER");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_rides_available_page, container, false);

        noRides = (TextView)v.findViewById(R.id.rides_available_none);
        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.rides_available_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        ridesList = (RecyclerView)v.findViewById(R.id.rides_available_list);
        LinearLayoutManager llm = new LinearLayoutManager(ridesList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        ridesList.setLayoutManager(llm);

        refreshContent();

        return v;
    }

    private void refreshContent() {

        RestClient.post("reservations/freeReservations", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Reservation> result;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type listType = new TypeToken<List<Reservation>>() {
                }.getType();

                result = (List<Reservation>)gson.fromJson(response.toString(), listType);

                // check whether or not any rides even exist
                if (result.size() == 0) {
                    noRides.setVisibility(TextView.VISIBLE);
                    ridesList.setVisibility(RecyclerView.GONE);
                } else {
                    noRides.setVisibility(TextView.GONE);
                    ridesList.setVisibility(RecyclerView.VISIBLE);

                    List<List<Reservation>> mainList = new ArrayList<List<Reservation>>();
                    for (int i = 0; i < result.size(); i++)
                    {
                        if (mainList.isEmpty())
                        {
                            List<Reservation> newList = new ArrayList<Reservation>();
                            newList.add(result.get(i));
                            mainList.add(newList);
                        } else {
                            boolean added = false;

                            for (List<Reservation> x : mainList)
                            {
                                if (x.get(0).getPickup_time().compareTo(result.get(i).getPickup_time()) == 0)
                                {
                                    x.add(result.get(i));
                                    added = true;
                                    break;
                                }
                            }
                            if (!added)
                            {
                                List<Reservation> newList = new ArrayList<Reservation>();
                                newList.add(result.get(i));
                                mainList.add(newList);
                            }
                        }
                    }

                    AvailableRidesTimeSlotsAdapter adapter = new AvailableRidesTimeSlotsAdapter(result);

                    ridesList.addItemDecoration(new SimpleDividerItemDecoration(ridesList.getContext()));

                    ridesList.setAdapter(adapter);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, "Error: " + errorResponse);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
