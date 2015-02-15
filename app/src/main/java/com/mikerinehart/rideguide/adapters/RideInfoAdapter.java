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
import com.mikerinehart.rideguide.models.RideInfo;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Mike on 2/3/2015.
 */
public class RideInfoAdapter extends RecyclerView.Adapter<RideInfoAdapter.RideInfoViewHolder> {

    private List<RideInfo> rideInfoList;
    private Context c;

    public RideInfoAdapter(List<RideInfo> rideInfoList) {
        this.rideInfoList = rideInfoList;
    }

    @Override
    public int getItemCount() {
        return rideInfoList.size();
    }

    @Override
    public void onBindViewHolder(RideInfoViewHolder rideViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("E d, h:mma");

        RideInfo r = rideInfoList.get(i);
        Picasso.with(c).load("https://graph.facebook.com/" + r.user.getFbUid() + "/picture?type=large")
                .transform(new RoundedTransformation(100, 5))
                .into(rideViewHolder.userPic);
        rideViewHolder.name.setText(r.user.getFullName());
        rideViewHolder.startTime.setText(df.format(r.getStart()));
        rideViewHolder.endTime.setText(df.format(r.getEnd()));
        rideViewHolder.seats.setText(Integer.toString(r.getSeats()));
    }

    @Override
    public RideInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.c = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ride_info_list_item, viewGroup, false);
        return new RideInfoViewHolder(itemView);
    }

    //Holds the RideInfo cardviews
    public static class RideInfoViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView startTime;
        protected TextView endTime;
        protected TextView seats;
        protected ImageView userPic;

        public RideInfoViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            startTime = (TextView) v.findViewById(R.id.start_time);
            endTime = (TextView) v.findViewById(R.id.end_time);
            userPic = (ImageView) v.findViewById(R.id.user_pic);
            seats = (TextView) v.findViewById(R.id.num_seats);
        }

    }

}
