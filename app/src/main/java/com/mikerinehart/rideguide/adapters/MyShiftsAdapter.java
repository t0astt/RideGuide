package com.mikerinehart.rideguide.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.page_fragments.MyShiftsPageFragment;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
                Log.i("Adapter", "Reservation was clicked");
                createReservationsDialog(s.getReservations());

            }
        });

        myShiftsViewHolder.reservationsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adapter", "Reservation was clicked");
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
        MaterialDialog dialog;
        RecyclerView reservationList = (RecyclerView)dialogLayout.findViewById(R.id.myshifts_view_shift_reservations_dialog_list);


        //reservationList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(reservationList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        reservationList.setLayoutManager(llm);

        List<Reservation> resList = Arrays.asList(reservations);
        MyShiftReservationsAdapter reservationAdapter = new MyShiftReservationsAdapter(resList);
        reservationList.setAdapter(reservationAdapter);

        dialog = new MaterialDialog.Builder(c)
                .title("Reservations")
                .customView(dialogLayout)
                .positiveText("OK")
                .build();
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
