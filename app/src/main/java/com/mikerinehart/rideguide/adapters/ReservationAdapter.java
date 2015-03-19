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
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private List<Reservation> reservationList;
    private Context c;

    public ReservationAdapter(List<Reservation> reservationList) {
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
    public void onBindViewHolder(ReservationViewHolder reservationViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("E d, h:mma");
        
        Reservation r = reservationList.get(i);
        reservationViewHolder.name.setText(r.getShift().getUser().getFullName());
        reservationViewHolder.numPassengers.setText(Integer.toString(r.getPassengers()));
        reservationViewHolder.pickupTime.setText(df.format(r.getPickup_time()));
        Picasso.with(reservationViewHolder.userPic.getContext())
                .load("https://graph.facebook.com/" + r.getShift().getUser().getFbUid() + "/picture?type=large")
                .transform(new RoundedTransformation(100, 5))
                .into(reservationViewHolder.userPic);
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.c = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_list_item, viewGroup, false);
        return new ReservationViewHolder(itemView);
    }

    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_view_header, parent, false);
        return new RecyclerView.ViewHolder(view) { };
    }

    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        DateFormat df = new SimpleDateFormat("EEEE, MMMMMM d");
        TextView textView = (TextView)holder.itemView;
        textView.setText(df.format(getReservationFromList(position).getPickup_time()));
    }

    @Override
    public long getHeaderId(int position) {
        DateFormat df = new SimpleDateFormat("D");
        Reservation r = getReservationFromList(position);
        Date d = r.getPickup_time();
        String s = df.format(d);
        long l = Long.parseLong(s, 10);
        return l;
    }




    public User getUserFromList(int i) {
        return reservationList.get(i).getShift().getUser();
    }

    public Reservation getReservationFromList(int i) {
        return reservationList.get(i);
    }

    //Holds the Ride cardviews
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView numPassengers;
        protected TextView pickupTime;
        protected ImageView userPic;

        public ReservationViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.reservation_name);
            numPassengers = (TextView) v.findViewById(R.id.reservation_num_passengers);
            pickupTime = (TextView) v.findViewById(R.id.reservation_pickup_time);
            userPic = (ImageView) v.findViewById(R.id.reservation_user_pic);
        }

    }

}
