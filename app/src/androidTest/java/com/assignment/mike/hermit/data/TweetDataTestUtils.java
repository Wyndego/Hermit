package com.assignment.mike.hermit.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Mike on 1/22/17.
 * Helper class with common operations for various test classes.
 */

public class TweetDataTestUtils extends AndroidTestCase {

    private static final long USER_ID = 55566677L;
    private static final String DISPLAY_NAME = "Super Dave";
    private static final String HANDLE = "@sprDave";
    private static final String ICON_LOC = "http://some_img_loc.com";
    private static final long TWEET_TS = 123456789L;
    private static final String TWEET_CONTENT = "This test tweet is less than 140 char.";
    private static final int NUM_LIKES = 2;
    private static final int NUM_RETWEETS = 5;
    private static final int NUM_REPLIES = 10;

    public static  void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    // Creates a test value for a tweet
    public static ContentValues createTweet() {
        ContentValues tweetValues = new ContentValues();
        tweetValues.put(TweetContract.TweetEntry.COLUMN_USER_ID, USER_ID);
        tweetValues.put(TweetContract.TweetEntry.COLUMN_DISPLAY_NAME, DISPLAY_NAME);
        tweetValues.put(TweetContract.TweetEntry.COLUMN_HANDLE, HANDLE);
        tweetValues.put(TweetContract.TweetEntry.COLUMN_USER_ICON, ICON_LOC);
        tweetValues.put(TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP, TWEET_TS);
        tweetValues.put(TweetContract.TweetEntry.COLUMN_CONTENT, TWEET_CONTENT);
        tweetValues.put(TweetContract.TweetEntry.COLUMN_NUM_LIKES, NUM_LIKES);
        tweetValues.put(TweetContract.TweetEntry.COLUMN_NUM_REPLIES, NUM_REPLIES);
        tweetValues.put(TweetContract.TweetEntry.COLUMN_NUM_RETWEETS, NUM_RETWEETS);

        return tweetValues;
    }
}
