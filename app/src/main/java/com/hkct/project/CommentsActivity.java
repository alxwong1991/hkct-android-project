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
import com.hkct.project.Adapter.CommentsAdapter;
import com.hkct.project.Model.Comments;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private EditText commentEdit;
    private Button mAddCommentBtn;
    private RecyclerView mCommentRecyclerView;
    private FirebaseFirestore firestore;
    private String post_id;
    private String postCaption;
    private String currentUserId;
    private FirebaseAuth auth;
    private CommentsAdapter adapter;
    private List<Comments> mList;
    private List<Users> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentEdit = findViewById(R.id.comment_edittext);
        mAddCommentBtn = findViewById(R.id.add_comment);
        mCommentRecyclerView = findViewById(R.id.comment_recyclerView);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        mList = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new CommentsAdapter(CommentsActivity.this, mList, usersList);

        post_id = getIntent().getStringExtra("postid");


        mCommentRecyclerView.setHasFixedSize(true);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommentRecyclerView.setAdapter(adapter);

        firestore.collection("Posts/" + post_id + "/Comments").addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Comments comments = documentChange.getDocument().toObject(Comments.class);
                        String userId = documentChange.getDocument().getString("user");

                        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Users users = task.getResult().toObject(Users.class);
                                    usersList.add(users);
                                    mList.add(comments);
                                    adapter.notifyDataSetChanged();

                                    if (!userId.equals(currentUserId)) {
                                        firestore.collection("Posts").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    postCaption = task.getResult().getString("caption");
                                                } else {
                                                    Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mAddCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentEdit.getText().toString();
                if (!comment.isEmpty()) {
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("comment", comment);
                    commentsMap.put("time", FieldValue.serverTimestamp());
                    commentsMap.put("user", currentUserId);

                    final String[] receiver = {null};
                    firestore.collection("Posts").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    receiver[0] = documentSnapshot.getString("user");
                                    String sender = currentUserId;
                                    String title = postCaption;
                                    String type = "post";

                                    Map<String, Object> notificaitonsMap = new HashMap<>();
                                    notificaitonsMap.put("receiver", receiver[0]);
                                    notificaitonsMap.put("sender", sender);
                                    notificaitonsMap.put("time", FieldValue.serverTimestamp());
                                    notificaitonsMap.put("title", title);
                                    notificaitonsMap.put("reference", post_id);
                                    notificaitonsMap.put("type", type);
                                    firestore.collection("Notifications").add(notificaitonsMap);
                                }
                            }
                        }
                    });

                    firestore.collection("Posts/" + post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CommentsActivity.this, "Comment Added!!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(CommentsActivity.this, "Please write Comment", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(CommentsActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}