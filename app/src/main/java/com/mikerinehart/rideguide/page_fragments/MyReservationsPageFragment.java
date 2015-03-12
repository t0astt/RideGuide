package com.mikerinehart.rideguide.page_fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.SimpleDividerItemDecoration;
import com.mikerinehart.rideguide.activities.MainActivity;
import com.mikerinehart.rideguide.adapters.ReservationAdapter;
import com.mikerinehart.rideguide.main_fragments.AboutFragment;
import com.mikerinehart.rideguide.main_fragments.HomeFragment;
import com.mikerinehart.rideguide.main_fragments.ProfileFragment;
import com.mikerinehart.rideguide.main_fragments.RidesFragment;
import com.mikerinehart.rideguide.main_fragments.SettingsFragment;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyReservationsPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyReservationsPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyReservationsPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static User ARG_PARAM1;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private User me;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ProgressBarCircularIndeterminate loadingIcon;
    private TextView reservationFrowny;
    private TextView reservationNoneFound;
    private ReservationAdapter reservationAdapter;

    private String TAG = "MyReservationsPageFragment";

    public static MyReservationsPageFragment newInstance(User param1, String param2) {
        MyReservationsPageFragment fragment = new MyReservationsPageFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyReservationsPageFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_reservations_page, container, false);
        loadingIcon = (ProgressBarCircularIndeterminate)v.findViewById(R.id.reservation_circular_loading);

        final RecyclerView reservationList = (RecyclerView) v.findViewById(R.id.reservation_list);
        reservationList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(reservationList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        reservationList.setLayoutManager(llm);

        // Asynchronously load reservations with POST
        RequestParams params = new RequestParams("user_id", me.getId());
        RestClient.post("reservations/myReservations", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                List<Reservation> result;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type listType = new TypeToken<List<Reservation>>() {
                }.getType();

                result = (List<Reservation>) gson.fromJson(response.toString(), listType);

                loadingIcon.setVisibility(ProgressBarCircularIndeterminate.GONE);

                // check whether or not to shame the user hehe
                if (result == null || result.size() == 0) {
                    reservationNoneFound = (TextView)v.findViewById(R.id.reservation_none_found);
                    reservationFrowny = (TextView)v.findViewById(R.id.reservation_frowny);

                    reservationNoneFound.setVisibility(TextView.VISIBLE);
                    reservationFrowny.setVisibility(TextView.VISIBLE);
                    reservationList.setVisibility(RecyclerView.GONE);
                } else {
                    reservationList.setVisibility(RecyclerView.VISIBLE);

                    reservationAdapter = new ReservationAdapter(result);

                    reservationList.addItemDecoration(new SimpleDividerItemDecoration(reservationList.getContext()));
                    reservationList.setAdapter(reservationAdapter);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, "Error: " + errorResponse);
            }
        });

        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        reservationList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                ViewGroup child = (ViewGroup) recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int itemClicked = recyclerView.getChildPosition(child);

                    final LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
                    View dialogLayout = inflater.inflate(R.layout.user_actions_dialog, null);

                    final User u = reservationAdapter.getUserFromList(itemClicked);

                    ImageView userPic = (ImageView)dialogLayout.findViewById(R.id.user_actions_dialog_user_pic);
                    TextView firstName = (TextView)dialogLayout.findViewById(R.id.user_actions_dialog_first_name);
                    TextView lastName = (TextView)dialogLayout.findViewById(R.id.user_actions_dialog_last_name);
                    com.gc.materialdesign.views.ButtonRectangle callUserButton = (ButtonRectangle)dialogLayout.findViewById(R.id.user_actions_dialog_call_user_button);


                    Picasso.with(userPic.getContext())
                            .load("https://graph.facebook.com/" + u.getFbUid() + "/picture?height=1000&type=large&width=1000")
                            .transform(new RoundedTransformation(600, 5))
                            .into(userPic);
                    firstName.setText(u.getFirstName());
                    lastName.setText(u.getLastName());
                    callUserButton.setText("CALL " + u.getFirstName().toUpperCase());
                    callUserButton.setRippleSpeed(9001); // IT'S OVER 9000!!!

                    final MaterialDialog userActionsDialog = new MaterialDialog.Builder(MyReservationsPageFragment.this.getActivity())
                            .title("")
                            .customView(dialogLayout)
                            .negativeText("Cancel")
                            .positiveText("Create")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" + u.getPhone()));
                                    startActivity(intent);
                                }
                            })
                            .build();
                    userActionsDialog.show();

                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                Log.i(TAG, "TouchEvent");
            }
        });

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

}
