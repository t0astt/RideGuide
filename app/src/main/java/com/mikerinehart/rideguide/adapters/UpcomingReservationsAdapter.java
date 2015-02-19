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
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Shift;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Mike on 2/3/2015.
 */
public class UpcomingReservationsAdapter extends RecyclerView.Adapter<UpcomingReservationsAdapter.UpcomingReservationsViewHolder> {

    private List<Reservation> reservationList;
    private Context c;

    public UpcomingReservationsAdapter(List<Reservation> reservationList) {
        this.reservationList = this.reservationList;
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
    public void onBindViewHolder(UpcomingReservationsViewHolder upcomingReservationsViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("E d, h:mma");

        Reservation r = reservationList.get(i);
        upcomingReservationsViewHolder.name.setText(r.getShift().getUser().getFullName());
        upcomingReservationsViewHolder.pickupTime.setText(df.format(r.getPickup_time()));
        upcomingReservationsViewHolder.passengers.setText(Integer.toString(r.getPassengers()));
        Picasso.with(upcomingReservationsViewHolder.userPic.getContext())
                .load("https://graph.facebook.com/" + r.getShift().getUser().getFbUid() + "/picture?type=large")
                .transform(new RoundedTransformation(100, 5))
                .into(upcomingReservationsViewHolder.userPic);
    }

    @Override
    public UpcomingReservationsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.c = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ride_info_list_item, viewGroup, false);
        return new UpcomingReservationsViewHolder(itemView);
    }

    //Holds the Ride cardviews
    public static class UpcomingReservationsViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView pickupTime;
        protected TextView passengers;
        protected ImageView userPic;

        public UpcomingReservationsViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.reservation_name);
            pickupTime = (TextView) v.findViewById(R.id.reservation_pickup_time);
            passengers = (TextView) v.findViewById(R.id.reservation_num_passengers);
            userPic = (ImageView) v.findViewById(R.id.reservation_user_pic);
        }

    }

}
