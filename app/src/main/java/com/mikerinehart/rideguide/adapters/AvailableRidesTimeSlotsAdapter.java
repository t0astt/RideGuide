package com.mikerinehart.rideguide.adapters;

import android.content.Context;
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
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.main_fragments.ProfileFragment;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;
import com.mikerinehart.rideguide.page_fragments.MyShiftsPageFragment;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AvailableRidesTimeSlotsAdapter extends RecyclerView.Adapter<AvailableRidesTimeSlotsAdapter.AvailableRidesTimeSlotsViewHolder> {

    private List<List<Reservation>> ridesList;
    private Context c;

    private final String TAG = "MyShiftsAdapter";

    public AvailableRidesTimeSlotsAdapter(List<List<Reservation>> ridesList) {
        this.ridesList = ridesList;
    }

    @Override
    public int getItemCount() {
        if (ridesList != null)
        {
            return ridesList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(final AvailableRidesTimeSlotsViewHolder vh, int i) {
        DateFormat df = new SimpleDateFormat("E d, h:mma");

        final List<Reservation> r = ridesList.get(i);
        vh.pickupTime.setText(df.format(r.get(0).getPickup_time()));
        vh.numRides.setText(Integer.toString(r.size()));

    }

    @Override
    public AvailableRidesTimeSlotsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.c = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rides_available_timeslot_list_item, viewGroup, false);
        return new AvailableRidesTimeSlotsViewHolder(itemView);
    }

    public List<Reservation> getDrivers(int i)
    {
        return ridesList.get(i);
    }


    public static class AvailableRidesTimeSlotsViewHolder extends RecyclerView.ViewHolder {

        protected TextView pickupTime;
        protected TextView numRides;

        public AvailableRidesTimeSlotsViewHolder(View v) {
            super(v);
            pickupTime = (TextView) v.findViewById(R.id.rides_available_timeslot_pickup_time);
            numRides = (TextView) v.findViewById(R.id.rides_available_timeslot_num_rides);

        }
    }

}
