package com.example.admin.simplenote.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.admin.simplenote.data.NoteContract.NoteEntry;

import static android.R.attr.version;

/**
 * Created by Admin on 25.03.2017.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    private static final String NOTE_DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;


    public NoteDbHelper(Context context) {
        super(context, NOTE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold notes data
        final String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NoteEntry.COLUMN_TEXT + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_LABEL + " INTEGER DEFAULT "+NoteEntry.NO_LABEL+", " +
                NoteEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_NOTES_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
// For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
