package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private Button mAddEventBtn;
    private TextView mEventDateTextView, mEventTimeTextView;
    private EditText mEventTitle, mEventLocation, mEventDescription;
    private ImageView mEventImage;
    private ProgressBar mProgressBar;
    private Uri eventImageUri = null;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentUserId;

    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedTime = Calendar.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mAddEventBtn = findViewById(R.id.save_event_btn);
        mEventTitle = findViewById(R.id.event_title);
        mEventImage = findViewById(R.id.event_image);
        mEventLocation = findViewById(R.id.event_location);
        mEventDescription = findViewById(R.id.event_description);
        mEventDateTextView = findViewById(R.id.event_date);
        mEventTimeTextView = findViewById(R.id.event_time);

        ImageView datePicker = findViewById(R.id.event_date_picker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = selectedDate.get(Calendar.YEAR);
                int month = selectedDate.get(Calendar.MONTH);
                int day = selectedDate.get(Calendar.DAY_OF_MONTH);

                // Create a date picker dialog and set the selected date as the default
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Set the selected date to the chosen date
                                selectedDate.set(year, month, dayOfMonth);

                                // Update the date TextView
                                mEventDateTextView.setText(formatDate(selectedDate.getTime()));
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        ImageView timePicker = findViewById(R.id.event_time_picker);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
                int minute = selectedTime.get(Calendar.MINUTE);

                // Create a time picker dialog and set the selected time as the default
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set the selected time to the chosen time
                                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedTime.set(Calendar.MINUTE, minute);

                                // Update the time TextView
                                mEventTimeTextView.setText(formatTime(selectedTime.getTime()));
                            }
                        },
                        hour, minute, false);
                timePickerDialog.show();
            }
        });

        mProgressBar = findViewById(R.id.event_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        mEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("addeventlog", "testing");
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3,2)
                        .setMinCropResultSize(512, 512)
                        .start(AddEventActivity.this);
            }
        });

        mAddEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                String title = mEventTitle.getText().toString();
                String description = mEventDescription.getText().toString();
                String location = mEventLocation.getText().toString();
                String date = mEventDateTextView.getText().toString();
                String time = mEventTimeTextView.getText().toString();

                if (!description.isEmpty() && eventImageUri != null) {
                    StorageReference eventRef = storageReference
                            .child("event_images")
                            .child(FieldValue.serverTimestamp().toString() + ".jpg");

                    eventRef.putFile(eventImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                eventRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String, Object> postEventMap = new HashMap<>();
                                        postEventMap.put("image", uri.toString());
                                        postEventMap.put("user", currentUserId);
                                        postEventMap.put("description", description);
                                        postEventMap.put("location", location);
                                        postEventMap.put("title", title);
                                        postEventMap.put("date", date);
                                        postEventMap.put("time", time);
                                        postEventMap.put("timestamp", FieldValue.serverTimestamp());

                                        firestore.collection("Events").add(postEventMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
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
                    Toast.makeText(AddEventActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return timeFormat.format(date);
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
        Toast.makeText(AddEventActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}