package com.assignment.mike.hermit.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Mike on 1/22/17.
 */

public class TestTweetProvider extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void deleteAllRecords() {
        TweetDbHelper dbHelper = new TweetDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(TweetContract.TweetEntry.TABLE_NAME, null, null);
        db.close();
    }


    // Make sure the Tweet Content Provider is connected properly.
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                TweetProvider.class.getName());
        try {
            // See if the provider is registered with the packageManager
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Validate the authority values in registration and contract match
            assertEquals("Error: TweetProvider registration authorityy: " + providerInfo.authority +
                            " but contract authority is: " + TweetContract.CONTENT_AUTHORITY,
                    providerInfo.authority, TweetContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: TweetProvider has not been registered with " + mContext.getPackageName(),
                    false);
        }
    }

    // Make sure the getType implementation is valid.
    public void testGetType() {

        String type = mContext.getContentResolver().getType(TweetContract.TweetEntry.CONTENT_URI);

        assertEquals("Error: mismatch on TweetEntry.CONTENT_URI",
                TweetContract.TweetEntry.CONTENT_TYPE, type);

        long userId = 1234L;
        type = mContext.getContentResolver().getType(TweetContract.TweetEntry.buildTweetsByUserIdUri(userId));
        assertEquals("Error: mismatch on TweetEntry.buildTweetsByUserIdUri", TweetContract.TweetEntry.CONTENT_TYPE, type);

        long testDate = 1455555;
        type = mContext.getContentResolver().getType(TweetContract.TweetEntry.buildTweetsByUserWithStartTimeUri(userId, testDate));
        assertEquals("Error: mismatch on TweetEntry.buildTweetsByUserIdUri", TweetContract.TweetEntry.CONTENT_TYPE, type);

    }

    // Make sure the query implementation is valid.
    public void testQuery() {

        // Add test data
        TweetDbHelper dbHelper = new TweetDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues tweetValues = TweetDataTestUtils.createTweet();
        long tweetRecordId = db.insert(TweetContract.TweetEntry.TABLE_NAME, null, tweetValues);
        assertTrue(tweetRecordId != -1);

        db.close();

        // Now that there is test data, fetch it using the content provider.
        long userId = tweetValues.getAsLong(TweetContract.TweetEntry.COLUMN_USER_ID);
        Cursor cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.buildTweetsByUserIdUri(userId),
                null,
                null,
                null,
                null
        );

        // Validate retrieved data
        assertTrue("Query returned an invalid cursor", cursor.moveToFirst());
        TweetDataTestUtils.validateCurrentRecord("testQuery", cursor, tweetValues);

        // Now do a query with userid
        cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Validate retrieved data
        assertTrue("Query returned an invalid cursor", cursor.moveToFirst());
        TweetDataTestUtils.validateCurrentRecord("testQuery", cursor, tweetValues);

        // Perform the query with both userid and timestamp
        long timestamp = tweetValues.getAsLong(TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP) - 500;
        cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.buildTweetsByUserWithStartTimeUri(userId, timestamp),
                null,
                null,
                null,
                null
        );

        // Validate retrieved data
        assertTrue("Query returned an invalid cursor", cursor.moveToFirst());
        TweetDataTestUtils.validateCurrentRecord("testQuery", cursor, tweetValues);

        // now query based on time alone:
        cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.CONTENT_URI,
                null,
                TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " > ?",
                new String[]{Long.toString(timestamp)},
                TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " DESC"
        );

        // Now do the query with userid and an invalid timestamp:
        timestamp = tweetValues.getAsLong(TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP) + 500;
        cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.buildTweetsByUserWithStartTimeUri(userId, timestamp),
                null,
                null,
                null,
                null
        );

        // Validate retrieved data
        assertFalse("Query should not return a valid cursor", cursor.moveToFirst());

    }

    // Make sure the content provider create, update, and delete operations are valid.
    public void testCRUD() {

        // Create
        ContentValues tweetValues = TweetDataTestUtils.createTweet();
        Uri insertUri = mContext.getContentResolver().insert(
                TweetContract.TweetEntry.CONTENT_URI,
                tweetValues
        );

        // Retrieve the created row
        long userId = tweetValues.getAsLong(TweetContract.TweetEntry.COLUMN_USER_ID);
        Cursor cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.buildTweetsByUserIdUri(userId),
                null,
                null,
                null,
                null
        );

        // Validate retrieved data
        assertTrue("Query returned an invalid cursor", cursor.moveToFirst());
        TweetDataTestUtils.validateCurrentRecord("testQuery", cursor, tweetValues);

        long tweetRowId =  ContentUris.parseId(insertUri);

        // Make an update call to the record.
        ContentValues updateValues = new ContentValues(tweetValues);
        updateValues.put(TweetContract.TweetEntry._ID, tweetRowId);
        updateValues.put(TweetContract.TweetEntry.COLUMN_NUM_REPLIES, 100);

        int updateCount = mContext.getContentResolver().update(
                TweetContract.TweetEntry.CONTENT_URI,
                updateValues,
                TweetContract.TweetEntry._ID + " = ? ",
                new String[]{Long.toString(tweetRowId)}
        );

        assertEquals(1, updateCount);
        cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.buildTweetsByUserIdUri(userId),
                null,
                null,
                null,
                null
        );
        assertTrue("Query returned an invalid cursor", cursor.moveToFirst());
        TweetDataTestUtils.validateCurrentRecord("testQuery", cursor, updateValues);
        assertEquals("Error: should have had 100 replies", 100, cursor.getLong(cursor.getColumnIndex(TweetContract.TweetEntry.COLUMN_NUM_REPLIES)));

        // Now check the delete operation:
        int deleteCount = mContext.getContentResolver().delete(
                TweetContract.TweetEntry.CONTENT_URI,
                TweetContract.TweetEntry._ID + " = ? ",
                new String[]{Long.toString(tweetRowId)}
        );

        // Make sure the records are gone.
        assertEquals(1, deleteCount);
        cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.buildTweetsByUserIdUri(userId),
                null,
                null,
                null,
                null
        );
        assertFalse("Query should not return a cursor pointing to a row", cursor.moveToFirst());
    }

    // Make sure the bulk upload logic is valid.
    public void testBulkUpload() {
        final int recordLimit = 50;
        ContentValues[] bulkUploadValues = createBulkContentValues(recordLimit);
        int insertCount = mContext.getContentResolver().bulkInsert(TweetContract.TweetEntry.CONTENT_URI, bulkUploadValues);

        assertEquals("Error: created record count does not match expected count", insertCount, recordLimit);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                TweetContract.TweetEntry.CONTENT_URI,
                null,
                null,
                null,
                TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " DESC"
        );

        assertEquals("Error: fetched record count does not match expected count", cursor.getCount(), recordLimit);

        cursor.moveToFirst();
        for ( int i = 0; i < recordLimit; i++, cursor.moveToNext() ) {
            TweetDataTestUtils.validateCurrentRecord("Bulk Upload validation failure for record " + i,
                    cursor, bulkUploadValues[i]);
        }
        cursor.close();
    }

    private ContentValues[] createBulkContentValues(int itemsToCreate) {
        ContentValues[] result = new ContentValues[itemsToCreate];

        for (int i = 0; i < itemsToCreate; i++) {
            ContentValues addObj = TweetDataTestUtils.createTweet();
            addObj.put(TweetContract.TweetEntry.COLUMN_NUM_REPLIES, i);
            addObj.put(TweetContract.TweetEntry.COLUMN_NUM_RETWEETS, i);
            addObj.put(TweetContract.TweetEntry.COLUMN_NUM_LIKES, i);
            result[i] = addObj;
        }

        return result;
    }
}
