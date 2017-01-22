package com.assignment.mike.hermit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mike on 1/21/17.
 */

/**
 * Stores the SQL implementation of the tweet data model.
 */
public class TweetDbHelper extends SQLiteOpenHelper {

    // Variable to keep track of schema changes.
    public static final int DB_VERSION = 1;

    public static final String DB_NAME = "tweet.db";

    public TweetDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TWEET_TABLE = "CREATE TABLE " + TweetContract.TweetEntry.TABLE_NAME +
                " (" +
                TweetContract.TweetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                TweetContract.TweetEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                TweetContract.TweetEntry.COLUMN_HANDLE + " TEXT NOT NULL, " +
                TweetContract.TweetEntry.COLUMN_DISPLAY_NAME + " TEXT NOT NULL, " +
                TweetContract.TweetEntry.COLUMN_USER_ICON + " TEXT NOT NULL, " +
                TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " INTEGER NOT NULL, " +
                TweetContract.TweetEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                TweetContract.TweetEntry.COLUMN_NUM_REPLIES + " INTEGER NOT NULL, " +
                TweetContract.TweetEntry.COLUMN_NUM_RETWEETS + " INTEGER NOT NULL, " +
                TweetContract.TweetEntry.COLUMN_NUM_LIKES + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_TWEET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since this is not a real db implementation of a real twitter client, this
        // db is only really used as a cache. This the upgrade policy is to just
        // drop the table and recreate.
        db.execSQL("DROP TABLE IF EXISTS " + TweetContract.TweetEntry.TABLE_NAME);
        onCreate(db);
    }
}
