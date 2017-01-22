package com.assignment.mike.hermit.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Mike on 1/22/17.
 */

public class TweetProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int TWEET = 100;
    private static final int TWEET_BY_USER = 101;
    private static final int TWEET_BY_USER_FROM_START_TIME = 102;
    private static final int TWEET_FROM_START_TIME = 103;

    private TweetDbHelper mTweetDbHelper;

    // Helper method to setup possible Content provider Uri match patterns.
    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI you want to add, create a corresponding code.
        uriMatcher.addURI(TweetContract.CONTENT_AUTHORITY, TweetContract.PATH_TWEET, TWEET);
        uriMatcher.addURI(TweetContract.CONTENT_AUTHORITY, TweetContract.PATH_TWEET + "/*", TWEET_BY_USER);
        uriMatcher.addURI(TweetContract.CONTENT_AUTHORITY, TweetContract.PATH_TWEET + "/*/#", TWEET_BY_USER_FROM_START_TIME);

        return uriMatcher;
    }

    private static final String tweetByUserIdSelection =
            TweetContract.TweetEntry.TABLE_NAME + "." +
                    TweetContract.TweetEntry.COLUMN_USER_ID + " = ? ";
    private static final String tweetByUserIdFromStartTimeSelection =
            tweetByUserIdSelection + " AND " + TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP +
            " >= ? ";

    @Override
    public boolean onCreate() {
        mTweetDbHelper = new TweetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int uriMatch = uriMatcher.match(uri);
        Cursor result;
        switch (uriMatch) {
            case TWEET_BY_USER:
            case TWEET_BY_USER_FROM_START_TIME:
                result = getTweetsForUser(uri, projection, sortOrder);
                break;
            case TWEET:
                result = getTweets(uri, projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("URI not found: " + uri);
        }

        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int uriMatch = uriMatcher.match(uri);
        switch (uriMatch) {
            case TWEET:
            case TWEET_BY_USER:
            case TWEET_BY_USER_FROM_START_TIME:
                // All types return a collection of results.
                return TweetContract.TweetEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("URI not found: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mTweetDbHelper.getWritableDatabase();
        final int uriMatch = uriMatcher.match(uri);

        Uri returnUri;

        switch(uriMatch) {
            case TWEET:
                long insertedId = db.insert( TweetContract.TweetEntry.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    returnUri = TweetContract.TweetEntry.buildTweetUri(insertedId);
                } else {
                    throw new SQLException("Failed to insert row for " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mTweetDbHelper.getWritableDatabase();
        final int uriMatch = uriMatcher.match(uri);

        int deletedRows;
        switch(uriMatch) {
            case TWEET:
                deletedRows = db.delete( TweetContract.TweetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        // only notify if there was actually a change to the data. Since a null selection
        // means delete everything, this counts.
        if (deletedRows != 0 || selection == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mTweetDbHelper.getWritableDatabase();
        final int uriMatch = uriMatcher.match(uri);

        int updatedRows;
        switch(uriMatch) {
            case TWEET:
                updatedRows = db.update( TweetContract.TweetEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        if (updatedRows != 0 || selection == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mTweetDbHelper.getWritableDatabase();
        final int uriMatch = uriMatcher.match(uri);
        switch (uriMatch) {
            case TWEET:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TweetContract.TweetEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getTweetsForUser(Uri uri, String[] projection, String sortOrder) {
        long userId = TweetContract.TweetEntry.getUserIdFromUri(uri);
        long startTime = TweetContract.TweetEntry.getStartTimeFromUri(uri);

        String selection;
        String[] selectionArgs;

        if (startTime == 0) {
            selection = tweetByUserIdSelection;
            selectionArgs = new String[]{Long.toString(userId)};
        } else {
            selection = tweetByUserIdFromStartTimeSelection;
            selectionArgs = new String[]{Long.toString(userId), Long.toString(startTime)};
        }

        final SQLiteDatabase db = mTweetDbHelper.getReadableDatabase();

        return db.query(
                TweetContract.TweetEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTweets(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mTweetDbHelper.getReadableDatabase();

        return db.query(
                TweetContract.TweetEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

}
