package com.hkct.project.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.LikeViewHolder> {

    private Activity context;
    private List<Users> usersList;

    public LikeAdapter(Activity context, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public LikeAdapter.LikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate()
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LikeAdapter.LikeViewHolder holder, int position) {
        Users users = usersList.get(position);

        holder.setmUserName(users.getName());
        holder.setCircleImageView(users.getImage());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class LikeViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName;
        CircleImageView circleImageView;
        View mView;

        public LikeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setmUserName(String userName) {
            mUserName = mView.findViewById(R.id.like_user);
            mUserName.setText(userName);
        }

        public void setCircleImageView(String profilePic) {
            circleImageView = mView.findViewById(R.id.like_profile_pic);
            Glide.with(context).load(profilePic).into(circleImageView);
        }
    }
}
