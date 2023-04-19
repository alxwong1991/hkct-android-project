package com.hkct.project.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hkct.project.Model.Attendees;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.AttendeesViewHolder> {

    private Activity context;
    private List<Users> usersList;
    private List<Attendees> attendeesList;

    public AttendeesAdapter(Activity context, List<Users> usersList, List<Attendees> attendeesList) {
        this.context = context;
        this.usersList = usersList;
        this.attendeesList = attendeesList;
    }

    @NonNull
    @Override
    public AttendeesAdapter.AttendeesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_attendee, parent, false);
        return new AttendeesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeesViewHolder holder, int position) {

        Users users = usersList.get(position);
        holder.setmUserName(users.getName());
        holder.setCircleImageView(users.getImage());
    }

    @Override
    public int getItemCount() {
        return attendeesList.size();
    }

    public class AttendeesViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName;
        CircleImageView circleImageView;
        View mView;

        public AttendeesViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setmUserName(String userName) {
            mUserName = mView.findViewById(R.id.attendee_user);
            mUserName.setText(userName);
        }

        public void setCircleImageView(String profilePic) {
            circleImageView = mView.findViewById(R.id.attendee_profile_pic);
            Glide.with(context).load(profilePic).into(circleImageView);
        }
    }
}
