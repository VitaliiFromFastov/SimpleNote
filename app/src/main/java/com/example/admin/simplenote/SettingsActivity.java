package com.example.admin.simplenote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

public class SettingsActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //use SharedPreferences when Activity starts
        setSharedPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SettingsActivity.this, NotesListActivity.class);
        startActivity(intent);

        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home){

            Intent intent = new Intent(SettingsActivity.this, NotesListActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
