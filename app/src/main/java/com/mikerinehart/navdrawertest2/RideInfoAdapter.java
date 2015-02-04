package com.mikerinehart.navdrawertest2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mike on 2/3/2015.
 */
public class RideInfoAdapter extends RecyclerView.Adapter<RideInfoAdapter.RideInfoViewHolder> {

    private List<RideInfo> rideInfoList;

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
        RideInfo r = rideInfoList.get(i);
        rideViewHolder.vName.setText(r.name);
        rideViewHolder.vSurname.setText(r.surname);
        rideViewHolder.vEmail.setText(r.email);
        rideViewHolder.vTitle.setText(r.name + " " + r.surname);
    }

    @Override
    public RideInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ride_info_list_item, viewGroup, false);
        return new RideInfoViewHolder(itemView);
    }

    //Holds the RideInfo cardviews
    public static class RideInfoViewHolder extends RecyclerView.ViewHolder {

        protected TextView vName;
        protected TextView vSurname;
        protected TextView vEmail;
        protected TextView vTitle;

        public RideInfoViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txtName);
            vSurname = (TextView)  v.findViewById(R.id.txtSurname);
            vEmail = (TextView)  v.findViewById(R.id.txtEmail);
            vTitle = (TextView) v.findViewById(R.id.title);
        }

    }

}
