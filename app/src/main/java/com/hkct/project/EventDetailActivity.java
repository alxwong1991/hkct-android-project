package com.hkct.project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {

    private TextView mEventDetailTitle, mEventDetailLocation, mEventDetailDescription, mEventDetailHostName;
    private FirebaseFirestore firestore;
    private String event_id;
    private ImageView mEventDetailHostProfilePic, mEventDetailImage;
    private Button mMessageHostBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        firestore = FirebaseFirestore.getInstance();

        event_id = getIntent().getStringExtra("eventId");

        mEventDetailHostName = findViewById(R.id.event_host_name);
        mEventDetailImage = findViewById(R.id.event_detail_image);
        mEventDetailHostProfilePic = findViewById(R.id.event_host_profile_pic);
        mEventDetailTitle = findViewById(R.id.event_detail_title);
        mEventDetailDescription = findViewById(R.id.event_detail_description);
        mEventDetailLocation = findViewById(R.id.event_detail_location);
        mMessageHostBtn = findViewById(R.id.message_host_btn);

        mMessageHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageHostIntent = new Intent(EventDetailActivity.this, MessageHostActivity.class);
                messageHostIntent.putExtra("eventId", event_id);
                messageHostIntent.putExtra("eventTitle", mEventDetailTitle.getText().toString());
                startActivity(messageHostIntent);
            };
        });

        firestore.collection("Events").document(event_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String eventHostUid = documentSnapshot.getString("user");
                    firestore.collection("Users").document(eventHostUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String eventHostName = documentSnapshot.getString("name");
                                mEventDetailHostName.setText(eventHostName);
                                String eventHostProfilePic = documentSnapshot.getString("image");
                                Glide.with(EventDetailActivity.this).load(eventHostProfilePic).into(mEventDetailHostProfilePic);
                            }
                        }
                    });
                    String eventDetailImageUrl = documentSnapshot.getString("image");
                    Glide.with(EventDetailActivity.this).load(eventDetailImageUrl).into(mEventDetailImage);
                    mEventDetailTitle.setText(documentSnapshot.getString("title"));
                    mEventDetailLocation.setText(documentSnapshot.getString("location"));
                    mEventDetailDescription.setText(documentSnapshot.getString("description"));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void menu_logout_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Toast.makeText(EventDetailActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}