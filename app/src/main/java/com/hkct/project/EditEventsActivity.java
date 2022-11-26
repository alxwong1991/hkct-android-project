package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.HashMap;

public class EditEventsActivity extends AppCompatActivity {

    /* <!-- NoteDemo --> */
    // Properties
    private String TAG = "EditActivity===>";
    private TextView txtOutput;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String noteId;
    private EditText noteDesc;

    private EditText eventAddress;
    private EditText eventName;
    private TextView text_date;
    private TextView text_time;

    private Button btnEdit, btnDelete;
    private DBHelper dbhelper = new DBHelper(this);

    //顯示日期、時間
    TextView textDate,textTime;
    //這個dialog的監聽物件(目前空)
    DatePickerDialog.OnDateSetListener pickerDialog;
    TimePickerDialog.OnTimeSetListener timeDialog;
    Calendar calendar = Calendar.getInstance();//用來做date
    Calendar calendar1 = Calendar.getInstance();//用來做time


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events);

        // date time
        // ========================================================
        textDate=findViewById(R.id.text_date);
        textTime=findViewById(R.id.text_time);

        //date裡面dialog的日期選擇給Calendar.xxx及日期文字的顯示
        pickerDialog= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);//年
                calendar.set(Calendar.MONTH,month);//月(*注意：此處的月份從0~11*)
                calendar.set(Calendar.DATE,dayOfMonth);//日
                textDate.setText(+year+" / "+(month+1)+" / "+dayOfMonth);//使其月份+1顯示
            }
        };

        //time裡面dialog時間的選擇給Calendar.xxx及時間的顯示
        timeDialog= new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar1.set(Calendar.HOUR,hourOfDay);//小時
                calendar1.set(Calendar.MINUTE,minute);//分鐘
                textTime.setText(hourOfDay+" : "+minute);
            }
        };
        // =========================================================

        Log.d(TAG,"onCreate()");

        // Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setNavigationDrawer();

        Log.d(TAG,"===>eventsActivity!!!");






        setTitle("");

        noteDesc = findViewById(R.id.noteDesc);
        eventAddress = findViewById(R.id.eventAddress);
        eventName = findViewById(R.id.eventName);
        text_date = findViewById(R.id.text_date);
        text_time = findViewById(R.id.text_time);

        // GetExtra
        Intent objIntent = getIntent();
        noteId = objIntent.getStringExtra("noteId");

        // Get notedesc by noteId
        Log.d(TAG, "onCreate()->getNoteDescByNoteId, NoteId=" + noteId);
        HashMap<String, String> noteList = dbhelper.getNoteById(noteId);

        Log.d(TAG,"onCreate()->noteList->"+noteList.get("noteDesc"));

        if(noteList.size()!=0) {
            Log.d(TAG,"onCreate()->update noteDesc");
            noteDesc.setText(noteList.get("noteDesc"));
            eventAddress.setText(noteList.get("eventAddress"));
            eventName.setText(noteList.get("eventName"));
            text_date.setText(noteList.get("text_date"));
            text_time.setText(noteList.get("text_time"));
        }

        // Edit Button
        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEdit();
            }
        });

        // Delete Button
        btnDelete = findViewById(R.id.btnDel);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDelete();
            }
        });

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

    //    date time
    public void datePicker(View v){
        //建立date的dialog
        DatePickerDialog dialog = new DatePickerDialog(v.getContext(),
                pickerDialog,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    public void timePicker(View v){
        //建立time的dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                timeDialog,
                calendar1.get(Calendar.HOUR),
                calendar1.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }
    //    date time

//    public void backClick(View v){
//        startActivity(new Intent(this,EventsActivity.class));
//
//        int version = Integer.valueOf(android.os.Build.VERSION.SDK);
//        if(version >=5){
//
////            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//        }
//        this.finish();
//    }

    private void doEdit(){
        Log.d(TAG,"doEdit()");
        HashMap<String, String> queryValues =  new  HashMap<String, String>();
        queryValues.put("noteId", noteId);
        queryValues.put("noteDesc", noteDesc.getText().toString());
        queryValues.put("eventName", eventName.getText().toString());
        queryValues.put("eventAddress", eventAddress.getText().toString());
        queryValues.put("text_date", text_date.getText().toString());
        queryValues.put("text_time", text_time.getText().toString());
        dbhelper.updateNote(queryValues);
        startActivity(new Intent(getApplicationContext(),EventsActivity.class));
    } //doEdit()

    private void doDelete(){
        Log.d(TAG,"doDelete()");
        dbhelper.deleteNote(noteId);
        startActivity(new Intent(getApplicationContext(),EventsActivity.class));
    } //doDelete()
    /* <!-- NoteDemo --> */

} //EditActivity