package com.hkct.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class AddEventsActivity extends AppCompatActivity {

    /* <!-- NoteDemo --> */
    // Properties
    private String TAG = "AddEventsActivity===>";
    private EditText noteDesc;
    private Button btnAdd;
    private DBHelper dbhelper = new DBHelper(this);

    private CardView card_view1;
    private CardView card_view3;

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
        setContentView(R.layout.activity_add_events);

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
                textDate.setText("日期："+year+"/"+(month+1)+"/"+dayOfMonth);//使其月份+1顯示
            }
        };

        //time裡面dialog時間的選擇給Calendar.xxx及時間的顯示
        timeDialog= new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar1.set(Calendar.HOUR,hourOfDay);//小時
                calendar1.set(Calendar.MINUTE,minute);//分鐘
                textTime.setText("時間："+hourOfDay+"時"+minute+"分");
            }
        };
        // =========================================================

        Log.d(TAG,"onCreate()");

        setTitle(R.string.title_add);

        // references
        noteDesc = findViewById(R.id.noteDesc);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onCreate()->btnAdd->onClick()");
                HashMap<String, String> queryValues =  new  HashMap<String, String>();
                queryValues.put("noteDesc", noteDesc.getText().toString());
                dbhelper.addNote(queryValues);
                startActivity(new Intent(getApplicationContext(),EventsActivity.class));
                AddEventsActivity.this.finish();
            }
        });

        card_view1 = findViewById(R.id.card_view1);

        card_view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddEventsActivity.this,EventsActivity.class);
                startActivity(intent);
//                startActivity(new Intent(getApplicationContext(),EventsActivity.class));
//                AddEventsActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        card_view3 = findViewById(R.id.card_view3);

        card_view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddEventsActivity.this,MapActivity.class);
                startActivity(intent);
                AddEventsActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


    } //onCreate()
    /* <!-- NoteDemo --> */

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


    public void backClick(View v){
        startActivity(new Intent(this,EventsActivity.class));

        int version = Integer.valueOf(android.os.Build.VERSION.SDK);
        if(version >=5){

//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        this.finish();
    }
}