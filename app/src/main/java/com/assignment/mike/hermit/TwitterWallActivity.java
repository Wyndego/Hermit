package com.assignment.mike.hermit;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.assignment.mike.hermit.data.TweetContract;
import com.assignment.mike.hermit.data.TweetDataUtility;

import java.util.Random;

/**
 * Created by Mike on 1/21/17.
 */

public class TwitterWallActivity
        extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = TwitterWallActivity.class.getSimpleName();

    private static final int TWEET_LOADER = 0;
    private static final String[] TWEET_COLUMNS = {
            TweetContract.TweetEntry._ID,
            TweetContract.TweetEntry.COLUMN_USER_ID,
            TweetContract.TweetEntry.COLUMN_HANDLE,
            TweetContract.TweetEntry.COLUMN_DISPLAY_NAME,
            TweetContract.TweetEntry.COLUMN_USER_ICON,
            TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP,
            TweetContract.TweetEntry.COLUMN_CONTENT,
            TweetContract.TweetEntry.COLUMN_NUM_REPLIES,
            TweetContract.TweetEntry.COLUMN_NUM_RETWEETS,
            TweetContract.TweetEntry.COLUMN_NUM_LIKES

    };

    // These indices are tied to TWEET_COLUMNS.  If TWEET_COLUMNS changes, these
    // must change.
    public static final int COL_TWEET_ID = 0;
    public static final int COL_TWEET_USER_ID = 1;
    public static final int COL_TWEET_HANDLE = 2;
    public static final int COL_TWEET_DISPLAY_NAME = 3;
    public static final int COL_TWEET_USER_ICON = 4;
    public static final int COL_TWEET_POST_TIMESTAMP = 5;
    public static final int COL_TWEET_CONTENT = 6;
    public static final int COL_TWEET_NUM_REPLIES = 7;
    public static final int COL_TWEET_NUM_RETWEETS = 8;
    public static final int COL_TWEET_NUM_LIKES = 9;


    private TweetAdapter mTweetAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CursorLoader mCursorLoader;

    private FetchTweetTask mFetchTweetTask;

    private long lastFetchTime = 0;

    public TwitterWallActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        mTweetAdapter = new TweetAdapter(this, null, 0);

        // Setup the swipe refresh controls.
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNewTweets();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.colorPrimary,
                R.color.grey);


        final ListView listView = (ListView) findViewById(R.id.listview_tweet);
        listView.setAdapter(mTweetAdapter);

        getSupportLoaderManager().initLoader(TWEET_LOADER, null, this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.twitter_wall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int selectedMenuItem = item.getItemId();
        switch (selectedMenuItem) {
//            case R.id.refresh:
//                Log.d(LOG_TAG, "Refresh menu option selected");
//                fetchNewTweets();
//                return true;
            case R.id.compose:
                Log.d(LOG_TAG, "Compose menu option selected");
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                Log.d(LOG_TAG, "Uknown menu item selected.");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        fetchNewTweets();
    }

    @Override
    protected void onResume() {
        updateLoader();
        super.onResume();
    }

    @Override
    protected void onPause() {
        updateLoader();
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Get the last set of data, stored in the db.
        long sinceLastWeek = System.currentTimeMillis() - TweetDataUtility.WEEK_IN_MS;
        String sortAndLimit = TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " DESC LIMIT 50";

        return new CursorLoader(
                this,
                TweetContract.TweetEntry.CONTENT_URI,
                TWEET_COLUMNS,
                TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " > ?",
                new String[]{Long.toString(sinceLastWeek)},
                sortAndLimit
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSwipeRefreshLayout.setRefreshing(false);
        mTweetAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSwipeRefreshLayout.setRefreshing(false);
        mTweetAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        // Do nothing...prevents user from accidentally going back to login screen.
    }

    private void updateLoader() {
        mSwipeRefreshLayout.setRefreshing(false);
        killFetchTweetTask();

        long sinceLastWeek = System.currentTimeMillis() - TweetDataUtility.WEEK_IN_MS;
        String sortAndLimit = TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " DESC LIMIT 50";

        Cursor cursor = getContentResolver().query(
                TweetContract.TweetEntry.CONTENT_URI,
                TWEET_COLUMNS,
                TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " > ?",
                new String[]{Long.toString(sinceLastWeek)},
                sortAndLimit
        );
        mTweetAdapter.changeCursor(cursor);
    }

    private void killFetchTweetTask() {
        if (mFetchTweetTask != null) {
            mFetchTweetTask.cancel(true);
            mFetchTweetTask = null;
        }
    }

    private void fetchNewTweets() {
        if (mFetchTweetTask == null) {
            FetchTweetTask fetchTweetTask = new FetchTweetTask();
            fetchTweetTask.execute();
            mFetchTweetTask = fetchTweetTask;
        }
    }

    private void logout() {
        // Perform any cleanup tasks here...deleting records, etc.

        // Finally navigate to parent of this app to logout.
        NavUtils.navigateUpFromSameTask(this);
    }

    public class FetchTweetTask extends AsyncTask<Void, Void, String[]> {
        private final String LOG_TAG = FetchTweetTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {
            Log.d(LOG_TAG, "Fetching the tweet data from HTTP call.");

            String[] result = null;

            Random rand = new Random();
            try {
                Thread.sleep(rand.nextInt(5000));
            } catch (Exception e) {
                Log.d(LOG_TAG, "Something interrupted the network lag simulation.");
            }
            ContentValues[] data = TweetDataUtility.generateRandomTweets(lastFetchTime);
            lastFetchTime = System.currentTimeMillis();

            // Now that we have the data, inert it to the Tweet DB.
            getContentResolver().bulkInsert(
                    TweetContract.TweetEntry.CONTENT_URI, data
            );

            // Also perform cleanup on old tweets we no longer care about
            int deletedRecords = getContentResolver().delete(
                    TweetContract.TweetEntry.CONTENT_URI,
                    TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP + " < ?",
                    new String[]{Long.toString(System.currentTimeMillis() - TweetDataUtility.WEEK_IN_MS)}
            );

            result = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = TweetDataUtility.convertTweetConentValueToTweet(data[i]).toString();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                // Update the loader with new cursor data for fetched tweets
                updateLoader();
            }
        }
    }


}
