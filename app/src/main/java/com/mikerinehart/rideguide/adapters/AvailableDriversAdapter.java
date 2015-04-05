package com.mikerinehart.rideguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AvailableDriversAdapter extends RecyclerView.Adapter<AvailableDriversAdapter.AvailableDriversAdapterViewHolder> {

    private List<Reservation> reservationList;
    private Context c;

    private final String TAG = "AvailableDriversAdapter";

    public AvailableDriversAdapter(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    @Override
    public int getItemCount() {
        if (reservationList != null)
        {
            return reservationList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(AvailableDriversAdapterViewHolder vh, int i) {
        DateFormat df = new SimpleDateFormat("E d, h:mma");

        Reservation r = reservationList.get(i);
        vh.name.setText(r.getShift().getUser().getFullName());
        vh.numSeats.setText(Integer.toString(r.getShift().getSeats()));
        Picasso.with(vh.userPic.getContext())
                .load("https://graph.facebook.com/" + r.getShift().getUser().getFbUid() + "/picture?type=large")
                .transform(new RoundedTransformation(100, 5))
                .into(vh.userPic);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adapter", "Click");
            }
        });
    }

    public Reservation getReservation(int i) {
        Reservation r = reservationList.get(i);
        return r;
    }

    @Override
    public AvailableDriversAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.c = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rides_available_view_drivers_dialog_list_item, viewGroup, false);
        return new AvailableDriversAdapterViewHolder(itemView);
    }

    //Holds the Ride cardviews
    public static class AvailableDriversAdapterViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView numSeats;
        protected ImageView userPic;
        protected TextView thumbUpCount;
        protected TextView thumbDownCount;

        public AvailableDriversAdapterViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.rides_available_view_drivers_dialog_user_name);
            numSeats = (TextView)v.findViewById(R.id.rides_available_view_drivers_dialog_num_seats);
            userPic = (ImageView) v.findViewById(R.id.rides_available_view_drivers_dialog_user_pic);
            thumbUpCount = (TextView)v.findViewById(R.id.rides_available_view_drivers_dialog_thumbup_count);
            thumbDownCount = (TextView)v.findViewById(R.id.rides_available_view_drivers_dialog_thumbdown_count);
        }

    }

}
