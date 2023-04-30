package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.Adapter.PostAdapter;
import com.hkct.project.Model.Post;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileActivity extends AppCompatActivity {

    private final String TAG="OtherProfileActivity===>";
    private TextView mOtherProfileName;
    private CircleImageView mOtherProfileImage;
    private ImageView mOtherMembershipIcon;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
//    private Button mFollowBtn;

    private RecyclerView mRecyclerView;
    private List<Post> posts;
    private List<Users> user;
    private PostAdapter adapter;
    private Query query;
    private Uri mImageUri;

    private ListenerRegistration listenerRegistration;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("alex", "testing");
        setContentView(R.layout.activity_other_profile);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String otherUserUid = getIntent().getStringExtra("otherUserUid");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        mOtherProfileImage = findViewById(R.id.other_profile_pic_personal);
        mOtherProfileName = findViewById(R.id.other_profile_name_personal);
        mOtherMembershipIcon = findViewById(R.id.other_membership_icon);
//        mFollowBtn = findViewById(R.id.follow_btn);

        mRecyclerView = findViewById(R.id.recyclerViewPosts);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(OtherProfileActivity.this));

        posts = new ArrayList<>();
        user = new ArrayList<>();
        adapter = new PostAdapter(OtherProfileActivity.this, posts, user);
        mRecyclerView.setAdapter(adapter);

        if (auth.getCurrentUser() != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isBottom = !mRecyclerView.canScrollVertically(1);
                    if (isBottom)
                        Toast.makeText(OtherProfileActivity.this, "Reached Bottom", Toast.LENGTH_SHORT).show();
                }
            });

            query = firestore.collection("Posts").whereEqualTo("user", otherUserUid).orderBy("time", Query.Direction.DESCENDING);

            listenerRegistration = query.addSnapshotListener(OtherProfileActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(OtherProfileActivity.this, "No Posts", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DocumentChange doc : value.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String postID = doc.getDocument().getId();
                                Post post = doc.getDocument().toObject(Post.class).withId(postID);
                                String postUserID = doc.getDocument().getString("user");
                                firestore.collection("Users").document(postUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Users users = task.getResult().toObject(Users.class);
                                            user.add(users);
                                            posts.add(post);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(OtherProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                    listenerRegistration.remove();
                }
            });

            firestore.collection("Users").document(otherUserUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String name = task.getResult().getString("name");
                            String imageUrl = task.getResult().getString("image");
                            String memberShip = task.getResult().getString("membership");

                            mOtherProfileName.setText(name);
                            mImageUri = Uri.parse(imageUrl);
                            Glide.with(OtherProfileActivity.this).load(imageUrl).into(mOtherProfileImage);

                            if (memberShip.equals("1")) {
                                mOtherMembershipIcon.setVisibility(View.VISIBLE);
                            } else {
                                mOtherMembershipIcon.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            });
        }
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
        Toast.makeText(OtherProfileActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}