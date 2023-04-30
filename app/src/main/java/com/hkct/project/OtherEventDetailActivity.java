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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;

public class OtherEventDetailActivity extends AppCompatActivity {

    private TextView mOtherEventDetailTitle, mOtherEventDetailLocation, mOtherEventDetailDescription, mOtherEventDetailHostName;
    private FirebaseFirestore firestore;
    private String otherUserEventPostUid, eventId;
    private ImageView mOtherEventDetailHostProfilePic, mOtherEventDetailImage;
    private Button mMessageHostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_event_detail);

        firestore = FirebaseFirestore.getInstance();

        otherUserEventPostUid = getIntent().getStringExtra("otherUserEventPostUid");
        eventId = getIntent().getStringExtra("eventId");

        mOtherEventDetailHostName = findViewById(R.id.event_host_name);
        mOtherEventDetailImage = findViewById(R.id.event_detail_image);
        mOtherEventDetailHostProfilePic = findViewById(R.id.event_host_profile_pic);
        mOtherEventDetailTitle = findViewById(R.id.event_detail_title);
        mOtherEventDetailDescription = findViewById(R.id.event_detail_description);
        mOtherEventDetailLocation = findViewById(R.id.event_detail_location);
        mMessageHostBtn = findViewById(R.id.message_host_btn);

        mMessageHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageHostIntent = new Intent(OtherEventDetailActivity.this, MessageHostActivity.class);
                messageHostIntent.putExtra("eventId", eventId);
                startActivity(messageHostIntent);
            };
        });

        firestore.collection("Events")
                .whereEqualTo("user", otherUserEventPostUid)
                .whereEqualTo(FieldPath.documentId(), eventId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Get the first document in the result set
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    mOtherEventDetailTitle.setText(documentSnapshot.getString("title"));
                    mOtherEventDetailLocation.setText(documentSnapshot.getString("location"));
                    mOtherEventDetailDescription.setText(documentSnapshot.getString("description"));
                    String eventDetailImageUrl = documentSnapshot.getString("image");
                    Glide.with(OtherEventDetailActivity.this).load(eventDetailImageUrl).into(mOtherEventDetailImage);

                    firestore.collection("Users").document(otherUserEventPostUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String eventHostName = documentSnapshot.getString("name");
                                mOtherEventDetailHostName.setText(eventHostName);
                                String eventHostProfilePic = documentSnapshot.getString("image");
                                Glide.with(OtherEventDetailActivity.this).load(eventHostProfilePic).into(mOtherEventDetailHostProfilePic);
                            }
                        }
                    });
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
        Toast.makeText(OtherEventDetailActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}