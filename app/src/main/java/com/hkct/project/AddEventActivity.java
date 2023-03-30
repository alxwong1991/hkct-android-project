package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {

    private Button mAddPostBtn;
    private EditText mCaptionText, mLocation, mEventTitle;
    private ImageView mEventImage;
    private ProgressBar mProgressBar;
    private Uri eventImageUri = null;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentUserId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mAddPostBtn = findViewById(R.id.save_event_btn);
        mCaptionText = findViewById(R.id.event_title);
        mEventImage = findViewById(R.id.event_image);
        mLocation = findViewById(R.id.event_location);
        mEventTitle = findViewById(R.id.event_title);

        mProgressBar = findViewById(R.id.event_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        mEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3,2)
                        .setMinCropResultSize(512, 512)
                        .start(AddEventActivity.this);
            }
        });
        mAddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                String title = mEventTitle.getText().toString();
                String description = mCaptionText.getText().toString();
                String location = mLocation.getText().toString();
                if (!description.isEmpty() && eventImageUri != null) {
                    StorageReference eventRef = storageReference.child("event_images").child(FieldValue.serverTimestamp().toString() + ".jpg");
                    eventRef.putFile(eventImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                eventRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String, Object> postMap = new HashMap<>();
                                        postMap.put("image", uri.toString());
                                        postMap.put("user", currentUserId);
                                        postMap.put("description", description);
                                        postMap.put("location", location);
                                        postMap.put("title", title);
                                        postMap.put("time", FieldValue.serverTimestamp());

                                        firestore.collection("Events").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AddEventActivity.this, "Event Added Successfully!!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AddEventActivity.this, EventActivity.class));
                                                } else {
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AddEventActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AddEventActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddEventActivity.this, "Please Add Image and Write Your Caption", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                eventImageUri = result.getUri();
                mEventImage.setImageURI(eventImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, result.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}