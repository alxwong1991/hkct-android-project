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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.MessageHostActivity;
import com.hkct.project.Model.Event;
import com.hkct.project.Model.Notification;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Activity context;
    private List<Users> usersList;
    private List<Notification> notificationList;

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

        holder.mNotificationTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String notificationType = notification.getType();
                String notificationTitle = notification.getTitle();

                FirebaseFirestore.getInstance()
                        .collection("Notification")
                        .whereEqualTo("type", notificationType)
                        .whereEqualTo("title", notificationTitle)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("notification adapter", "testing");
                                    Intent messageHostIntent = new Intent(context, MessageHostActivity.class);
                                    messageHostIntent.putExtra("notificationEventTitle", notificationTitle);
                                    context.startActivity(messageHostIntent);
                                } else {
                                    Toast.makeText(context, "No matching document found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

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
            mNotificationTitle.setText(title);
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
