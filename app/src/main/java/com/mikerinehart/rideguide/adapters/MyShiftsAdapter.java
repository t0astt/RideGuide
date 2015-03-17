package com.mikerinehart.rideguide.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonRectangle;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.main_fragments.ProfileFragment;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;
import com.mikerinehart.rideguide.page_fragments.MyShiftsPageFragment;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MyShiftsAdapter extends RecyclerView.Adapter<MyShiftsAdapter.MyShiftsViewHolder> {

    private List<Shift> shiftList;
    private Context c;

    private final String TAG = "MyShiftsAdapter";

    public MyShiftsAdapter(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    @Override
    public int getItemCount() {
        if (shiftList != null)
        {
            return shiftList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(final MyShiftsViewHolder myShiftsViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("E d, h:mma");

        final Shift s = shiftList.get(i);
        myShiftsViewHolder.startTime.setText(df.format(s.getStart()));
        myShiftsViewHolder.endTime.setText(df.format(s.getEnd()));
        myShiftsViewHolder.reservations.setText(Integer.toString(s.getReservationCount()));
        myShiftsViewHolder.seats.setText(Integer.toString(s.getSeats()));

        myShiftsViewHolder.startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adapter", "Edit Shift");
            }
        });

        myShiftsViewHolder.endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adapter", "Edit Shift");
            }
        });

        myShiftsViewHolder.seats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adapter", "Edit Shift");
            }
        });

        myShiftsViewHolder.numSeatsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adapter", "Edit Shift");
            }
        });

        myShiftsViewHolder.reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReservationsDialog(s.getReservations());

            }
        });

        myShiftsViewHolder.reservationsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReservationsDialog(s.getReservations());
            }
        });
    }

    @Override
    public MyShiftsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.c = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myshifts_list_item, viewGroup, false);
        return new MyShiftsViewHolder(itemView);
    }

    public void createReservationsDialog(Reservation[] reservations) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View dialogLayout = inflater.inflate(R.layout.myshifts_view_shift_reservations_dialog, null);
        final MaterialDialog dialog = new MaterialDialog.Builder(c)
                                .title("Reservations")
                                .customView(dialogLayout)
                                .positiveText("OK")
                                .build();
        RecyclerView reservationList = (RecyclerView)dialogLayout.findViewById(R.id.myshifts_view_shift_reservations_dialog_list);

        LinearLayoutManager llm = new LinearLayoutManager(reservationList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        reservationList.setLayoutManager(llm);

        List<Reservation> resList = Arrays.asList(reservations);
        final MyShiftReservationsAdapter reservationAdapter = new MyShiftReservationsAdapter(resList);
        reservationList.setAdapter(reservationAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(c, new GestureDetector.SimpleOnGestureListener() {
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
                    dialog.dismiss();
                    int itemClicked = recyclerView.getChildPosition(child);

                    final LayoutInflater inflater = LayoutInflater.from(c);
                    View dialogLayout = inflater.inflate(R.layout.user_actions_dialog, null);

                    DateFormat df = new SimpleDateFormat("E d, h:mma");

                    final User u = reservationAdapter.getUserFromList(itemClicked);
                    final Reservation r = reservationAdapter.getReservationFromList(itemClicked);

                    ImageView userPic = (ImageView)dialogLayout.findViewById(R.id.user_actions_dialog_user_pic);
                    TextView firstName = (TextView)dialogLayout.findViewById(R.id.user_actions_dialog_first_name);
                    TextView lastName = (TextView)dialogLayout.findViewById(R.id.user_actions_dialog_last_name);
                    TextView pickupOrigin = (TextView)dialogLayout.findViewById(R.id.user_actions_dialog_pickup_origin);
                    TextView pickupDestination = (TextView)dialogLayout.findViewById(R.id.user_actions_dialog_destination);
                    TextView pickupTime = (TextView)dialogLayout.findViewById(R.id.user_actions_dialog_pickup_time);
                    com.gc.materialdesign.views.ButtonRectangle callUserButton = (ButtonRectangle)dialogLayout.findViewById(R.id.user_actions_dialog_call_user_button);


                    Picasso.with(userPic.getContext())
                            .load("https://graph.facebook.com/" + u.getFbUid() + "/picture?height=1000&type=large&width=1000")
                            .transform(new RoundedTransformation(600, 5))
                            .into(userPic);
                    firstName.setText(u.getFirstName());
                    lastName.setText(u.getLastName());
                    pickupOrigin.setText(r.getOrigin());
                    pickupDestination.setText(r.getDestination());
                    pickupTime.setText(df.format(r.getPickup_time()));
                    callUserButton.setText("CALL " + u.getFirstName().toUpperCase());
                    callUserButton.setRippleSpeed(9001); // IT'S OVER 9000!!!

                    callUserButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + u.getPhone()));
                            c.startActivity(intent); // MAY BORK SHIT
                        }
                    });

                    final MaterialDialog userActionsDialog = new MaterialDialog.Builder((FragmentActivity)c)
                            .title("")
                            .customView(dialogLayout)
                            .neutralText("Delete Reservation")
                            .neutralColor(c.getResources().getColor(R.color.ColorNegative))
                            .positiveText("Ok")
                            .callback(new MaterialDialog.ButtonCallback() {

                                @Override
                                public void onNeutral(MaterialDialog dialog) {
                                    MaterialDialog confirmDeleteDialog = new MaterialDialog.Builder((FragmentActivity)c)
                                            .title("Confirm Reservation Deletion")
                                            .content("Are you sure you want to delete " + u.getFirstName() + "'s reservation?")
                                            .positiveText("Yes")
                                            .negativeText("Cancel")
                                            .callback(new MaterialDialog.ButtonCallback() {
                                                public void onPositive(MaterialDialog materialDialog) {

                                                    RequestParams params = new RequestParams("reservation_id", r.getId());
                                                    RestClient.post("reservations/cancelUserReservation", params, new JsonHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                            try {
                                                                if (response.getString("status").equalsIgnoreCase("success")) {
                                                                    Toast.makeText(((FragmentActivity)c), "Reservation deleted!", Toast.LENGTH_LONG).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                            //Toast.makeText(getActivity().getBaseContext(), "Network error, please try again", Toast.LENGTH_LONG);
                                                        }
                                                    });
                                                }
                                            })
                                            .build();
                                    confirmDeleteDialog.show();
                                }
                            })
                            .build();
                    userActionsDialog.show();
                    userPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userActionsDialog.dismiss();
                            ((FragmentActivity)c).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, ProfileFragment.newInstance(u, "ProfileFragment"))
                                    .addToBackStack("MyShifts")
                                    .commit();
                        }
                    });
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                Log.i(TAG, "TouchEvent");
            }
        });

        dialog.show();
    }

    public static class MyShiftsViewHolder extends RecyclerView.ViewHolder {

        protected TextView startTime;
        protected TextView endTime;
        protected TextView reservations;
        protected TextView reservationsLabel;
        protected TextView numSeatsLabel;
        protected TextView seats;

        public MyShiftsViewHolder(View v) {
            super(v);
            startTime = (TextView) v.findViewById(R.id.myshifts_start_time);
            endTime = (TextView) v.findViewById(R.id.myshifts_end_time);
            reservations = (TextView) v.findViewById(R.id.myshifts_num_reservations);
            reservationsLabel = (TextView)v.findViewById(R.id.myshifts_num_reservations_label);
            numSeatsLabel = (TextView)v.findViewById(R.id.myshifts_num_seats_label);
            seats = (TextView) v.findViewById(R.id.myshifts_num_seats);

        }
    }

}
