package com.mikerinehart.rideguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.models.Ride;
import com.mikerinehart.rideguide.models.Shift;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Mike on 2/3/2015.
 */
public class MyShiftsAdapter extends RecyclerView.Adapter<MyShiftsAdapter.MyShiftsViewHolder> {

    private List<Shift> shiftList;
    private Context c;

    public MyShiftsAdapter(List<Ride> rideList) {
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
    public void onBindViewHolder(MyShiftsViewHolder myShiftsViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("E d, h:mma");

        Shift s = shiftList.get(i);
        myShiftsViewHolder.startTime.setText(df.format(s.getStart()));
        myShiftsViewHolder.endTime.setText(df.format(s.getEnd()));
        myShiftsViewHolder.reservations.setText(Integer.toString(s.getReservationCount()));
        myShiftsViewHolder.seats.setText(Integer.toString(s.getSeats()));
    }

    @Override
    public MyShiftsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.c = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ride_info_list_item, viewGroup, false);
        return new MyShiftsViewHolder(itemView);
    }

    //Holds the Ride cardviews
    public static class MyShiftsViewHolder extends RecyclerView.ViewHolder {

        protected TextView startTime;
        protected TextView endTime;
        protected TextView reservations;
        protected TextView seats;

        public MyShiftsViewHolder(View v) {
            super(v);
            startTime = (TextView) v.findViewById(R.id.start_time);
            endTime = (TextView) v.findViewById(R.id.end_time);
            reservations = (TextView) v.findViewById(R.id.num_reservations);
            seats = (TextView) v.findViewById(R.id.num_seats);
        }

    }

}
