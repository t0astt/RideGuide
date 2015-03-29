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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.SimpleDividerItemDecoration;
import com.mikerinehart.rideguide.activities.MainActivity;
import com.mikerinehart.rideguide.adapters.MyShiftsAdapter;
import com.mikerinehart.rideguide.main_fragments.ProfileFragment;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MyShiftsPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static User ARG_PARAM1;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private User me;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ButtonFloat newShiftButton;
    private TextView shiftShame;
    private ProgressBarCircularIndeterminate loadingIcon;
    private ButtonFloat createShiftButton;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView shiftList;

    private String TAG = "MyShiftsPageFragment";


    // TODO: Rename and change types and number of parameters
    public static MyShiftsPageFragment newInstance(User param1, String param2) {
        MyShiftsPageFragment fragment = new MyShiftsPageFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyShiftsPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "In OnCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            me = getArguments().getParcelable("USER");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.toolbar.setTitle("My Shifts");
        final View v = inflater.inflate(R.layout.fragment_my_shifts_page, container, false);
        shiftShame = (TextView)v.findViewById(R.id.myshifts_shift_shame);
        loadingIcon = (ProgressBarCircularIndeterminate)v.findViewById(R.id.myshifts_circular_loading);

        createShiftButton = (ButtonFloat)v.findViewById(R.id.myshifts_new_shift_fab);
        createShiftButton.setEnabled(true);
        createShiftButton.setRippleSpeed(100);
        createShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShiftDialog();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.myshifts_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshContent();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, MyShiftsPageFragment.newInstance(me, "ProfileFragment"))
                        .commit();
            }
        });

        shiftList = (RecyclerView) v.findViewById(R.id.myshifts_my_shifts_list);
        shiftList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(shiftList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        shiftList.setLayoutManager(llm);

        refreshContent();


        return v;
    }

    /*
     * Returns false if no list items, true if list items present
     */
    private void refreshContent() {

        RequestParams params = new RequestParams("user_id", me.getId());
        RestClient.post("shifts/myShifts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Shift> result;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type listType = new TypeToken<List<Shift>>() {
                }.getType();

                result = (List<Shift>)gson.fromJson(response.toString(), listType);

                // check whether or not to shame the user hehe
                if (result.size() == 0) {
                    shiftShame.setVisibility(TextView.VISIBLE);
                    shiftList.setVisibility(RecyclerView.GONE);
                    loadingIcon.setVisibility(ProgressBarCircularIndeterminate.GONE);
                } else {
                    shiftShame.setVisibility(TextView.GONE);
                    shiftList.setVisibility(RecyclerView.VISIBLE);
                    MyShiftsAdapter shiftsAdapter = new MyShiftsAdapter(result, me);

                    shiftList.addItemDecoration(new SimpleDividerItemDecoration(shiftList.getContext()));

                    loadingIcon.setVisibility(ProgressBarCircularIndeterminate.GONE);
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

    public void createShiftDialog() {

        final DateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd h:mma");

        MaterialDialog dialog = null;
        final EditText startTime;
        final EditText endTime;
        final EditText seats;

        LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
        View dialogLayout = inflater.inflate(R.layout.myshifts_new_shift_dialog, null);

        startTime = (EditText)dialogLayout.findViewById(R.id.myshifts_new_shift_dialog_start_time);
        endTime = (EditText)dialogLayout.findViewById(R.id.myshifts_new_shift_dialog_end_time);
        seats = (EditText)dialogLayout.findViewById(R.id.myshifts_new_shift_dialog_num_seats);

        dialog = new MaterialDialog.Builder(MyShiftsPageFragment.this.getActivity())
                .title("Create new Shift")
                .customView(dialogLayout)
                .positiveText("Create")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        RequestParams params = new RequestParams("user_id", me.getId());
                        params.put("seats", seats.getText());
                        params.put("start", startTime.getText());
                        params.put("end", endTime.getText());
                        RestClient.post("shifts", params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                //refreshContent();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, MyShiftsPageFragment.newInstance(me, "RidesFragment"))
                                        .commit();
                                Toast.makeText(getActivity().getApplicationContext(), "Shift created!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                Log.i(TAG, "Error " + statusCode + ": " + response);
                                Toast.makeText(getActivity().getApplicationContext(), "Error, please try again", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .build();
        dialog.show();

        final SlideDateTimeListener startListener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                startTime.setText(displayFormat.format(date));
            }
        };

        final SlideDateTimeListener endListener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                endTime.setText(displayFormat.format(date));
            }
        };

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = new Date();
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(d);
                calendar.add(Calendar.DATE, 7);
                new SlideDateTimePicker.Builder(getFragmentManager())
                        .setListener(startListener)
                        .setInitialDate(d)
                        .setMinDate(d)
                        .setMaxDate(calendar.getTime())
                        .build()
                        .show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = new Date();
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(d);
                calendar.add(Calendar.DATE, 7);
                new SlideDateTimePicker.Builder(getFragmentManager())
                        .setListener(endListener)
                        .setInitialDate(d)
                        .setMinDate(d)
                        .setMaxDate(calendar.getTime())
                        .build()
                        .show();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
