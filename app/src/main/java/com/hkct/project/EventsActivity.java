package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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

import com.google.android.material.navigation.NavigationView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        txtOutput = findViewById(R.id.txtOutput);

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
                    new String[] { "noteId","noteDesc"},
                    new int[] {R.id.noteId, R.id.noteDesc}
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
        txtOutput.setText(R.string.msg1);
        txtOutput.setTextColor(Color.RED);
        startActivity(new Intent(this, DiscoverActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }
    public void menu2_click(MenuItem m){
        Log.d(TAG,"menu2_click()->" + m.getItemId() + ","+ m.getTitle());
        txtOutput.setText(R.string.msg2);
        txtOutput.setTextColor(Color.BLUE);

        drawerLayout.closeDrawers();
    }
    public void menu3_click(MenuItem m){
        Log.d(TAG,"menu3_click()->" + m.getItemId() + ","+ m.getTitle());
        txtOutput.setText(R.string.msg3);
        txtOutput.setTextColor(Color.GREEN);
        drawerLayout.closeDrawers();
    }

    public void menu5_click(MenuItem menuItem) {
        Log.d(TAG,"menu5_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        startActivity(new Intent(this, EventsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    public void menu4_click(MenuItem m){
        Log.d(TAG,"menu3_click()->" + m.getItemId() + ","+ m.getTitle());
        txtOutput.setText(R.string.msg4);
        txtOutput.setTextColor(Color.CYAN);
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    // add events btn
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void menu_add_click(MenuItem m){
        startActivity(new Intent(getApplicationContext(),AddEventsActivity.class));
    }

    public void backClick(View v){
        startActivity(new Intent(this,AddEventsActivity.class));

        int version = Integer.valueOf(android.os.Build.VERSION.SDK);
        if(version >=5){

//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        this.finish();
    }

}