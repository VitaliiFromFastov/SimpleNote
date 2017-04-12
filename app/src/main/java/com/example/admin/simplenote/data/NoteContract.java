package com.example.admin.simplenote.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin on 25.03.2017.
 */

public class NoteContract {

    public static final String AUTHORITY = "com.example.admin.simplenote";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_NOTES = "notes";


    public static class NoteEntry implements BaseColumns {

        //constant to get to existing data in database
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_NOTES).build();

        //constants to create new table in database
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_TIMESTAMP = "timestamp";


        //different label integers
        public static final int LABEL_EXTRA_HIGH = 1;
        public static final int LABEL_HIGH=2;
        public static final int LABEL_MEDIUM = 3;
        public static final int LABEL_LOW = 4;
        public static final int LABEL_EXTRA_LOW = 5;
        public static final int NO_LABEL = 6;

    }
}