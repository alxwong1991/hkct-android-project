package com.hkct.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    /* <!-- NoteDemo --> */
    // Properties
    private static final String TAG = "DBHelper===>";

    // Constructor
    public DBHelper(Context c) {
        super(c, "notedemo.db", null, 1);
        Log.d(TAG,"DBHelper constructor");
    } //DBHelper()

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(TAG,"onCreate()");
        String query;
        query = "CREATE TABLE notetable ( noteId INTEGER PRIMARY KEY, noteDesc TEXT)";
        database.execSQL(query);
    } //onCreate()

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        Log.d(TAG,"onUpgrade()");
        String query;
        query = "DROP TABLE IF EXISTS noteTable";
        database.execSQL(query);
        onCreate(database);
    } //onUpgrade()

    public void addNote(HashMap<String, String> queryValues) {
        Log.d(TAG,"addNote()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("noteDesc", queryValues.get("noteDesc"));
        database.insert("noteTable", null, values);
        database.close();
    } //addNote()

    public int updateNote(HashMap<String, String> queryValues) {
        Log.d(TAG,"updateNote()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("noteDesc", queryValues.get("noteDesc"));
        return database.update("noteTable",
                values,
                "noteId" + " = ?",
                new String[] { queryValues.get("noteId") }
        );
    } //updateNote()

    public void deleteNote(String id) {
        Log.d(TAG,"deleteNote");
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM notetable where noteId='"+ id +"'";
        Log.d(TAG,"deleteNote()->query="+deleteQuery);
        database.execSQL(deleteQuery);
    } //deleteNote()

    public ArrayList<HashMap<String, String>> getAllNotes() {
        ArrayList<HashMap<String, String>> notelist =
                new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM notetable";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("noteId", cursor.getString(0));
                map.put("noteDesc", cursor.getString(1));
                Log.d(TAG,"noteId->"+cursor.getString(0)+",noteDesc->"+cursor.getString(1));
                notelist.add(map);
            } while (cursor.moveToNext());
        }

        // return list
        return notelist;
    } //getAllNotes()

    public HashMap<String, String> getNoteById(String id) {
        HashMap<String, String> notelist = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM noteTable where noteId='"+id+"'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                notelist.put("noteDesc", cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return notelist;
    } //getNoteById()
    /* <!-- NoteDemo --> */

}
