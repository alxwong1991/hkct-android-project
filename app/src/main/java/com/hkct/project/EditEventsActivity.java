package com.hkct.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class EditEventsActivity extends AppCompatActivity {

    /* <!-- NoteDemo --> */
    // Properties
    private String TAG = "EditActivity===>";
    private String noteId;
    private EditText noteDesc;
    private Button btnEdit, btnDelete;
    private DBHelper dbhelper = new DBHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events);

        Log.d(TAG,"onCreate()");

        setTitle(R.string.title_edit);

        noteDesc = findViewById(R.id.noteDesc);

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

    private void doEdit(){
        Log.d(TAG,"doEdit()");
        HashMap<String, String> queryValues =  new  HashMap<String, String>();
        queryValues.put("noteId", noteId);
        queryValues.put("noteDesc", noteDesc.getText().toString());
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