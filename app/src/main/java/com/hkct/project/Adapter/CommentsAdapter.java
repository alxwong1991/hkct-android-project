package com.hkct.project.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hkct.project.Model.Comments;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private Activity context;
    private List<Users> usersList;
    private List<Comments> commentsList;

    public CommentsAdapter(Activity context, List<Comments> commentsList, List<Users> usersList) {
        this.context = context;
        this.commentsList = commentsList;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_comment, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comments comments = commentsList.get(position);
        holder.setmComment(comments.getComment());
        holder.setmTimeStamp(comments.getTime());

        Users users = usersList.get(position);
        holder.setmUserName(users.getName());
        holder.setCircleImageView(users.getImage());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        TextView mComment, mUserName, mTimeStamp;
        CircleImageView circleImageView;
        View mView;
        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setmComment(String comment) {
            mComment = mView.findViewById(R.id.comment_tv);
            mComment.setText(comment);
        }
        public void setmUserName(String userName) {
            mUserName = mView.findViewById(R.id.comment_user);
            mUserName.setText(userName);
        }
        public void setCircleImageView(String profilePic) {
            circleImageView = mView.findViewById(R.id.comment_profile_pic);
            Glide.with(context).load(profilePic).into(circleImageView);
        }

        public void setmTimeStamp(Date timestamp) {
            mTimeStamp = mView.findViewById(R.id.comment_timestamp);
            if (timestamp != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm a");
                String formattedDate = dateFormat.format(timestamp);
                mTimeStamp.setText(formattedDate);
            }
        }
    }
}
