package com.assignment.mike.hermit;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
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
    private ProgressDialog mFetchNewTweetDialog;

    private long lastFetchTime = 0;
    private boolean fetchingNewTweetsFromServer;
    private int mLastRecordedFirstVisibleItem = -1;
    private int mLastListViewTop;
    private boolean isScrollingUp;

    public TwitterWallActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTweetAdapter = new TweetAdapter(this, null, 0);

        setContentView(R.layout.fragment_main);
        final ListView listView = (ListView) findViewById(R.id.listview_tweet);
        listView.setAdapter(mTweetAdapter);
        // add a scroll listener to detect reaching the top or bottom of the view
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (listView.getFirstVisiblePosition() == 0 && isScrollingUp) {
                    fetchNewTweets();
                    mLastRecordedFirstVisibleItem = -1;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                View childView = view.getChildAt(0);
                int top = (childView == null) ? 0 : childView.getTop();

                if (firstVisibleItem == mLastRecordedFirstVisibleItem) {
                    if (top > mLastListViewTop) {
                        isScrollingUp = true;
                    } else if (top < mLastListViewTop) {
                        isScrollingUp = false;
                    }
                } else {
                    if (firstVisibleItem < mLastRecordedFirstVisibleItem) {
                        isScrollingUp = true;
                    } else {
                        isScrollingUp = false;
                    }
                }

                mLastListViewTop = top;
                mLastRecordedFirstVisibleItem = firstVisibleItem;

                if ((visibleItemCount == (totalItemCount - firstVisibleItem)) && !isScrollingUp) {
                    Log.d(LOG_TAG, "BOTTOM OF LIST FROM ON SCROLL");
                }
            }
        });

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
            case R.id.refresh:
//                Log.d(LOG_TAG, "Refresh menu option selected");
//                fetchNewTweets();
                return true;
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

        fetchingNewTweetsFromServer = false;
        fetchNewTweets();
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
        clearLoadingDialog();
        mTweetAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clearLoadingDialog();
        mTweetAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        // Do nothing...prevents user from accidentally going back to login screen.
    }

    private void updateLoader() {
        clearLoadingDialog();

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

    private void fetchNewTweets() {
        if (!fetchingNewTweetsFromServer) {
            createLoadingDialog("Getting new tweets...");

            FetchTweetTask fetchTweetTask = new FetchTweetTask();
            fetchTweetTask.execute();
        } else {
            Log.d(LOG_TAG, "Already fetching new tweet data.");
        }

    }

    private void deleteTweets() {
        int deletedRecords = getContentResolver().delete(TweetContract.TweetEntry.CONTENT_URI, null, null);
        if (deletedRecords > 0) {
            updateLoader();
        }
        Log.d(LOG_TAG, "Deleted this many tweets: " + deletedRecords);
    }

    private void createLoadingDialog(String message) {
        mFetchNewTweetDialog = new ProgressDialog(this);
        mFetchNewTweetDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mFetchNewTweetDialog.setMessage(message);
        mFetchNewTweetDialog.setIndeterminate(true);
        mFetchNewTweetDialog.setCanceledOnTouchOutside(false);
        mFetchNewTweetDialog.show();
    }

    private void clearLoadingDialog() {
        if (mFetchNewTweetDialog != null) {
            mFetchNewTweetDialog.cancel();
            mFetchNewTweetDialog.hide();
            mFetchNewTweetDialog = null;
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

            fetchingNewTweetsFromServer = true;

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

            fetchingNewTweetsFromServer = false;

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
