package com.assignment.mike.hermit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Mike on 1/21/17.
 */

public class TwitterWallActivity
        extends AppCompatActivity {

    private static final String LOG_TAG = TwitterWallActivity.class.getSimpleName();

    private ArrayAdapter<String> mTweetAdapter;
    public TwitterWallActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTweetAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item_tweet,
                R.id.list_item_tweet_textview,
                new ArrayList<String>()
        );

        setContentView(R.layout.fragment_main);
        ListView listView = (ListView) findViewById(R.id.listview_tweet);
        listView.setAdapter(mTweetAdapter);
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
                Log.d(LOG_TAG, "Refresh menu option selected");
                FetchTweetTask fetchTweetTask = new FetchTweetTask();
                fetchTweetTask.execute();
                return true;
            case R.id.compose:
                Log.d(LOG_TAG, "Compose menu option selected");
                return true;
            case R.id.logout:
                Log.d(LOG_TAG, "Logout menu option selected");
                return true;
            default:
                Log.d(LOG_TAG, "Uknown menu item selected.");
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchTweetTask extends AsyncTask<Void, Void, String[]> {
        private final String LOG_TAG = FetchTweetTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {
            Log.d(LOG_TAG, "Fetching the tweet data from HTTP call.");

            String[] data = {
                    "Mon 6/23 - Sunny - 31/17",
                    "Tue 6/24 - Foggy - 21/8",
                    "Wed 6/25 - Cloudy - 22/17",
                    "Thurs 6/26 - Rainy - 18/11",
                    "Fri 6/27 - Foggy - 21/10",
                    "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                    "Sun 6/29 - Sunny - 20/7",
                    "A lot more",
                    "Stuff to come",
                    "in this list",
                    "soon"
            };

            return data;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mTweetAdapter.clear();
                for (String tweet : result) {
                    mTweetAdapter.add(tweet);
                }
            }
        }
    }


}