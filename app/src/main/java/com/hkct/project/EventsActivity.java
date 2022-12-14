package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class EventsActivity extends AppCompatActivity {

    private final String TAG="EventsActivity===>";
    private TextView txtOutput;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView noteId;
    private DBHelper dbhelper = new DBHelper(this);
    private ListView listView;

    private CardView card1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);


        //ActivityName
        setTitle("");

        // Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setNavigationDrawer();

        Log.d(TAG,"===>eventsActivity!!!");

        // Create List
        ArrayList<HashMap<String, String>> noteList =  dbhelper.getAllNotes();

        // Construct listView
        if(noteList.size()!=0) {
            listView = findViewById(R.id.listView);
            // Set onclick listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    noteId = view.findViewById(R.id.noteId);
                    String Id = noteId.getText().toString();
                    Intent intent = new Intent(getApplicationContext(),EditEventsActivity.class);
                    intent.putExtra("noteId", Id);
                    startActivity(intent);
                }
            });
            ListAdapter adapter = new SimpleAdapter(EventsActivity.this,
                    noteList,
                    R.layout.note_row,
                    new String[] { "noteId","noteDesc", "eventName", "eventAddress", "text_date", "text_time"},
                    new int[] {R.id.noteId, R.id.noteDesc, R.id.eventName, R.id.eventAddress , R.id.text_date , R.id.text_time}
            );
            listView.setAdapter(adapter);
        }
    } //onCreate()

    private void setNavigationDrawer(){
        // drawer layout instance
        drawerLayout = findViewById(R.id.drawerLayout);
        // Toggle the menu icon
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
        actionBarDrawerToggle.syncState();

        // pass the toggle for the drawer layout listener
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

    } //setNavigationDrawer()

    // override the onOptionsItemSelected() function to implement
    // the item click listener callback to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG,"onOptionsItemSelected->" + item.getItemId());

//        if (item.getItemId()==R.id.nav_account){
//            Log.d(TAG,"onOptionsItemSelected->" + "id=" + R.id.nav_account + "title=" + item.getTitle());
//            txtOutput.setText("Account clicked");
//        }

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    } //onOptionsItemSelected()

    public void menu1_click(MenuItem m){
        Log.d(TAG,"menu1_click()->" + m.getItemId() + ","+ m.getTitle());
        startActivity(new Intent(this, DiscoverActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }
//    public void menu2_click(MenuItem m){
//        Log.d(TAG,"menu2_click()->" + m.getItemId() + ","+ m.getTitle());
////        txtOutput.setText(R.string.msg2);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        drawerLayout.closeDrawers();
//    }

    //   Profile
    public void menu3_click(MenuItem m){
        Log.d(TAG,"menu3_click()->" + m.getItemId() + ","+ m.getTitle());
        startActivity(new Intent(this, ProfileActivity.class));
//        txtOutput.setText(R.string.msg3);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //   Events
    public void menu5_click(MenuItem menuItem) {
        Log.d(TAG,"menu5_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        startActivity(new Intent(this, EventsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

//    MembershipActivity
    public void menu6_click(MenuItem menuItem) {
        Log.d(TAG,"menu6_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
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

//    public void menu4_click(MenuItem m){
//        Log.d(TAG,"menu3_click()->" + m.getItemId() + ","+ m.getTitle());
//        FirebaseAuth.getInstance().signOut();
//        startActivity(new Intent(this, LoginActivity.class));
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        Toast.makeText(EventsActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
//        drawerLayout.closeDrawers();
//    }

    // add events btn
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        getMenuInflater().inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void menu_profile_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),SetUpActivity.class));
    }

    public void menu_add_click(MenuItem m){
        startActivity(new Intent(getApplicationContext(),AddEventsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void menu_logout_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Toast.makeText(EventsActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }


    public void addClick(View v) {
        startActivity(new Intent(this, AddEventsActivity.class));

        int version = Integer.valueOf(android.os.Build.VERSION.SDK);
        if (version >= 5) {

//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        this.finish();
    }
}

