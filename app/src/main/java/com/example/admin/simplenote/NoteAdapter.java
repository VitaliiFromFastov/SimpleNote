package com.example.admin.simplenote;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.simplenote.data.NoteContract.NoteEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin on 25.03.2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

    Cursor mCursor;
    Context mContext;

    public NoteAdapter(Context context){mContext=context;}

    //create new TaskViewHolder
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_item,parent,false);
        return new NoteViewHolder(view);
    }
    //bind item views with data from database
    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {

        //get column indices
        int idIndex = mCursor.getColumnIndex(NoteEntry._ID);
        int textIndex =  mCursor.getColumnIndex(NoteEntry.COLUMN_TEXT);
        int timestampIndex = mCursor.getColumnIndex(NoteEntry.COLUMN_TIMESTAMP);
        int labelIndex = mCursor.getColumnIndex(NoteEntry.COLUMN_LABEL);

        // move to the right row position in database table
        mCursor.moveToPosition(position);

        //get values using indices above
          final long id = mCursor.getInt(idIndex);
        String text = mCursor.getString(textIndex);
        String time = mCursor.getString(timestampIndex);
         int label  = mCursor.getInt(labelIndex);


        //put values into item views
        holder.itemView.setTag(id);
        holder.noteTextTextView.setText(text);
        holder.noteTimeTextView.setText(time);
        holder.noteCardView.setCardBackgroundColor(NoteUtils.getLabelColor(label,mContext));

        // open NoteEditorActivity when itemView is clicked.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,NoteEditorActivity.class);
                Uri uriForCurrentPosition = ContentUris.withAppendedId(NoteEntry.CONTENT_URI,id);
                intent.setData(uriForCurrentPosition);
                mContext.startActivity(intent);
            }
        });
    }
    // return number of item to be displayed
    @Override
    public int getItemCount() {
        if (mCursor==null){ return 0;}
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == cursor) {
            return null; //  nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = cursor; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }



    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTextTextView;
        TextView noteTimeTextView;
        CardView noteCardView;

        public NoteViewHolder(View itemView) {
            super(itemView);

            noteTextTextView = (TextView) itemView.findViewById(R.id.note_item_text);
            noteTimeTextView = (TextView) itemView.findViewById(R.id.note_item_timestamp);
            noteCardView = (CardView) itemView.findViewById(R.id.note_card_view);
        }
    }
}
