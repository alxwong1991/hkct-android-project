package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.Adapter.EventAdapter;
import com.hkct.project.Model.Event;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    private final String TAG="EventActivity===>";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private RecyclerView mRecyclerView;
    private EventAdapter adapter;
    private Query query;
    private ListenerRegistration listenerRegistration;
    private List<Users> usersList;
    private List<Event> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.recyclerViewEventPosts);

        list = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new EventAdapter(EventActivity.this, list, usersList);

        Log.d(TAG,"===>eventActivity!!!");

        if (firebaseAuth.getCurrentUser() != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isBottom = !mRecyclerView.canScrollVertically(1);
                    if (isBottom) {
                        Toast.makeText(EventActivity.this, "Reached Bottom", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            query = firestore.collection("Events").orderBy("time", Query.Direction.DESCENDING);

            listenerRegistration = query.addSnapshotListener(EventActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange doc : value.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String eventId = doc.getDocument().getId();
                            Event event = doc.getDocument().toObject(Event.class).withId(eventId);
                            String eventUserId = doc.getDocument().getString("user");
                            firestore.collection("Users").document(eventUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Users users = task.getResult().toObject(Users.class);
                                        usersList.add(users);
                                        list.add(event);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(EventActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void setNavigationDrawer() {
        Log.d("hihihihihi","here");
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    // Menu
    //   Discover
    public void menu1_click(MenuItem menuItem) {
        Log.d(TAG,"menu1_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(new Intent(this, DiscoverActivity.class));
        drawerLayout.closeDrawers();
    }

//    public void menu2_click(MenuItem menuItem) {
//        Log.d(TAG,"menu2_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
////        txtOutput.setText(R.string.msg2);
////        txtOutput.setTextColor(Color.RED);
//        drawerLayout.closeDrawers();
//    }

    //   Profile
    public void menu3_click(MenuItem menuItem) {
        Log.d(TAG,"menu3_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
//        txtOutput.setText(R.string.msg3);
//        txtOutput.setTextColor(Color.RED);
        startActivity(new Intent(this, ProfileActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //   Events
    public void menu5_click(MenuItem menuItem) {
        Log.d(TAG,"menu5_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        startActivity(new Intent(this, EventActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //    MembershipActivity
    public void menu6_click(MenuItem menuItem) {
        Log.d(TAG, "menu6_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
        startActivity(new Intent(this, MembershipActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //   QR code
    public void menu7_click(MenuItem menuItem) {
        Log.d(TAG, "menu7_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
        startActivity(new Intent(this, QRcodeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events_add, menu);
        getMenuInflater().inflate(R.menu.profile, menu);
        getMenuInflater().inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void menu_profile_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),SetUpActivity.class));
    }

    public void menu_add_event_click(MenuItem m) {
        startActivity(new Intent(EventActivity.this, AddEventActivity.class));
    }

    public void menu_logout_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Toast.makeText(EventActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}