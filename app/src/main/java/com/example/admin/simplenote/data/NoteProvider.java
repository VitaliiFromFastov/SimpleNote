package com.example.admin.simplenote.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.admin.simplenote.data.NoteContract.NoteEntry;


/**
 * Created by Admin on 25.03.2017.
 */

public class NoteProvider extends ContentProvider {

    NoteDbHelper mNoteDbhelper;

    private static final int NOTES = 100;
    private static final int NOTES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(NoteContract.AUTHORITY,NoteContract.PATH_NOTES,NOTES);
        sUriMatcher.addURI(NoteContract.AUTHORITY,NoteContract.PATH_NOTES+"/#",NOTES_WITH_ID);

    }


    /** onCreate() is where you should initialize anything you’ll need to setup
     your underlying data source.
     In this case, you’re working with a SQLite database, so you’ll need to
     initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {

        mNoteDbhelper = new NoteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projections, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase sqLiteDatabase = mNoteDbhelper.getReadableDatabase();
        Cursor cursorToReturn;
        switch (sUriMatcher.match(uri)){

            case NOTES:
                cursorToReturn= sqLiteDatabase.query(NoteEntry.TABLE_NAME,projections,selection,selectionArgs,null,null,sortOrder);
                break;

            case NOTES_WITH_ID:
                selection= NoteEntry._ID +"=?";
                String id = String.valueOf( ContentUris.parseId(uri));
                selectionArgs = new String []{id};
                cursorToReturn = sqLiteDatabase.query(NoteEntry.TABLE_NAME,projections,selection,selectionArgs,null,null,sortOrder);
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        cursorToReturn.setNotificationUri(getContext().getContentResolver(),uri);

        return cursorToReturn;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase sqLiteDatabase = mNoteDbhelper.getWritableDatabase();

        Uri uriToReturn;

        switch (sUriMatcher.match(uri)){
            case NOTES:
                long id = sqLiteDatabase.insert(NoteEntry.TABLE_NAME,null,contentValues);
                if (id>0){
                    uriToReturn= ContentUris.withAppendedId(NoteEntry.CONTENT_URI,id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri,null);

        return uriToReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase sqLiteDatabase= mNoteDbhelper.getWritableDatabase();
        int deletedTask;
        switch (sUriMatcher.match(uri)){
            case NOTES_WITH_ID:
                selection= NoteEntry._ID +"=?";
                String id =uri.getLastPathSegment();
                selectionArgs = new String []{id};
                deletedTask = sqLiteDatabase.delete(NoteEntry.TABLE_NAME,selection,selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (deletedTask!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return deletedTask;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase sqLiteDatabase =  mNoteDbhelper.getWritableDatabase();
        selection= NoteEntry._ID +"=?";
        String id =uri.getLastPathSegment();
        selectionArgs = new String []{id};
        int rowsAffected = sqLiteDatabase.update(NoteEntry.TABLE_NAME,contentValues,selection,selectionArgs);
        if (rowsAffected!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);

        }
        return rowsAffected;

    }
}
