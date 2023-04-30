package com.hkct.project.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import com.hkct.project.AttendeesActivity;
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
        Log.d("eventlog123","size="+this.eventList.size());
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
        Log.d("eventlog","hihi");
        Event event = eventList.get(position);
        holder.setEventTitle(event.getTitle());
        holder.setEventDate(event.getDate());
        holder.setEventTime(event.getTime());
        holder.setEventPic(event.getImage());
        holder.setEventDescription(event.getDescription());
        holder.setEventAddress(event.getLocation());

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

        // leave event
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

        // view member attending event detail page
        holder.eventAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent attendeeDetailIntent = new Intent(context, AttendeesActivity.class);
                attendeeDetailIntent.putExtra("eventId", eventId);
                context.startActivity(attendeeDetailIntent);
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
                    eventDetailIntent.putExtra("eventId", eventId);
                } else {
                    eventDetailIntent = new Intent(context, OtherEventDetailActivity.class);
                    eventDetailIntent.putExtra("otherUserEventPostUid", otherUserEventPost);
                    eventDetailIntent.putExtra("eventId", eventId);
                }
                context.startActivity(eventDetailIntent);
            }
        });

        if (currentUserId.equals(event.getUser())) {
            // edit event
            holder.eventEditBtn.setVisibility(View.VISIBLE);
            holder.eventEditBtn.setClickable(true);
            holder.eventEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent editEventIntent = new Intent(context, EditEventActivity.class);
                    editEventIntent.putExtra("eventId", eventId);
                    editEventIntent.putExtra("image", event.getImage());
                    context.startActivity(editEventIntent);
                }
            });

            // delete event
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
        TextView eventTitle, eventDate, eventTime, eventAddress, eventDescription, eventAttendees;
        Button eventDetailBtn;
        View mView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            joinIcon = mView.findViewById(R.id.join_icon);
            leaveIcon = mView.findViewById(R.id.leave_icon);
            eventAttendees = mView.findViewById(R.id.member_count_tv);
            eventDetailBtn = mView.findViewById(R.id.event_view_btn);
            eventDeleteBtn = mView.findViewById(R.id.event_delete_btn);
            eventEditBtn = mView.findViewById(R.id.event_edit_btn);
        }

        public void setEventTitle(String title) {
            eventTitle = mView.findViewById(R.id.event_title);
            eventTitle.setText(title);
        }

        public void setEventPic(String image) {
            eventPic = mView.findViewById(R.id.event_image);
            if (image != null) {
                Glide.with(context).load(image).into(eventPic);
            }
        }

        public void setEventAddress(String address) {
            eventAddress = mView.findViewById(R.id.event_location);
            eventAddress.setText(address);
        }

        public void setEventDate(String date) {
            eventDate = mView.findViewById(R.id.event_date);
            eventDate.setText(date);
        }

        public void setEventTime(String time) {
            eventTime = mView.findViewById(R.id.event_time);
            eventTime.setText(time);
        }

        public void setEventAttendees(int count) {
            eventAttendees.setText(count + " Member Joined");
        }

        public void setEventDescription(String description) {
            eventDescription = mView.findViewById(R.id.event_detail_description);
            if (eventDescription != null) {
                eventDescription.setText(description);
            }
        }
    }
}
