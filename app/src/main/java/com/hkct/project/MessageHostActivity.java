package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.Adapter.MessagesAdapter;
import com.hkct.project.Model.Messages;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHostActivity extends AppCompatActivity {

    private EditText messageEdit;
    private Button mSendMessageBtn;
    private RecyclerView mMessageRecyclerView;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private MessagesAdapter adapter;
    private List<Messages> messagesList;
    private List<Users> usersList;
    private String currentUserId;
    private String event_id;
    private String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_host);

        messageEdit = findViewById(R.id.message_edittext);
        mSendMessageBtn = findViewById(R.id.send_message);
        mMessageRecyclerView = findViewById(R.id.message_host_recyclerView);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        messagesList = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new MessagesAdapter(MessageHostActivity.this, messagesList, usersList);

        event_id = getIntent().getStringExtra("eventId");

        mMessageRecyclerView.setHasFixedSize(true);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecyclerView.setAdapter(adapter);

        firestore.collection("Events/" + event_id + "/Messages").addSnapshotListener(MessageHostActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Messages messages = documentChange.getDocument().toObject(Messages.class);
                        String userId = documentChange.getDocument().getString("user");

                        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Users users = task.getResult().toObject(Users.class);
                                    usersList.add(users);
                                    messagesList.add(messages);
                                    adapter.notifyDataSetChanged();

                                    if (!userId.equals(currentUserId)) {
                                        firestore.collection("Events").document(event_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    eventTitle = task.getResult().getString("title");
                                                } else {
                                                    Toast.makeText(MessageHostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

//                                        firestore.collection("Events/" + event_id + "/Messages").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    eventMessage = task.getResult().getString("message");
//                                                } else {
//                                                    Toast.makeText(MessageHostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
                                    }
                                } else {
                                    Toast.makeText(MessageHostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mSendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEdit.getText().toString();
                if (!message.isEmpty()) {
                    Map<String, Object> messagesMap = new HashMap<>();
                    messagesMap.put("message", message);
                    messagesMap.put("timestamp", FieldValue.serverTimestamp());
                    messagesMap.put("user", currentUserId);

                    final String[] receiver = {null};
                    firestore.collection("Events").document(event_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    receiver[0] = documentSnapshot.getString("user");
                                    String sender = currentUserId;
                                    String title = eventTitle;
                                    String type = "event";

                                    Map<String, Object> notificaitonsMap = new HashMap<>();
                                    notificaitonsMap.put("receiver", receiver[0]);
                                    notificaitonsMap.put("sender", sender);
                                    notificaitonsMap.put("time", FieldValue.serverTimestamp());
                                    notificaitonsMap.put("title", title);
                                    notificaitonsMap.put("reference", event_id);
                                    notificaitonsMap.put("type", type);
                                    firestore.collection("Notifications").add(notificaitonsMap);
                                }
                            }
                        }
                    });

                    firestore.collection("Events/" + event_id + "/Messages").add(messagesMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MessageHostActivity.this, "Message Sent!!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MessageHostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MessageHostActivity.this, "Please write a Message", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(MessageHostActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}