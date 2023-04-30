package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {

    private String TAG = "EditEventActivity===>";
    private Button mEditEventBtn;
    private TextView mEditEventDateTextView, mEditEventTimeTextView;
    private EditText mEditEventDescription, mEditEventLocation, mEditEventTitle;
    private ImageView mEditEventImage;
    private ProgressBar mProgressBar;
    private FirebaseFirestore firestore;
    private String mEditEventImageUrl;

    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        String eventId = getIntent().getStringExtra("eventId");
        mEditEventImageUrl = getIntent().getStringExtra("image");
        mEditEventImage = findViewById(R.id.edit_event_image);

        mEditEventBtn = findViewById(R.id.edit_event_btn);

        mEditEventTitle = findViewById(R.id.edit_event_title);
        mEditEventDescription = findViewById(R.id.edit_event_description);
        mEditEventLocation = findViewById(R.id.edit_event_location);
        mEditEventDateTextView = findViewById(R.id.edit_event_date);
        mEditEventTimeTextView = findViewById(R.id.edit_event_time);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Events").document(eventId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    mEditEventImageUrl = documentSnapshot.getString("image");
                    mEditEventTitle.setText(documentSnapshot.getString("title"));
                    mEditEventDescription.setText(documentSnapshot.getString("description"));
                    mEditEventLocation.setText(documentSnapshot.getString("location"));
                    mEditEventDateTextView.setText(documentSnapshot.getString("date"));
                    mEditEventTimeTextView.setText(documentSnapshot.getString("time"));

                    Glide.with(EditEventActivity.this).load(mEditEventImageUrl).into(mEditEventImage);
                }
            }
        });


        ImageView datePicker = findViewById(R.id.edit_event_date_picker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = selectedDate.get(Calendar.YEAR);
                int month = selectedDate.get(Calendar.MONTH);
                int day = selectedDate.get(Calendar.DAY_OF_MONTH);

                // Create a date picker dialog and set the selected date as the default
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Set the selected date to the chosen date
                                selectedDate.set(year, month, dayOfMonth);

                                // Update the date TextView
                                mEditEventDateTextView.setText(formatDate(selectedDate.getTime()));
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        ImageView timePicker = findViewById(R.id.edit_event_time_picker);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
                int minute = selectedTime.get(Calendar.MINUTE);

                // Create a time picker dialog and set the selected time as the default
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        EditEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set the selected time to the chosen time
                                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedTime.set(Calendar.MINUTE, minute);

                                // Update the time TextView
                                mEditEventTimeTextView.setText(formatTime(selectedTime.getTime()));
                            }
                        },
                        hour, minute, false);
                timePickerDialog.show();
            }
        });

        mProgressBar = findViewById(R.id.edit_event_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mEditEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                String title = mEditEventTitle.getText().toString();
                String description = mEditEventDescription.getText().toString();
                String location = mEditEventLocation.getText().toString();
                String date = mEditEventDateTextView.getText().toString();
                String time = mEditEventTimeTextView.getText().toString();

                DocumentReference updateEvent = firestore.collection("Events").document(eventId);
                updateEvent.update(
                        "title", title,
                        "description", description,
                        "location", location,
                        "date", date,
                        "time", time,
                        "timestamp", FieldValue.serverTimestamp()
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditEventActivity.this, "Event Updated Successfully!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditEventActivity.this, EventActivity.class));
                        } else {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditEventActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Log.d(TAG,"===>editEventActivity!!!");
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
        Toast.makeText(EditEventActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}