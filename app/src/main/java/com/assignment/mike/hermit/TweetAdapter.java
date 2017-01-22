package com.assignment.mike.hermit;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assignment.mike.hermit.data.TweetDataUtility;

/**
 * Class to handle conversion of Tweet data model values into usable UI
 * inputs.
 *
 * Created by Mike on 1/22/17.
 */

public class TweetAdapter extends CursorAdapter {

    public TweetAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_tweet, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView)view;
        ((TextView) view).setText(TweetDataUtility.convertCursorRowToTweet(cursor).toString());
    }
}
