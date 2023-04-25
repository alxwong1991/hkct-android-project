package com.hkct.project.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hkct.project.Model.Messages;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private Activity context;
//    private String Uid;
//    private FirebaseAuth auth;
//    private FirebaseFirestore firestore;
    private List<Users> usersList;
    private List<Messages> messagesList;

    public MessagesAdapter(Activity context, List<Messages> messagesList, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_message, parent, false);
//        auth = FirebaseAuth.getInstance();
//        firestore = FirebaseFirestore.getInstance();
//        Uid = auth.getCurrentUser().getUid();
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessagesViewHolder holder, int position) {
//        String currentUserId = auth.getCurrentUser().getUid();
        Messages userMessages = messagesList.get(position);
        holder.setmMessage(userMessages.getMessage());
        holder.setmMessageTimestamp(userMessages.getTimestamp());

        Users users = usersList.get(position);
        holder.setmUserName(users.getName());
        holder.setCircleImageView(users.getImage());

//        if (currentUserId.equals(userMessages.getUser())) {
//            holder.mMessageDeleteBtn.setVisibility(View.VISIBLE);
//            holder.mMessageDeleteBtn.setClickable(true);
//            holder.mMessageDeleteBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
//                    alert.setTitle("Delete Message")
//                            .setMessage("Are you sure?")
//                            .setNegativeButton("No", null)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    String messageId = userMessages.getMessageId();
//                                    firestore.collection("Events/" + eventId + "/Messages" + messageId).document().delete()
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    Toast.makeText(context, "Message deleted successfully", Toast.LENGTH_SHORT).show();
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(context, "Error deleting message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                }
//                            })
//                            .show();
//                }
//            });
//        } else {
//            holder.mMessageDeleteBtn.setVisibility(View.INVISIBLE);
//            holder.mMessageDeleteBtn.setClickable(false);
//        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {
        TextView mMessage, mUserName, mMessageTimestamp;
//        ImageView mMessageDeleteBtn;
        CircleImageView circleImageView;
        View mView;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
//            mMessageDeleteBtn = mView.findViewById(R.id.delete_message_btn);
        }

        public void setCircleImageView(String profilePic) {
            circleImageView = mView.findViewById(R.id.message_profile_pic);
            Glide.with(context).load(profilePic).into(circleImageView);
        }

        public void setmMessage(String message) {
            mMessage = mView.findViewById(R.id.message_tv);
            mMessage.setText(message);
        }

        public void setmUserName(String username) {
            mUserName = mView.findViewById(R.id.message_user);
            mUserName.setText(username);
        }

        public void setmMessageTimestamp(Date timestamp) {
            mMessageTimestamp = mView.findViewById(R.id.message_timestamp);
            if (timestamp != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm a");
                String formattedDate = dateFormat.format(timestamp);
                mMessageTimestamp.setText(formattedDate);
            }
        }
    }
}
