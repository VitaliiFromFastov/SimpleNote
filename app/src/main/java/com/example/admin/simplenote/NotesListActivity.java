package com.example.admin.simplenote;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static com.example.admin.simplenote.data.NoteContract.*;

public class NotesListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "NoteListActivity";
    private static final int LOADER_ID = 1;
    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private String mSortOrder;
    private String mSearchTerm = null;
    private int mNumberOfNoteColumns;
    private TextView mEmptyView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //use SharedPreferences when Activity starts
        setSharedPreferences();
        super.onCreate(savedInstanceState);
               setContentView(R.layout.content_notes_list);

        // get screen size and orientation
        determineScreenSize();

        mRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);
        mEmptyView = (TextView) findViewById(R.id.text_empty);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(mNumberOfNoteColumns,
                StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new NoteAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        //open NoteEditorActivity when fab is clicked
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesListActivity.this, AddNoteActivity.class);
                startActivity(intent);

            }
        });

        //initialise Loader
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        // Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                TextView title = new TextView(NotesListActivity.this);
                title.setText(getString(R.string.delete_note_message));
                title.setPadding(16, 16, 16, 16);
                title.setGravity(Gravity.CENTER); // this is required to bring it to center.
                title.setTextSize(22);
                title.setTextColor(ContextCompat.getColor(NotesListActivity.this,android.R.color.black));

                // Retrieve the id of the task to delete
                final long id = (long) viewHolder.itemView.getTag();
                AlertDialog.Builder alert = new AlertDialog.Builder(NotesListActivity.this);
                alert.setCustomTitle(title);
                alert.setPositiveButton(getString(R.string.delete_task), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
// Build appropriate uri with String row id appended
                        String stringId = Long.toString(id);
                        Uri uri = NoteEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(stringId).build();

                        //  Delete a single row of data using a ContentResolver
                        getContentResolver().delete(uri, null, null);

                        //Restart the loader to re-query for all notes after a deletion
                        getSupportLoaderManager().restartLoader(LOADER_ID, null, NotesListActivity.this);

                    }
                });
                alert.setNegativeButton(getString(R.string.cancel_deleting), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                        //Restart the loader to re-query for all notes after a deletion
                        getSupportLoaderManager().restartLoader(LOADER_ID, null, NotesListActivity.this);
                    }
                });
              AlertDialog alertDialog = alert.show();

                      alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        //Restart the loader to re-query for all notes after a deletion
                        getSupportLoaderManager().restartLoader(LOADER_ID, null, NotesListActivity.this);
                    }
                });


            }
        })
                .attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
               // get screen size and orientation
        determineScreenSize();

        // restart loader
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes_list, menu);


        MenuItem searchItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) NotesListActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(NotesListActivity.this.getComponentName()));
        }

        final SearchView finalSearchView = searchView;

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                mSearchTerm = finalSearchView.getQuery().toString().trim();
                getSupportLoaderManager().restartLoader(LOADER_ID, null, NotesListActivity.this);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(NotesListActivity.this, SettingsActivity.class);
            startActivity(intent);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)  {
        return new AsyncTaskLoader<Cursor>(this) {

            // create Cursor to hold task data
            Cursor taskCursor = null;

            @Override
            protected void onStartLoading() {
                if (taskCursor != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(taskCursor);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                if (mSearchTerm != null) {
                    // Defines selection criteria for the rows to query
                    String selectionClause = NoteEntry.COLUMN_TEXT + " like ?";
                    String[] selectionArgs = {"%" + mSearchTerm + "%"};
                    try {
                        return getContentResolver().query(NoteEntry.CONTENT_URI,
                                null, selectionClause,
                                selectionArgs, mSortOrder);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load data.");
                        return null;
                    }
                } else {
                    try {
                        return getContentResolver().query(NoteEntry.CONTENT_URI,
                                null, null, null, mSortOrder);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load data.");
                        return null;
                    }
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                taskCursor = data;

                // When database doesn't have any notes show empty view, otherwise show existing notes
                if (!taskCursor.moveToFirst() || taskCursor.getCount() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) { mAdapter.swapCursor(cursor); }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            sortNotes(sharedPreferences.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_time_value)));
        } else if (key.equals(getString(R.string.pref_app_style_key))){
            changeAppTheme(sharedPreferences.getString(getString(R.string.pref_app_style_key), getString(R.string.pref_light_app_style_value)));
        }
    }

    // set shared preferences to use them in Settings Activity
    private void setSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sortNotes(sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_time_value)));

        changeAppTheme(sharedPreferences.getString(getString(R.string.pref_app_style_key),getString(R.string.pref_light_app_style_value)));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    // method to sort noted by time or label color
    private void sortNotes(String sortKey) {

        if (sortKey.equals(getString(R.string.pref_sort_time_value))) {

            mSortOrder = NoteEntry.COLUMN_TIMESTAMP + " DESC";

        } else if (sortKey.equals(getString(R.string.pref_sort_color_value))) {

            mSortOrder = NoteEntry.COLUMN_LABEL + " ASC";
        }
    }

    //method to change app Style (light, dark, hot) in notes
    private void changeAppTheme(String textSizeKey) {


        if (textSizeKey.equals(getString(R.string.pref_light_app_style_value))) {

        setTheme(R.style.AppThemeLight);

        } else if (textSizeKey.equals(getString(R.string.pref_dark_app_style_value))) {

            setTheme(R.style.AppThemeDark);

        } else if (textSizeKey.equals(getString(R.string.pref_hot_app_style_value))) {

            setTheme(R.style.AppThemeHot);
        }

    }

    // method to determine Screen size and orientation of a user's device
    public void determineScreenSize() {

        int smallestDp = getResources().getConfiguration().smallestScreenWidthDp;

        if(smallestDp >= 720) {
            // For 10” tablets (720dp wide and bigger)
            if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                // large tablet screen Portrait mode
                mNumberOfNoteColumns = 4;
            } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                // large tablet screen landscape mode
                mNumberOfNoteColumns = 6;

            } else{
                // large tablet screen orientation undefined
                mNumberOfNoteColumns = 4;
            }
        }else if(smallestDp >= 600 && smallestDp < 720) {
            // For 7” tablets (600dp wide and bigger)
            if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                //  tablet screen Portrait mode
                mNumberOfNoteColumns = 3;
            } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                //  tablet screen landscape mode
                mNumberOfNoteColumns = 5;

            } else{
                //  tablet screen orientation undefined
                mNumberOfNoteColumns = 3;
            }

        }else if(smallestDp >= 300 && smallestDp < 600) {
            //For handsets (smaller than 600dp and bigger than 300 available width and )
                if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    // handset screen Portrait mode
                    mNumberOfNoteColumns = 2;
                } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    // handset screen landscape mode
                    mNumberOfNoteColumns = 3;

                } else{
                    // handset screen orientation undefined
                    mNumberOfNoteColumns = 2;
                }

        } else {
            //For handsets (smaller than 300dp available width)
            if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                // handset screen Portrait mode
                mNumberOfNoteColumns = 1;
            } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                // handset screen landscape mode
                mNumberOfNoteColumns = 2;

            } else{
                // handset screen orientation undefined
                mNumberOfNoteColumns = 1;
            }
        }

    }



}
