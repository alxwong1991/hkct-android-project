package com.hkct.project.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.ChatSellerActivity;
import com.hkct.project.CommentsActivity;
import com.hkct.project.MessageHostActivity;
import com.hkct.project.Model.Event;
import com.hkct.project.Model.Notification;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Activity context;
    private List<Users> usersList;
    private List<Notification> notificationList;
    private FirebaseFirestore firestore;

    public NotificationAdapter(Activity context, List<Users> usersList, List<Notification> notificationList) {
        this.context = context;
        this.usersList = usersList;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.setmNotificationType(notification.getType());
        holder.setmNotificationTitle(notification.getTitle());
        holder.setmNotificationTimeStamp(notification.getTime());

        Users users = usersList.get(position);
        if (users != null) {
            holder.setmNotificationSender(users.getName());
        }

        String notificationId = notification.NotificationId;
        holder.mNotificationDelBtn.setClickable(true);
        holder.mNotificationDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("Notifications").document(notificationId).delete();
                notificationList.remove(position);
                notifyDataSetChanged();
            }
        });

        // check which notification type
        // get a reference to the ConstraintLayout
        ConstraintLayout layout = holder.itemView.findViewById(R.id.notification_background);

        String notificationType = notification.getType();
        String notificationRef = notification.getReference();
        firestore = FirebaseFirestore.getInstance();
        holder.mNotificationType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (notificationType.toLowerCase()) {
                    case "event":
                        firestore.collection("Events").document(notificationRef).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        Intent messageHostIntent = new Intent(context, MessageHostActivity.class);
                                        messageHostIntent.putExtra("eventId", notificationRef);
                                        context.startActivity(messageHostIntent);
                                        // update the background color
                                        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.ivory_black));
                                    } else {
                                        Toast.makeText(context, "No matching document found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Error getting document: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case "product":
                        firestore.collection("Products").document(notificationRef).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        Intent chatSellerIntent = new Intent(context, ChatSellerActivity.class);
                                        chatSellerIntent.putExtra("productId", notificationRef);
                                        context.startActivity(chatSellerIntent);
                                        // update the background color
                                        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.ivory_black));
                                    } else {
                                        Toast.makeText(context, "No matching document found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Error getting document: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case "post":
                        firestore.collection("Posts").document(notificationRef).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        Intent viewPostIntent = new Intent(context, CommentsActivity.class);
                                        viewPostIntent.putExtra("postid", notificationRef);
                                        context.startActivity(viewPostIntent);
                                        // update the background color
                                        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.ivory_black));
                                    } else {
                                        Toast.makeText(context, "No matching document found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Error getting document: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    default:
                        // do something when the notificationType is neither "event" nor "product"
                        Toast.makeText(context, "Notification not match with type", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView mNotificationType, mNotificationSender, mNotificationTitle, mNotificationTimeStamp;
        ImageView mNotificationDelBtn;
        View mView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mNotificationDelBtn = mView.findViewById(R.id.notification_delete_btn);
        }

        public void setmNotificationType(String type) {
            mNotificationType = mView.findViewById(R.id.notification_type);
            mNotificationType.setText("New " + type + " notification");
        }

        public void setmNotificationSender(String sender) {
            mNotificationSender = mView.findViewById(R.id.notification_message_tv);
            mNotificationSender.setText("New message from " + sender);
        }

        public void setmNotificationTitle(String title) {
            mNotificationTitle = mView.findViewById(R.id.notification_title);
            mNotificationTitle.setText("From your post, " + title);
        }

        public void setmNotificationTimeStamp(Date timestamp) {
            mNotificationTimeStamp = mView.findViewById(R.id.notification_message_timestamp);
            if (timestamp != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd 'at' hh:mm a");
                String formattedDate = dateFormat.format(timestamp);
                mNotificationTimeStamp.setText(formattedDate);
            }
        }
    }
}
