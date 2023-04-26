package com.hkct.project.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hkct.project.Model.Chat;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Activity context;
    private List<Chat> chatList;
    private List<Users> usersList;

    public ChatAdapter(Activity context, List<Chat> chatList, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.setmUserName(chat.getUser());
        holder.setmChatMessage(chat.getMessage());
        holder.setmChatTimestamp(chat.getTimestamp());

        Users users = usersList.get(position);
        holder.setmUserName(users.getName());
        holder.setCircleImageView(users.getImage());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView mChatMessage, mUserName, mChatTimestamp;
        CircleImageView circleImageView;
        View mView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCircleImageView(String profilePic) {
            circleImageView = mView.findViewById(R.id.chat_profile_pic);
            Glide.with(context).load(profilePic).into(circleImageView);
        }

        public void setmUserName(String username) {
            mUserName = mView.findViewById(R.id.chat_username);
            mUserName.setText(username);
        }

        public void setmChatMessage(String message) {
            mChatMessage = mView.findViewById(R.id.chat_message_tv);
            mChatMessage.setText(message);
        }

        public void setmChatTimestamp(Date timestamp) {
            mChatTimestamp = mView.findViewById(R.id.chat_timestamp);
            if (timestamp != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm a");
                String formattedDate = dateFormat.format(timestamp);
                mChatTimestamp.setText(formattedDate);
            }
        }
    }
}


