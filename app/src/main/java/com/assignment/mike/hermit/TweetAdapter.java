package com.assignment.mike.hermit;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        // Read in the user icon to find the correct value:
        String stringVal = cursor.getString(TwitterWallActivity.COL_TWEET_USER_ICON);
        ImageView iconView = (ImageView) view.findViewById(R.id.user_icon);
        iconView.setImageResource(TweetDataUtility.getUserIconReferenceFromString(stringVal));

        // Read in the user handle
        stringVal = cursor.getString(TwitterWallActivity.COL_TWEET_HANDLE);
        TextView textView = (TextView)view.findViewById(R.id.user_handle_textview);
        textView.setText(stringVal);

        // Read in the user display name
        stringVal = cursor.getString(TwitterWallActivity.COL_TWEET_DISPLAY_NAME);
        textView = (TextView)view.findViewById(R.id.user_display_name_textview);
        textView.setText(stringVal);

        // Read in the content of the tweet
        stringVal = cursor.getString(TwitterWallActivity.COL_TWEET_CONTENT);
        textView = (TextView)view.findViewById(R.id.tweet_content_textview);
        textView.setText(stringVal);

        // Read the posted time
        long longVal = cursor.getLong(TwitterWallActivity.COL_TWEET_POST_TIMESTAMP);
        textView = (TextView)view.findViewById(R.id.post_time_textview);
        textView.setText(TweetDataUtility.formatTimeString(longVal));

        // Read the number Of replies
        longVal = cursor.getLong(TwitterWallActivity.COL_TWEET_NUM_REPLIES);
        textView = (TextView)view.findViewById(R.id.reply_count_textview);
        textView.setText(Long.toString(longVal));

        // read the number of retweets
        longVal = cursor.getLong(TwitterWallActivity.COL_TWEET_NUM_RETWEETS);
        textView = (TextView)view.findViewById(R.id.retweet_count_textview);
        textView.setText(Long.toString(longVal));

        // read the number of likes
        longVal = cursor.getLong(TwitterWallActivity.COL_TWEET_NUM_LIKES);
        textView = (TextView)view.findViewById(R.id.like_count_textview);
        textView.setText(Long.toString(longVal));
    }
}
