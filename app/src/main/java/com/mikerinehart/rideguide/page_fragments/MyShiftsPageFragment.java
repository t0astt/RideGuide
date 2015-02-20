package com.mikerinehart.rideguide.page_fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
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
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyShiftsPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyShiftsPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            me = getArguments().getParcelable("USER");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_my_shifts_page, container, false);
        newShiftButton = (ButtonFloat)v.findViewById(R.id.myshifts_new_shift_fab);
        shiftShame = (TextView)v.findViewById(R.id.myshifts_shift_shame);
        loadingIcon = (ProgressBarCircularIndeterminate)v.findViewById(R.id.myshifts_circular_loading);

        createShiftButton = (ButtonFloat)v.findViewById(R.id.myshifts_new_shift_fab);
        createShiftButton.setEnabled(true);
        createShiftButton.setRippleSpeed(100);
        createShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Some shit happened");
                Log.i(TAG, getActivity().getApplicationContext().toString());
                MaterialDialog dialog = new MaterialDialog.Builder(MyShiftsPageFragment.this.getActivity())
                        .title("Create new Shift")
                        .content("Test!")
                        .positiveText("Agree")
                        .negativeText("Disagree")
                        .build();
                dialog.show();
            }
        });



        final RecyclerView shiftList = (RecyclerView) v.findViewById(R.id.myshifts_my_shifts_list);
        shiftList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(shiftList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        shiftList.setLayoutManager(llm);

        // Asynchronously load my shifts with POST
        RequestParams params = new RequestParams("user_id", me.getId());
        RestClient.post("shifts/myShifts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Shift> result;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type listType = new TypeToken<List<Shift>>() {
                }.getType();

                result = (List<Shift>)gson.fromJson(response.toString(), listType);

                loadingIcon.setVisibility(ProgressBarCircularIndeterminate.GONE);

                // check whether or not to shame the user hehe
                if (result.size() == 0) {
                    shiftShame.setVisibility(TextView.VISIBLE);
                    shiftList.setVisibility(RecyclerView.GONE);
                } else {
                    shiftList.setVisibility(RecyclerView.VISIBLE);
                    MyShiftsAdapter shiftsAdapter = new MyShiftsAdapter(result);

                    shiftList.addItemDecoration(new SimpleDividerItemDecoration(shiftList.getContext()));
                    shiftList.setAdapter(shiftsAdapter);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, "Error: " + errorResponse);
            }
        });

        return v;
    }

    public void createShift() {
        Log.i(TAG, "Some shit happened");
        MaterialDialog dialog = new MaterialDialog.Builder(this.getActivity().getApplicationContext())
                .title("Create new Shift")
                .content("Test!")
                .positiveText("Agree")
                .negativeText("Disagree")
                .build();
        dialog.show();
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
