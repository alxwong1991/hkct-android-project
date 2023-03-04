package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.auth.User;
import com.hkct.project.Adapter.LikeAdapter;
import com.hkct.project.Model.Comments;
import com.hkct.project.Model.Likes;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private RecyclerView mLikeRecyclerView;
    private String post_id;
    private List<Likes> mList;
    private List<Users> usersList;
    private LikeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        Log.d("hello", "bcd");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        mLikeRecyclerView = findViewById(R.id.like_recyclerView);

        mList = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new LikeAdapter(LikesActivity.this, mList, usersList);

        post_id = getIntent().getStringExtra("postid");
        mLikeRecyclerView.setHasFixedSize(true);
        mLikeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLikeRecyclerView.setAdapter(adapter);

        firestore.collection("Posts/" + post_id + "/Likes").addSnapshotListener(LikesActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                } else {
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Likes likes = documentChange.getDocument().toObject(Likes.class);
                            String userId = documentChange.getDocument().getString("user");

                            firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Users users = task.getResult().toObject(Users.class);
                                        usersList.add(users);
                                        mList.add(likes);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(LikesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
}