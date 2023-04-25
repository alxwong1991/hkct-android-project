package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Queue;

public class DiscoverActivity extends AppCompatActivity {

    private final String TAG="DiscoverActivity===>";
    private TextView txtOutput;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private RecyclerView mRecyclerView;
    private PostAdapter adapter;
    private List<Post> postList;
    private Query query;
    private ListenerRegistration listenerRegistration;
    private List<Users> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DiscoverActivity.this));

        postList = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new PostAdapter(DiscoverActivity.this, postList, usersList);
        mRecyclerView.setAdapter(adapter);

        if (firebaseAuth.getCurrentUser() != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isBottom = !mRecyclerView.canScrollVertically(1);
                    if (isBottom)
                        Toast.makeText(DiscoverActivity.this, "Reached Bottom", Toast.LENGTH_SHORT).show();
                }
            });

            // get all users posts
            query = firestore.collection("Posts").orderBy("time", Query.Direction.DESCENDING);

            listenerRegistration = query.addSnapshotListener(DiscoverActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange doc : value.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String postId = doc.getDocument().getId();
                            Post post = doc.getDocument().toObject(Post.class).withId(postId);
                            String postUserId = doc.getDocument().getString("user");
                            firestore.collection("Users").document(postUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Users users = task.getResult().toObject(Users.class);
                                        usersList.add(users);
                                        postList.add(post);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(DiscoverActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listenerRegistration.remove();
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(DiscoverActivity.this, LoginActivity.class));
            finish();
        } else {
            String currentUserId = firebaseAuth.getCurrentUser().getUid();
            firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            startActivity(new Intent(DiscoverActivity.this, SetUpActivity.class));
                            finish();
                        }
                    }
                }
            });
        }
    }

    private void setNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected->" + item.getItemId());
        if (item.getItemId() == R.id.nav_discover) {
            Log.d(TAG, "onOptionsItemSelected->" + "id=" + R.id.nav_discover + "title=" + item.getTitle());
            txtOutput.setText("Discover clicked");
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Menu
    //   Discover
    public void menu1_click(MenuItem menuItem) {
        Log.d(TAG,"menu1_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(new Intent(this, DiscoverActivity.class));
        drawerLayout.closeDrawers();
    }
    //   Profile
    public void menu2_click(MenuItem menuItem) {
        Log.d(TAG,"menu2_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        startActivity(new Intent(this, ProfileActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //   Events
    public void menu3_click(MenuItem menuItem) {
        Log.d(TAG,"menu3_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        startActivity(new Intent(this, EventActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //    MembershipActivity
    public void menu4_click(MenuItem menuItem) {
        Log.d(TAG, "menu4_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
        startActivity(new Intent(this, MembershipActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //   Store
    public void menu5_click(MenuItem menuItem) {
        Log.d(TAG, "menu5_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
        startActivity(new Intent(this, StoreActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.posts_add, menu);
        getMenuInflater().inflate(R.menu.profile, menu);
        getMenuInflater().inflate(R.menu.notifications, menu);
        getMenuInflater().inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void menu_profile_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),SetUpActivity.class));
    }

    public void menu_add_post_click(MenuItem m) {
        startActivity(new Intent(DiscoverActivity.this, AddPostActivity.class));
    }

    public void menu_notification_click(MenuItem m) {
        startActivity(new Intent(DiscoverActivity.this, NotificationActivity.class));
    }

    public void menu_logout_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Toast.makeText(DiscoverActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}