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
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyShiftReservationsAdapter extends RecyclerView.Adapter<MyShiftReservationsAdapter.MyShiftReservationsViewHolder> {

    private List<Reservation> reservationList;
    private Context c;

    private final String TAG = "MyShiftReservationsAdapter";

    public MyShiftReservationsAdapter(List<Reservation> reservationList) {
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
    public void onBindViewHolder(MyShiftReservationsViewHolder reservationViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("E d, h:mma");

        Reservation r = reservationList.get(i);
        reservationViewHolder.name.setText(r.getUser().getFullName());
        reservationViewHolder.numPassengers.setText(Integer.toString(r.getPassengers()));
        reservationViewHolder.pickupTime.setText(df.format(r.getPickup_time()));
        Picasso.with(reservationViewHolder.userPic.getContext())
                .load("https://graph.facebook.com/" + r.getUser().getFbUid() + "/picture?type=large")
                .transform(new RoundedTransformation(100, 5))
                .into(reservationViewHolder.userPic);
    }

    public User getUserFromList(int i) {
        Reservation r = reservationList.get(i);
        return r.getUser();
    }

    public Reservation getReservationFromList(int i) {
        return reservationList.get(i);
    }

    @Override
    public MyShiftReservationsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.c = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myshifts_reservations_dialog_list_item, viewGroup, false);
        return new MyShiftReservationsViewHolder(itemView);
    }

    //Holds the Ride cardviews
    public static class MyShiftReservationsViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView numPassengers;
        protected TextView pickupTime;
        protected ImageView userPic;

        public MyShiftReservationsViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.myshifts_reservations_dialog_reservation_name);
            numPassengers = (TextView) v.findViewById(R.id.myshifts_reservations_dialog_num_passengers);
            pickupTime = (TextView) v.findViewById(R.id.myshifts_reservations_dialog_pickup_time);
            userPic = (ImageView) v.findViewById(R.id.myshifts_reservations_dialog_user_pic);
        }

    }

}
