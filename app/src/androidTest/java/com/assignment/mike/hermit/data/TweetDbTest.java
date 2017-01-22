package com.assignment.mike.hermit.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import org.junit.Before;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mike on 1/21/17.
 */

public class TweetDbTest extends AndroidTestCase {
    public static final String LOG_TAG = TweetDbTest.class.getSimpleName();

    private final String DISPLAY_NAME = "Super Dave";
    private final String HANDLE = "@sprDave";
    private final String ICON_LOC = "http://some_img_loc.com";
    private final long TWEET_TS = 123456789;
    private final String TWEET_CONTENT = "This test tweet is less than 140 char.";
    private final int NUM_LIKES = 2;
    private final int NUM_RETWEETS = 5;
    private final int NUM_REPLIES = 10;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // Start each run with a clean DB.
        mContext.deleteDatabase(TweetDbHelper.DB_NAME);
    }

    // Test to ensure the basic creation of the DB is valid.
    public void testCreateDb() throws Throwable {

        // Make sure we can get write access to the tweet db.
        SQLiteDatabase db = new TweetDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: DB is not properly instantiaated.",
                c.moveToFirst());

        // Verify the schema on the table is correct.
        c = db.rawQuery("PRAGMA table_info(" + TweetContract.TweetEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: Can't access table details from db.",
                c.moveToFirst());

        // Create a set of the column names to compare against table details.
        final HashSet<String> tweetTableColumnSet = new HashSet<String>();
        tweetTableColumnSet.add(TweetContract.TweetEntry._ID);
        tweetTableColumnSet.add(TweetContract.TweetEntry.COLUMN_HANDLE);
        tweetTableColumnSet.add(TweetContract.TweetEntry.COLUMN_DISPLAY_NAME);
        tweetTableColumnSet.add(TweetContract.TweetEntry.COLUMN_USER_ICON);
        tweetTableColumnSet.add(TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP);
        tweetTableColumnSet.add(TweetContract.TweetEntry.COLUMN_CONTENT);
        tweetTableColumnSet.add(TweetContract.TweetEntry.COLUMN_NUM_LIKES);
        tweetTableColumnSet.add(TweetContract.TweetEntry.COLUMN_NUM_REPLIES);
        tweetTableColumnSet.add(TweetContract.TweetEntry.COLUMN_NUM_RETWEETS);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            tweetTableColumnSet.remove(columnName);
        } while(c.moveToNext());

        // If the set is not empty, then the schema does not match the expected column set
        assertTrue("Error: Tweet db schema does not match expected column set",
                tweetTableColumnSet.isEmpty());
        db.close();
    }

    // Test insertion to tweet table and reading the data bak from the table.
    public void testTweetTable() {

        // Get reference to the DB.
        TweetDbHelper dbHelper = new TweetDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert a record into the Tweet db
        ContentValues tweetValues = createTweet();
        long tweetRecordId = db.insert(TweetContract.TweetEntry.TABLE_NAME, null, tweetValues);
        assertTrue(tweetRecordId != -1);

        // Query the DB for the record.
        Cursor tweetCursor = db.query(
                TweetContract.TweetEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Only record in the table should be the one just created. Get it and make
        // sure the values are valid.
        assertTrue( "Error: No Records returned", tweetCursor.moveToFirst() );
        validateCurrentRecord("Error: failed to retrieve same values that were inserted",
                tweetCursor, tweetValues);

        // Make sure there are no more records in the DB
        assertFalse( "Error: Should not have been more than 1 record in the table",
                tweetCursor.moveToNext() );

        // Try a delete:
        int deleteCount = db.delete(TweetContract.TweetEntry.TABLE_NAME, null, null);
        assertTrue("ERROR: only 1 record should be deleted.", deleteCount == 1);

        // Make sure there is nothing left in the db.
        tweetCursor = db.query(
                TweetContract.TweetEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertFalse("ERROR: record was deleted...should not be able to reach another record.", tweetCursor.moveToFirst());

        // Close out the db.
        tweetCursor.close();
        dbHelper.close();
    }

    private void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
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
    private ContentValues createTweet() {
        ContentValues tweetValues = new ContentValues();
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
