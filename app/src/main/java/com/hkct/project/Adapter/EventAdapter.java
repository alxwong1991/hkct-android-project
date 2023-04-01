package com.hkct.project.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.hkct.project.EditEventActivity;
import com.hkct.project.EventDetailActivity;
import com.hkct.project.Model.Event;
import com.hkct.project.Model.Users;
import com.hkct.project.OtherEventDetailActivity;
import com.hkct.project.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Activity context;
    private List<Event> eventList;
    private List<Users> usersList;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String Uid;

    public EventAdapter(Activity context, List<Event> eventList, List<Users> usersList) {
        this.eventList = eventList;
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
        Event event = eventList.get(position);
//        String eventUsername = usersList.get(position).getName();
//        String image = usersList.get(position).getImage();

//        holder.setProfilePic(image);
//        holder.setEventUsername(eventUsername);

        holder.setEventTitle(event.getEventTitle());
        holder.setEventDate(event.getEventDate());
        holder.setEventTime(event.getEventTime());
        holder.setEventPic(event.getEventImage());
        holder.setEventDescription(event.getEventDescription());
        holder.setEventAddress(event.getEventAddress());

        // join event
        String eventId = event.EventId;
        String currentUserId = auth.getCurrentUser().getUid();

        holder.joinIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Events/" + eventId + "/Attendees").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> joinEventMap = new HashMap<>();
                            joinEventMap.put("timestamp", FieldValue.serverTimestamp());
                            joinEventMap.put("user", currentUserId);
                            firestore.collection("Events/" + eventId + "/Attendees").document(currentUserId).set(joinEventMap);
                        } else {
                            holder.leaveIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    firestore.collection("Events/" + eventId + "/Attendees").document(currentUserId).delete();
                                }
                            });
                            holder.joinIcon.setEnabled(false);
                        }
                    }
                });
            }
        });

        holder.leaveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Events/" + eventId + "/Attendees").document(currentUserId).delete();
            }
        });

        // join btn color change
        firestore.collection("Events/" + eventId + "/Attendees").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (value.exists()) {
                        holder.joinIcon.setImageDrawable(context.getDrawable(R.drawable.after_join));
                        holder.leaveIcon.setImageDrawable(context.getDrawable(R.drawable.before_leave));
                        holder.joinIcon.setEnabled(false);
                    } else {
                        holder.leaveIcon.setImageDrawable(context.getDrawable(R.drawable.after_leave));
                        holder.joinIcon.setImageDrawable(context.getDrawable(R.drawable.before_join));
                        holder.joinIcon.setEnabled(true);
                    }
                }
            }
        });

        // user attending count
        firestore.collection("Events/" + eventId + "/Attendees").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    int count = value != null ? value.size() : 0;
                    holder.setEventAttendees(count);
                }
            }
        });

        // event details btn
        holder.eventDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otherUserEventPost = eventList.get(position).getUser();
                String currentUserEventPost = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Intent eventDetailIntent;
                if (otherUserEventPost.equals(currentUserEventPost)) {
                    eventDetailIntent = new Intent(context, EventDetailActivity.class);
                } else {
                    eventDetailIntent = new Intent(context, OtherEventDetailActivity.class);
                    eventDetailIntent.putExtra("otherUserEventPostUid", otherUserEventPost);
                }
                context.startActivity(eventDetailIntent);
            }
        });

        if (currentUserId.equals(event.getUser())) {
            holder.eventEditBtn.setVisibility(View.VISIBLE);
            holder.eventEditBtn.setClickable(true);
            holder.eventEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent editEventIntent = new Intent(context, EditEventActivity.class);
                    editEventIntent.putExtra("eventId", eventId);
                    context.startActivity(editEventIntent);
                }
            });

            holder.eventDeleteBtn.setVisibility(View.VISIBLE);
            holder.eventDeleteBtn.setClickable(true);
            holder.eventDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Delete")
                            .setMessage("Are you sure?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    firestore.collection("Events").document(eventId).delete();
                                    eventList.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                    alert.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventPic, eventDeleteBtn, eventEditBtn, joinIcon, leaveIcon;
        CircleImageView hostProfilePic;
        TextView eventTitle, eventDate, eventTime, eventAddress, eventDescription, eventAttendees, eventUsername;
        Button eventDetailBtn;
        View mView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            eventTitle = mView.findViewById(R.id.event_title);
            eventAddress = mView.findViewById(R.id.event_location);
            eventDate = mView.findViewById(R.id.event_date);
            eventTime = mView.findViewById(R.id.event_time);
            eventPic = mView.findViewById(R.id.user_event);
            joinIcon = mView.findViewById(R.id.join_icon);
            leaveIcon = mView.findViewById(R.id.leave_icon);
            eventAttendees = mView.findViewById(R.id.member_count_tv);
            eventDetailBtn = mView.findViewById(R.id.event_view_btn);
            eventDescription = mView.findViewById(R.id.event_detail_description);
            eventDeleteBtn = mView.findViewById(R.id.event_delete_btn);
            eventEditBtn = mView.findViewById(R.id.event_edit_btn);
        }

//        public void setProfilePic(String eventHostPic) {
//            hostProfilePic = mView.findViewById(R.id.host_profile_pic_event);
//            Glide.with(context).load(eventHostPic).into(hostProfilePic);
//        }

        public void setEventTitle(String title) {
            eventTitle.setText(title);
        }

//        public void setEventUsername(String username) {
//            eventUsername = mView.findViewById(R.id.event_host_name);
//            eventUsername.setText(username);
//        }

        public void setEventPic(String image) {
            if (image != null) {
                Glide.with(context).load(image).into(eventPic);
            } else {
                Glide.with(context).load(R.drawable.rectangle).into(eventPic);
            }
        }

        public void setEventAddress(String address) {
            eventAddress.setText(address);
        }

        public void setEventDate(String date) {
            eventDate.setText(date);
        }

        public void setEventTime(String time) {
            eventTime.setText(time);
        }

        public void setEventAttendees(int count) {
            eventAttendees.setText(count + " Members Joined");
        }

        public void setEventDescription(String description) {
            if (eventDescription != null) {
                eventDescription.setText(description);
            }
        }
    }
}
