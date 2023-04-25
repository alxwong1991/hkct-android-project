package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private final String TAG="ProfileActivity===>";
    private TextView txtOutput, mProfileName;
    private CircleImageView mProfileImage;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseFirestore firestore;
    private String Uid;
    private FirebaseAuth auth;
    private Uri mImageUri;
    private Query query;
    private ListenerRegistration listenerRegistration;
    private List<Post> posts;
    private List<Users> user;
    private PostAdapter adapter;

    // test member
//    private Button mTestBtn;
//    private Button mCancelTestBtn;
    private ImageView mMembershipIcon;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // access to firestore
        firestore = FirebaseFirestore.getInstance();

        // access to user's uid
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();

        mProfileName = findViewById(R.id.profile_name_personal);
        mProfileImage = findViewById(R.id.profile_pic_personal);


        // test member
//        mTestBtn = findViewById(R.id.test_member_btn);
//        mCancelTestBtn = findViewById(R.id.test_cancel_member_btn);
        mMembershipIcon = findViewById(R.id.membership_icon);

        mRecyclerView = findViewById(R.id.recyclerViewPosts);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
//        int numberOfColumns = 3;
//        LinearLayoutManager linearLayoutManager = new GridLayoutManager(ProfileActivity.this, numberOfColumns);
//        mRecyclerView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();
        user = new ArrayList<>();
        adapter = new PostAdapter(ProfileActivity.this, posts, user);
        mRecyclerView.setAdapter(adapter);

//        mRecyclerView.setAdapter(profilePostAdapter);

        if (auth.getCurrentUser() != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isBottom = !mRecyclerView.canScrollVertically(1);
                    if (isBottom)
                        Toast.makeText(ProfileActivity.this, "Reached Bottom", Toast.LENGTH_SHORT).show();
                }
            });

            // get current user posts
            query = firestore.collection("Posts").whereEqualTo("user", auth.getCurrentUser().getUid()).orderBy("time", Query.Direction.DESCENDING);

            listenerRegistration = query.addSnapshotListener(ProfileActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this, "No Posts", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        }

        // get membership
//        mTestBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // update firestore user's field
//                firestore.collection("Users").document(Uid).update("membership", "1").addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ProfileActivity.this, "Subscribe!", Toast.LENGTH_SHORT).show();
//                            refreshPage();
//                        } else {
//                            Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });

        // cancel membership
//        mCancelTestBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firestore.collection("Users").document(Uid).update("membership", "0").addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ProfileActivity.this, "Unsubscribe!", Toast.LENGTH_SHORT).show();
//                            refreshPage();
//                        } else {
//                            Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });

        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String imageUrl = task.getResult().getString("image");

                        // get membership status of user
                        String memberShip = task.getResult().getString("membership");
                        mProfileName.setText(name);
                        mImageUri = Uri.parse(imageUrl);

                        // if membership is 1 then icon appear otherwise the icon is invisible
                        if (memberShip.equals("1")) {
                            mMembershipIcon.setVisibility(View.VISIBLE);
                        }

                        Glide.with(ProfileActivity.this).load(imageUrl).into(mProfileImage);
                    }
                }
            }
        });
        Log.d(TAG,"===>profileActivity!!!");
        setNavigationDrawer();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) {
//            startActivity(new Intent(ProfileActivity.this, DiscoverActivity.class));
//            finish();
//        } else {
//            String currentUserId = auth.getCurrentUser().getUid();
//            firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        if (!task.getResult().exists()) {
//                            startActivity(new Intent(ProfileActivity.this, AddPostActivity.class));
//                            finish();
//                        }
//                    }
//                }
//            });
//        }
//    }

    // test membership
    private void refreshPage() {
        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
    }


    private void setNavigationDrawer() {
        // drawer layout instance
        drawerLayout = findViewById(R.id.drawerLayout);
        // Toggle the menu icon
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.syncState();

        // pass the toggle for the drawer layout listener
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

    } //setNavigationDrawer()


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected->" + item.getItemId());
//        if (item.getItemId() == R.id.nav_profile) {
//            Log.d(TAG, "onOptionsItemSelected->" + "id=" + R.id.nav_profile + "title=" + item.getTitle());
//            txtOutput.setText("Profile clicked");
//        }
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
        startActivity(new Intent(ProfileActivity.this, AddPostActivity.class));
    }

    public void menu_notification_click(MenuItem m) {
        startActivity(new Intent(ProfileActivity.this, NotificationActivity.class));
    }

    public void menu_logout_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Toast.makeText(ProfileActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}