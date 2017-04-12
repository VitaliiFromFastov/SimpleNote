package com.example.admin.simplenote;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.admin.simplenote.data.NoteContract.NoteEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
         {

    private static final int LOADER_ID = 1000;

    private EditText mNoteTextEditText;
    private LinearLayout mLinearLayout;
    private Uri mCurrentNoteUri;
    private String mNoteTextString;
    private int mPriorityLabel=NoteEntry.NO_LABEL;
    private String  mNoteText;
    private int mTextCountBeforeEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //use SharedPreferences when Activity starts
        setSharedPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        mNoteTextEditText = (EditText) findViewById(R.id.note_text_edit_text);
        mLinearLayout = (LinearLayout) findViewById(R.id.edit_text_container);
        //get intent data from NoteAdapter
        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();

            //initialise loader
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);



    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteNoteFromDialog();
            return true;
        } else if (id == R.id.action_save) {
            //get info added by a user.
            mNoteText = mNoteTextEditText.getText().toString().trim();

            //check if all fields are filled in
            if (mNoteText.length()==0) {
                Toast.makeText(this, getString(R.string.enter_note_toast), Toast.LENGTH_SHORT).show();
            } else {
                updateNote();
            }
            return true;
        } else if (id==R.id.action_label){
            addLabel();
            return true;
        } if (id==android.R.id.home) {
            //get info added by a user.
            mNoteText = mNoteTextEditText.getText().toString().trim();

            //check if all fields are filled in
            if (mNoteText.length()==0) {
deleteNoteFromDialog();
                return true;
            } else {
                if (mNoteText.length()!=mTextCountBeforeEditing)
                updateNote();
                else {
                    finish();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mCurrentNoteUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        //check if cursor is null
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        //move to the first row in cursor
        if (cursor.moveToFirst()) {
            //get indices of data in cursor
            int noteTextColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_TEXT);
            int noteLabelColumnIndex =cursor.getColumnIndex(NoteEntry.COLUMN_LABEL);

            //get values using indices above
            mNoteTextString = cursor.getString(noteTextColumnIndex);
            mPriorityLabel = cursor.getInt(noteLabelColumnIndex);
            //put retrieved values into appropriate editTexts.
            mNoteTextEditText.setText(mNoteTextString);
            //set background color for the note according to picked label
            mLinearLayout.setBackgroundColor(NoteUtils.getLabelColor(mPriorityLabel,NoteEditorActivity.this));
            // get length of and existing note
            mTextCountBeforeEditing = mNoteTextEditText.getText().toString().length();
            //move EditText cursor to an end of note
            mNoteTextEditText.setSelection(mTextCountBeforeEditing);
        }
        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //put empty strings in editText and change background color.
        mNoteTextEditText.setText("");
        mLinearLayout.setBackgroundColor(ContextCompat.getColor(NoteEditorActivity.this, R.color.cardViewColor));
        mPriorityLabel = NoteEntry.NO_LABEL;
       }

    @Override
    public void onBackPressed() {
        //get info added by a user.
        mNoteText = mNoteTextEditText.getText().toString().trim();

        //check if all fields are filled in
        if (mNoteText.length()==0) {
            deleteNoteFromDialog();
            return;
        } else {
            if (mNoteText.length()!=mTextCountBeforeEditing)
                updateNote();
            else {
                finish();
            }
        }

        }

    //helper method to delete note from database
    private void deleteNote() {

        int rowsDeleted = getContentResolver().delete(mCurrentNoteUri, null, null);
        if (rowsDeleted < 1) {
            Toast.makeText(getBaseContext(), getString(R.string.toast_failed_to_delete_note), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.toast_note_deleted), Toast.LENGTH_SHORT).show();
        }

        finish(); //exit current Activity
    }

    //helper method to show delete dialog
    private void deleteNoteFromDialog() {
        TextView title = new TextView(NoteEditorActivity.this);
        title.setText(getString(R.string.delete_note_message));
        title.setPadding(16, 16, 16, 16);
        title.setGravity(Gravity.CENTER); // this is required to bring it to center.
        title.setTextSize(22);
        title.setTextColor(ContextCompat.getColor(NoteEditorActivity.this,android.R.color.black));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCustomTitle(title);
        alert.setPositiveButton(getString(R.string.delete_task), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                deleteNote();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel_deleting), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    //helper method to update note
    private void updateNote() {

        // Defines an object to contain the new values to insert
        ContentValues values = new ContentValues();

        //put added info into ContentValues object
        values.put(NoteEntry.COLUMN_TEXT, mNoteText);
        values.put(NoteEntry.COLUMN_TIMESTAMP,getDateTime());
        values.put(NoteEntry.COLUMN_LABEL,mPriorityLabel);

                        //update existing task
                int numberOfRowsUpdated = getContentResolver().update(mCurrentNoteUri, values, null, null);

                if (numberOfRowsUpdated == 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.toast_failed_to_update_note), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.toast_successfully_updated_note), Toast.LENGTH_SHORT).show();
                }
            finish();
        }

    //get current time to put into database
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd MMM yyyy, HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void addLabel(){

        final String [] items = new String[]
                {getString(R.string.label_extra_high),
                getString(R.string.label_high),
                getString(R.string.label_medium),
                getString(R.string.label_low),
                getString(R.string.label_no_priority)};
        final Integer[] icons = new Integer[]
                    {R.drawable.ic_label_extra_high_24px,
                    R.drawable.ic_label_high_24px,
                    R.drawable.ic_label_medium_24px,
                    R.drawable.ic_label_low_24px,
                    R.drawable.ic_label_no_priority_24px};
        ListAdapter adapter = new ArrayAdapterWithIcon(this, items, icons);

        new AlertDialog.Builder(this).setTitle(R.string.alert_dialog_title)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item ) {
                        switch (item){
                            case 0:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(NoteEditorActivity.this, R.color.label_extra_high));
                                mPriorityLabel=NoteEntry.LABEL_EXTRA_HIGH;
                                break;
                            case 1:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(NoteEditorActivity.this, R.color.label_high));
                                mPriorityLabel=NoteEntry.LABEL_HIGH;
                                break;
                            case 2:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(NoteEditorActivity.this, R.color.label_middle));
                                mPriorityLabel = NoteEntry.LABEL_MEDIUM;
                                break;
                            case 3:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(NoteEditorActivity.this, R.color.label_low));
                                mPriorityLabel = NoteEntry.LABEL_LOW;
                                break;
                            case 4:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(NoteEditorActivity.this, R.color.cardViewColor));
                                mPriorityLabel = NoteEntry.NO_LABEL;
                        }
                    }
                }).show();
    }

             // method to change app Style (light, dark, hot) in notes
    private void changeAppTheme(String textSizeKey) {


        if (textSizeKey.equals(getString(R.string.pref_light_app_style_value))) {

            setTheme(R.style.AppThemeLight);

        } else if (textSizeKey.equals(getString(R.string.pref_dark_app_style_value))) {

            setTheme(R.style.AppThemeDark);

        } else if (textSizeKey.equals(getString(R.string.pref_hot_app_style_value))) {

            setTheme(R.style.AppThemeHot);
        }

    }

    // set shared preferences to use them in Settings Activity
    private void setSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        changeAppTheme(sharedPreferences.getString(getString(R.string.pref_app_style_key),getString(R.string.pref_light_app_style_value)));
    }
}
