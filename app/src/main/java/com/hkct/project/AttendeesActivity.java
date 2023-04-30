package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.Adapter.AttendeesAdapter;
import com.hkct.project.Model.Attendees;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.List;

public class AttendeesActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView mAttendeeRecyclerView;
    private String event_id;
    private List<Attendees> attendeesList;
    private List<Users> usersList;
    private AttendeesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        firestore = FirebaseFirestore.getInstance();
        mAttendeeRecyclerView = findViewById(R.id.attendee_recyclerView);

        attendeesList = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new AttendeesAdapter(AttendeesActivity.this, usersList, attendeesList);

        event_id = getIntent().getStringExtra("eventId");
        mAttendeeRecyclerView.setHasFixedSize(true);
        mAttendeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAttendeeRecyclerView.setAdapter(adapter);

        firestore.collection("Events/" + event_id + "/Attendees").addSnapshotListener(AttendeesActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                } else {
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Attendees attendees = documentChange.getDocument().toObject(Attendees.class);
                            String userId = documentChange.getDocument().getString("user");

                            firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Users users = task.getResult().toObject(Users.class);
                                        usersList.add(users);
                                        attendeesList.add(attendees);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(AttendeesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
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
        Toast.makeText(AttendeesActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}