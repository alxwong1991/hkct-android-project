package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class DiscoverActivity extends AppCompatActivity {

    private final String TAG="DiscoverActivity===>";
    private TextView txtOutput;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        txtOutput = findViewById(R.id.txtOutput);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationDrawer();
    }

    private void setNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        Log.d(TAG, "onOptionsItemSelected->" + item.getItemId());
        if (item.getItemId() == R.id.nav_account) {
            Log.d(TAG, "onOptionsItemSelected->" + "id=" + R.id.nav_account + "title=" + item.getTitle());
            txtOutput.setText("Account clicked");
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.nav_menu, menu);
//        return true;
//    }

    public void menu1_click(MenuItem menuItem) {
        Log.d(TAG,"menu1_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        txtOutput.setText(R.string.msg1);
        txtOutput.setTextColor(Color.RED);
        drawerLayout.closeDrawers();
    }

    public void menu2_click(MenuItem menuItem) {
        Log.d(TAG,"menu2_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        txtOutput.setText(R.string.msg2);
        txtOutput.setTextColor(Color.RED);
        drawerLayout.closeDrawers();
    }

    public void menu3_click(MenuItem menuItem) {
        Log.d(TAG,"menu3_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        txtOutput.setText(R.string.msg3);
        txtOutput.setTextColor(Color.RED);
        drawerLayout.closeDrawers();
    }

    public void menu5_click(MenuItem menuItem) {
        Log.d(TAG,"menu5_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        startActivity(new Intent(this, EventsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }


    public void menu4_click(MenuItem menuItem) {
        Log.d(TAG,"menu4_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        txtOutput.setTextColor(Color.RED);
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }
}