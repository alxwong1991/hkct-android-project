package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditEventActivity extends AppCompatActivity {

    private String TAG = "EditEventActivity===>";
    private Button mEditEventBtn;
    private TextView mEditEventDateTextView, mEditEventTimeTextView;
    private EditText mEditEventDescription, mEditEventLocation, mEditEventTitle;
    private ImageView mEventImage;
    private ProgressBar mProgressBar;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private String Uid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        mEditEventBtn = findViewById(R.id.edit_event_btn);

        mEventImage = findViewById(R.id.edit_event_image);
        mEditEventTitle = findViewById(R.id.edit_event_title);
        mEditEventDescription = findViewById(R.id.edit_event_description);
        mEditEventLocation = findViewById(R.id.edit_event_location);
        mEditEventDateTextView = findViewById(R.id.edit_event_date);
        mEditEventTimeTextView = findViewById(R.id.edit_event_time);

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();

        mProgressBar = findViewById(R.id.event_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        firestore.collection("Events").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String eventImageUrl = task.getResult().getString("image");
                        String eventTitle = task.getResult().getString("title");
                        String eventLocation = task.getResult().getString("location");
                        String eventDescription = task.getResult().getString("description");
                        String eventDate = task.getResult().getString("date");
                        String eventTime = task.getResult().getString("time");

                        mEditEventTitle.setText(eventTitle);
                        mEditEventLocation.setText(eventLocation);
                        mEditEventDescription.setText(eventDescription);
                        mEditEventTimeTextView.setText(eventDate);
                        mEditEventDateTextView.setText(eventTime);

                        Glide.with(EditEventActivity.this).load(eventImageUrl).into(mEventImage);
                    }
                }
            }
        });

        mEditEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                String title = mEditEventTitle.getText().toString();
                String description = mEditEventDescription.getText().toString();
                String location = mEditEventLocation.getText().toString();
                String date = mEditEventDateTextView.getText().toString();
                String time = mEditEventTimeTextView.getText().toString();

            }
        });

        Log.d(TAG,"===>editEventActivity!!!");
    }
}