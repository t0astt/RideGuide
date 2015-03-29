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
import com.mikerinehart.rideguide.adapters.MyShiftsAdapter;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class ShiftsHistoryPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static User ARG_PARAM1;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private User me;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView shiftShame;
    private ProgressBarCircularIndeterminate loadingIcon;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView shiftList;

    private String TAG = "MyShiftsPageFragment";

    public static ShiftsHistoryPageFragment newInstance(User param1, String param2) {
        ShiftsHistoryPageFragment fragment = new ShiftsHistoryPageFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ShiftsHistoryPageFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_shifts_history_page, container, false);
        shiftShame = (TextView)v.findViewById(R.id.myshifts_history_shift_shame);
        loadingIcon = (ProgressBarCircularIndeterminate)v.findViewById(R.id.myshifts_history_circular_loading);

        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.myshifts_history_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                //refreshContent();
            }
        });

        shiftList = (RecyclerView) v.findViewById(R.id.myshifts_history_my_shifts_list);
        shiftList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(shiftList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        shiftList.setLayoutManager(llm);

        refreshContent();
        loadingIcon.setVisibility(ProgressBarCircularIndeterminate.GONE);

        return v;
    }

    /*
     * Returns false if no list items, true if list items present
     */
    private void refreshContent() {

        RequestParams params = new RequestParams("user_id", me.getId());
        RestClient.post("shifts/myPastShifts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Shift> result;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type listType = new TypeToken<List<Shift>>() {
                }.getType();

                result = (List<Shift>) gson.fromJson(response.toString(), listType);

                // check whether or not to shame the user hehe
                if (result.size() == 0) {
                    shiftShame.setVisibility(TextView.VISIBLE);
                    shiftList.setVisibility(RecyclerView.GONE);
                } else {
                    shiftShame.setVisibility(TextView.GONE);
                    shiftList.setVisibility(RecyclerView.VISIBLE);
                    MyShiftsAdapter shiftsAdapter = new MyShiftsAdapter(result, me);

                    shiftList.addItemDecoration(new SimpleDividerItemDecoration(shiftList.getContext()));

                    shiftList.setAdapter(shiftsAdapter);
                    StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(shiftsAdapter);
                    shiftList.addItemDecoration(headersDecor);
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
