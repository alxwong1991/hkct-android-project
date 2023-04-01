package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hkct.project.Model.Event;
import com.hkct.project.Model.Users;

public class EventDetailActivity extends AppCompatActivity {

    private TextView mEventTitle, mEventLocation, mEventDescription, mEventHostName;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Uri mImageUri;
    private String event_id, Uid;
    private ImageView mEventHostProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();

        event_id = getIntent().getStringExtra("eventid");
        mEventHostName = findViewById(R.id.event_host_name);
        mEventHostProfilePic = findViewById(R.id.host_profile_pic_event);
        mEventTitle = findViewById(R.id.event_detail_title);
        mEventDescription = findViewById(R.id.event_detail_description);
        mEventLocation = findViewById(R.id.event_detail_location);

//        firestore.collection("Events").document(event_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    if (task.getResult().exists()) {
//
//                        mEventTitle.setText(task.getResult().getString("title"));
//                        mEventDescription.setText(task.getResult().getString("description"));
//                        mEventLocation.setText(task.getResult().getString("location"));
//
//                        String userId = task.getResult().getString("user");
//                        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    if (task.getResult().exists()) {
//
//                                        mEventHostName.setText(task.getResult().getString("name"));
//                                        mImageUri = Uri.parse(task.getResult().getString("image"));
//
//                                        // Load user image using Glide library
//                                        Glide.with(EventDetailActivity.this).load(mImageUri).into(mEventHostProfilePic);
//                                    }
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
    }
}