package com.example.mrt.ship.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mrt.ship.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrt on 16/10/2016.
 */

public class NotesDatabaseHelper extends SQLiteOpenHelper{
    public static final String TAG = "errorDB";
    // Database Info
    private static final String DATABASE_NAME = "notesDB";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_NOTES = "notes";

    // Notes Table Columns
    private static final String NOTE_ID = "id";
    private static final String NOTE_HEADER = "noteHeader";
    private static final String NOTE_CONTENT = "noteContent";
    private static final String NOTE_DATE = "noteDate";

    private NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static NotesDatabaseHelper instance;
    public static synchronized NotesDatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new NotesDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES +
                "(" +
                    NOTE_ID + " INTEGER PRIMARY KEY," +
                    NOTE_HEADER + " TEXT," +
                    NOTE_CONTENT + " TEXT," +
                    NOTE_DATE + " DATE default current_timestamp" +
                ")";
        db.execSQL(CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
            onCreate(db);
        }
    }

    public void addNote(Note note){
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // Wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(NOTE_HEADER, note.getHeader());
            values.put(NOTE_CONTENT, note.getContent());
            db.insertOrThrow(TABLE_NOTES, null, values);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "addNote: " + e.toString());
        }finally {
            db.endTransaction();
        }
    }

    public List<Note> getAllNotes(){
        List<Note> notes = new ArrayList<>();
        String NOTES_SELECT_QUERY = String.format("select * from %s order by %s asc", TABLE_NOTES,
                NOTE_DATE);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(NOTES_SELECT_QUERY, null);
        try{
            if(cursor.moveToFirst()){
                do{
                    Note note = new Note();
                    note.setHeader(cursor.getString(cursor.getColumnIndex(NOTE_HEADER)));
                    note.setContent(cursor.getString(cursor.getColumnIndex(NOTE_CONTENT)));
                    note.setDate(cursor.getString(cursor.getColumnIndex(NOTE_DATE)));
                    note.setId(cursor.getInt(cursor.getColumnIndex(NOTE_ID)));
                    notes.add(note);
                }while(cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "getAllNotes: " + e.toString());
        } finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return notes;
    }

    public void updateNote(Note note){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(NOTE_HEADER, note.getHeader());
            values.put(NOTE_CONTENT, note.getContent());
            db.update(TABLE_NOTES, values, NOTE_ID + " = " + note.getId(), null);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "updateNote: " + e.toString());
        }finally {
            db.endTransaction();
        }

    }

    public void deleteNote(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            db.delete(TABLE_NOTES, NOTE_ID + " = " + id, null);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "deleteNote: " + e.toString());
        } finally {
            db.endTransaction();
        }
    }
}
