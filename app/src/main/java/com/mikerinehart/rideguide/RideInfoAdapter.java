package com.mikerinehart.rideguide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public RideInfoAdapter(List<RideInfo> rideInfoList)
    {
        this.rideInfoList = rideInfoList;
    }

    @Override
    public int getItemCount() {
        return rideInfoList.size();
    }

    @Override
    public void onBindViewHolder(RideInfoViewHolder rideViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("E HH:mm");


        RideInfo r = rideInfoList.get(i);
        //Don't need to show ride ID
        //rideViewHolder.rideId.setText(Integer.toString(r.id));
        Picasso.with(c).load("https://graph.facebook.com/"+ r.user.fb_uid +"/picture?type=large").transform(new RoundedTransformation(100, 5)).into(rideViewHolder.userPic);
        rideViewHolder.name.setText(r.user.getFullName());
        rideViewHolder.startTime.setText(df.format(r.start));
        rideViewHolder.endTime.setText(df.format(r.end));
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
            name = (TextView)v.findViewById(R.id.name);
            startTime = (TextView)v.findViewById(R.id.start_time);
            endTime = (TextView)v.findViewById(R.id.end_time);
            userPic = (ImageView)v.findViewById(R.id.user_pic);
        }

    }

}
