package com.example.admin.simplenote;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;

import com.example.admin.simplenote.data.NoteContract;

/**
 * Created by Admin on 28.03.2017.
 */

public class NoteUtils {

    //empty constructor
    public NoteUtils(){}

    // this method is needed to get appropriate color for label picker
    // it is used in NoteAdapter and NoteEditorActivity
    public static int getLabelColor(int label, Context context){
        int color;
        switch (label){
            case NoteContract.NoteEntry.LABEL_EXTRA_HIGH:
                color =   ContextCompat.getColor(context,R.color.label_extra_high);
                break;
            case NoteContract.NoteEntry.LABEL_HIGH:
                color= ContextCompat.getColor(context,R.color.label_high);
                break;
            case NoteContract.NoteEntry.LABEL_MEDIUM:
                color=ContextCompat.getColor(context,R.color.label_middle);
                break;
            case NoteContract.NoteEntry.LABEL_LOW:
                color=ContextCompat.getColor(context,R.color.label_low);
                break;
            default:
                color=ContextCompat.getColor(context,R.color.cardViewColor);

        }
        return color;
    }


}
