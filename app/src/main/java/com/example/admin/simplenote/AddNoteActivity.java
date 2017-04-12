package com.example.admin.simplenote;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;
import com.example.admin.simplenote.data.NoteContract.NoteEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {

    private EditText mNoteTextEditText;
    private LinearLayout mLinearLayout;
    private int mPriorityLabel= NoteEntry.NO_LABEL;
    private String mNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setSharedPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mNoteTextEditText = (EditText) findViewById(R.id.add_note_edit_text);
        mLinearLayout = (LinearLayout) findViewById(R.id.add_text_container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_label) {
            addLabel();
        } else if (item.getItemId() == R.id.action_add_note) {
            //get info added by a user.
            mNoteText = mNoteTextEditText.getText().toString().trim();

            //check if all fields are filled in
            if (TextUtils.isEmpty(mNoteText)) {
                Toast.makeText(this, getString(R.string.enter_note_toast), Toast.LENGTH_SHORT).show();
            } else {
                insetIntoDatabase();
            }
        } else if (item.getItemId() == android.R.id.home) {
            //get info added by a user.
            mNoteText = mNoteTextEditText.getText().toString().trim();

            //check if all fields are filled in
            if (TextUtils.isEmpty(mNoteText)) {
             finish();
            } else {
                insetIntoDatabase();
            }
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //get info added by a user.
        mNoteText = mNoteTextEditText.getText().toString().trim();

        //check if all fields are filled in
        if (TextUtils.isEmpty(mNoteText)) {
finish();
        } else {
            insetIntoDatabase();

        }
    }

    //helper method to insert new note into database
    private void insetIntoDatabase() {


        // Defines an object to contain the new values to insert
        ContentValues values = new ContentValues();

        //put added info into ContentValues object
        values.put(NoteEntry.COLUMN_TEXT, mNoteText);
        values.put(NoteEntry.COLUMN_TIMESTAMP,getDateTime());
        values.put(NoteEntry.COLUMN_LABEL,mPriorityLabel);


            // insert new note to database table
            Uri  newNoteUri = getContentResolver().insert(NoteEntry.CONTENT_URI, values);

            if (newNoteUri != null) {
                Toast.makeText(getBaseContext(), getString(R.string.note_created), Toast.LENGTH_LONG).show();
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
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(AddNoteActivity.this, R.color.label_extra_high));
                                mPriorityLabel=NoteEntry.LABEL_EXTRA_HIGH;
                                break;
                            case 1:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(AddNoteActivity.this, R.color.label_high));
                                mPriorityLabel=NoteEntry.LABEL_HIGH;
                                break;
                            case 2:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(AddNoteActivity.this, R.color.label_middle));
                                mPriorityLabel = NoteEntry.LABEL_MEDIUM;
                                break;
                            case 3:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(AddNoteActivity.this, R.color.label_low));
                                mPriorityLabel = NoteEntry.LABEL_LOW;
                                break;
                            case 4:
                                mLinearLayout.setBackgroundColor(ContextCompat.getColor(AddNoteActivity.this, R.color.cardViewColor));
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
