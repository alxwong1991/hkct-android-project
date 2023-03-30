package com.hkct.project.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.EventDetailActivity;
import com.hkct.project.Model.Event;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Activity context;
    private List<Event> mList;
    private List<Users> usersList;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String Uid;

    public EventAdapter(Activity context, List<Event> mList, List<Users> usersList) {
        this.mList = mList;
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_event, parent, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        Event event = mList.get(position);
        String eventUsername = usersList.get(position).getName();
        String image = usersList.get(position).getImage();

        holder.setProfilePic(image);
        holder.setEventUsername(eventUsername);

        holder.setEventTitle(event.getEventTitle());
        holder.setEventPic(event.getEventImage());
        holder.setEventDescription(event.getEventDescription());

        // join event
        String eventId = event.EventId;
        String currentUserId = auth.getCurrentUser().getUid();
        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Events/" + eventId + "/Attendees").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> joinEventMap = new HashMap<>();
                            joinEventMap.put("timestamp", FieldValue.serverTimestamp());
                            joinEventMap.put("user", currentUserId);
                            firestore.collection("Events/" + eventId + "/Attendees").document(currentUserId).set(joinEventMap);
                        } else {
                            firestore.collection("Events/" + eventId + "/Attendees").document(currentUserId).delete();
                        }
                    }
                });
            }
        });

        // join btn change
        firestore.collection("Events/" + eventId + "/Attendees").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    holder.joinBtn.setVisibility(View.INVISIBLE);
                    holder.leaveBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        // user attending count
        firestore.collection("Events/" + eventId + "/Attendees").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (!value.isEmpty()) {
                        int count = value.size();
                        holder.setEventAttendees(count);
                    } else {
                        holder.setEventAttendees(0);
                    }
                }
            }
        });

        // event details btn
        holder.eventDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventDetailIntent = new Intent(context, EventDetailActivity.class);
                eventDetailIntent.putExtra("eventId", event.EventId);
                context.startActivity(eventDetailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventPic;
        CircleImageView profilePic;
        TextView eventTitle, eventDate, eventDescription, eventAttendees, eventUsername;
        Button joinBtn, leaveBtn, eventDetailBtn;
        View mView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            joinBtn = mView.findViewById(R.id.event_join_btn);
            leaveBtn = mView.findViewById(R.id.event_leave_btn);
            eventAttendees = mView.findViewById(R.id.member_count_tv);
            eventDetailBtn = mView.findViewById(R.id.event_view_btn);
        }

        public void setProfilePic(String eventHostPic) {
            profilePic = mView.findViewById(R.id.profile_pic_event);
            Glide.with(context).load(eventHostPic).into(profilePic);
        }

        public void setEventTitle(String title) {
            eventTitle = mView.findViewById(R.id.event_title);
            eventTitle.setText(title);
        }

        public void setEventUsername(String username) {
            eventUsername = mView.findViewById(R.id.event_host_name);
            eventUsername.setText(username);
        }

        public void setEventPic(String eventPost) {
            eventPic = mView.findViewById(R.id.event_image);
            Glide.with(context).load(eventPost).into(eventPic);
        }

        public void setEventAttendees(int count) {
            eventAttendees.setText(count + " Joined");
        }

        public void setEventDescription(String description) {
            eventDescription = mView.findViewById(R.id.event_description);
            eventDescription.setText(description);
        }
    }
}
