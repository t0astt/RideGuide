package com.mikerinehart.navdrawertest2;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Target;

/**
 * Created by Mike on 1/19/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[];
    private int mIcons[];

    private int mSelectedPosition;

    private String myName;
    private String myEmail;
    private String myFbUid;

    Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;

        TextView textView;
        ImageView imageView;
        TextView name;
        TextView email;
        ImageView pic;

        View v;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            v = itemView;

            if (viewType == TYPE_ITEM) {
                textView = (TextView)itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderId = 1;
            } else {
                name = (TextView)itemView.findViewById(R.id.name);
                email = (TextView)itemView.findViewById(R.id.email);
                pic = (ImageView)itemView.findViewById(R.id.imageView);

                holderId = 0;
            }
        }
    }

    MyAdapter(String Titles[], int Icons[], String Name, String Email, String fbUid, Context c) {
        mNavTitles = Titles;
        mIcons = Icons;
        myName = Name;
        myEmail = Email;
        myFbUid = fbUid;
        context = c;
    }

    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
            ViewHolder vhItem = new ViewHolder(v,viewType);


            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType);

            return vhHeader;
        }
        return null;
    }

    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        if (holder.holderId == 1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);
        } else {
            Picasso.with(context).load("https://graph.facebook.com/"+ myFbUid +"/picture?type=large").transform(new RoundedTransformation(100, 5)).into(holder.pic);
            holder.name.setText((myName));
            holder.email.setText(myEmail);
        }
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

}
