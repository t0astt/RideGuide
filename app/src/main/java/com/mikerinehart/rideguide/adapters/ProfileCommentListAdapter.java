package com.mikerinehart.rideguide.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.models.Comment;
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ProfileCommentListAdapter extends RecyclerView.Adapter<ProfileCommentListAdapter.ProfileCommentViewHolder> {

    private List<Comment> commentList;
    private User me;
    private Context context;

    public ProfileCommentListAdapter(List<Comment> commentList, User me) {
        this.commentList = commentList;
        this.me = me;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public void onBindViewHolder(ProfileCommentViewHolder profileCommentViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");

        Comment c = commentList.get(i);
        profileCommentViewHolder.title.setText(c.getTitle());
        profileCommentViewHolder.date.setText(df.format(c.getCreated_at()));
        profileCommentViewHolder.content.setText("\"" + c.getComment() + "\"");
        if (c.getReviewer_user_id() == me.getId()) {
            profileCommentViewHolder.userPic.setVisibility(ImageView.VISIBLE);
            Picasso.with(profileCommentViewHolder.userPic.getContext())
                    .load("https://graph.facebook.com/" + c.getUser().getFbUid() + "/picture?type=large")
                    .transform(new RoundedTransformation(100, 2))
                    .into(profileCommentViewHolder.userPic);
            profileCommentViewHolder.listItemLayout.setBackgroundColor(context.getResources().getColor(R.color.background_material_light));
        }
    }

    @Override
    public ProfileCommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_item, viewGroup, false);
        return new ProfileCommentViewHolder(itemView);
    }


    //Holds the Ride cardviews
    public static class ProfileCommentViewHolder extends RecyclerView.ViewHolder {

        protected RelativeLayout listItemLayout;
        protected TextView title;
        protected TextView date;
        protected TextView content;
        protected ImageView userPic;

        public ProfileCommentViewHolder(View v) {
            super(v);
            listItemLayout = (RelativeLayout)v.findViewById(R.id.profile_comment_list_layout);
            title = (TextView) v.findViewById(R.id.profile_comment_list_title);
            date = (TextView) v.findViewById(R.id.profile_comment_list_date);
            content = (TextView) v.findViewById(R.id.profile_comment_list_content);
            userPic = (ImageView)v.findViewById(R.id.profile_comment_list_user_pic);
        }

    }

}
