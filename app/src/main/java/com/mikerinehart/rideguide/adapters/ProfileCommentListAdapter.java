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
import com.mikerinehart.rideguide.models.Comment;
import com.mikerinehart.rideguide.models.Reservation;
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileCommentListAdapter extends RecyclerView.Adapter<ProfileCommentListAdapter.ProfileCommentViewHolder> {

    private List<Comment> commentList;
    private Context context;

    public ProfileCommentListAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int getItemCount() {
        if (commentList != null)
        {
            return commentList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(ProfileCommentViewHolder reservationViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("h:mma");

        Comment c = commentList.get(i);
        reservationViewHolder.title.setText(c.getTitle());
        reservationViewHolder.date.setText(df.format(c.getCreated_at()));
        reservationViewHolder.content.setText(c.getComment());
    }

    @Override
    public ProfileCommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservation_list_item, viewGroup, false);
        return new ProfileCommentViewHolder(itemView);
    }


    //Holds the Ride cardviews
    public static class ProfileCommentViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView date;
        protected TextView content;

        public ProfileCommentViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.profile_comment_list_title);
            date = (TextView) v.findViewById(R.id.profile_comment_list_date);
            content = (TextView) v.findViewById(R.id.profile_comment_list_content);
        }

    }

}
