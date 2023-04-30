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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.hkct.project.Adapter.ChatAdapter;
import com.hkct.project.Model.Chat;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatSellerActivity extends AppCompatActivity {

    private EditText messageEdit;
    private Button mSendMessageBtn;
    private RecyclerView mChatRecyclerView;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ChatAdapter adapter;
    private List<Chat> chatList;
    private List<Users> usersList;
    private String currentUserId;
    private String product_id;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_seller);

        messageEdit = findViewById(R.id.chat_edittext);
        mSendMessageBtn = findViewById(R.id.send_chat_message);
        mChatRecyclerView = findViewById(R.id.chat_seller_recyclerView);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        chatList = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new ChatAdapter(ChatSellerActivity.this, chatList, usersList);

        product_id = getIntent().getStringExtra("productId");
        productName = getIntent().getStringExtra("productName");

        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setAdapter(adapter);

        firestore.collection("Products/" + product_id + "/Messages").addSnapshotListener(ChatSellerActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Chat chat = documentChange.getDocument().toObject(Chat.class);
                        String userId = documentChange.getDocument().getString("user");

                        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Users users = task.getResult().toObject(Users.class);
                                    usersList.add(users);
                                    chatList.add(chat);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(ChatSellerActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                    firestore.collection("Products").document(product_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    receiver[0] = documentSnapshot.getString("user");
                                    String sender = currentUserId;
                                    String title = productName;
                                    String type = "product";

                                    Map<String, Object> notificaitonsMap = new HashMap<>();
                                    notificaitonsMap.put("receiver", receiver[0]);
                                    notificaitonsMap.put("sender", sender);
                                    notificaitonsMap.put("time", FieldValue.serverTimestamp());
                                    notificaitonsMap.put("title", title);
                                    notificaitonsMap.put("reference", product_id);
                                    notificaitonsMap.put("type", type);
                                    firestore.collection("Notifications").add(notificaitonsMap);
                                }
                            }
                        }
                    });

                    firestore.collection("Products/" + product_id + "/Messages").add(messagesMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChatSellerActivity.this, "Message Sent!!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChatSellerActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChatSellerActivity.this, "Please write a Message", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(ChatSellerActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}