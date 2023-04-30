package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.Adapter.NotificationAdapter;
import com.hkct.project.Model.Event;
import com.hkct.project.Model.Notification;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView mNotificationRecyclerView;
    private List<Notification> notificationList;
    private List<Users> usersList;
    private NotificationAdapter adapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mNotificationRecyclerView = findViewById(R.id.notification_recyclerView);

        usersList = new ArrayList<>();
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(NotificationActivity.this, usersList, notificationList);

        mNotificationRecyclerView.setHasFixedSize(true);
        mNotificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNotificationRecyclerView.setAdapter(adapter);

        firestore.collection("Notifications")
                .whereEqualTo("receiver", auth.getCurrentUser().getUid())
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(NotificationActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                } else {
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            String notificationId = documentChange.getDocument().getId();
                            Notification notification = documentChange.getDocument().toObject(Notification.class).withId(notificationId);
                            String senderId = documentChange.getDocument().getString("sender");

                            if (senderId.equals(auth.getCurrentUser().getUid())) {
                                continue; // don't query if receiver and sender are the same
                            }

                                firestore.collection("Users").document(senderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Users users = task.getResult().toObject(Users.class);
                                            usersList.add(users);
                                            notificationList.add(notification);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(NotificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(NotificationActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}