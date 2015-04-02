package com.mikerinehart.rideguide.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mike on 1/19/2015.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    Context context;
    private String mNavTitles[];
    private int mIcons[];
    private int mSelectedPosition;
    User me;
    private String myName;
    private String myEmail;
    private String myFbUid;
    private String coverPhotoSource;

    public DrawerAdapter(String Titles[], int Icons[], User user, String Name, String Email, String fbUid, Context c) {
        mNavTitles = Titles;
        mIcons = Icons;
        me = user;
        myName = Name;
        myEmail = Email;
        myFbUid = fbUid;
        context = c;
    }

    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType);


            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType);

            return vhHeader;
        }
        return null;
    }

    public void onBindViewHolder(final DrawerAdapter.ViewHolder holder, int position) {
        if (holder.holderId == 1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);
        } else {
            // Get cover photo with the jankass Graph API call.
            RestClient.fbGet(me.getFbUid() + "?fields=cover", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        coverPhotoSource = response.getJSONObject("cover").getString("source");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Picasso.with(holder.background.getContext())
                            .load(coverPhotoSource)
                            .into(holder.background);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i("ProfileFragment", "Error: " + errorResponse);
                }
            });
            Picasso.with(context).load("https://graph.facebook.com/" + myFbUid + "/picture?type=large").into(holder.pic);
            holder.name.setText((myName));
            holder.email.setText(myEmail);

        }
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adapter", "Click");
            }
        });
        holder.v.setSelected(mSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return (mNavTitles.length + 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void selectPosition(int position) {
        notifyItemChanged(mSelectedPosition); // unset the old one
        mSelectedPosition = position;
        notifyItemChanged(position); //and set the new one
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;

        TextView textView;
        ImageView imageView;
        TextView name;
        TextView email;
        RoundedImageView pic;
        ImageView background;

        View v;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            v = itemView;

            if (viewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderId = 1;
            } else {
                name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                background = (ImageView)itemView.findViewById(R.id.imageView3);
                pic = (RoundedImageView) itemView.findViewById(R.id.imageView);
                pic.setBorderWidth((float)5);
                pic.setBorderColor(Color.WHITE);

                holderId = 0;
            }
        }
    }

}
