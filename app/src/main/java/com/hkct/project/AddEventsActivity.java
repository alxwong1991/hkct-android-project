package com.hkct.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class AddEventsActivity extends AppCompatActivity {

    /* <!-- NoteDemo --> */
    // Properties
    private String TAG = "AddEventsActivity===>";
    private EditText noteDesc;
    private Button btnAdd;
    private DBHelper dbhelper = new DBHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

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
    } //onCreate()
    /* <!-- NoteDemo --> */


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