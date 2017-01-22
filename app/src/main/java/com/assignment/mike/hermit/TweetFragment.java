package com.assignment.mike.hermit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Mike on 1/21/17.
 */

public class TweetFragment extends Fragment {

    private static final String LOG_TAG = TweetFragment.class.getSimpleName();

    private ArrayAdapter<String> mTweetAdapter;
    public TweetFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tweet_fragment, menu);
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
            default:
                Log.d(LOG_TAG, "Uknown menu item selected.");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        List<String> tweetData = new ArrayList<String>(Arrays.asList(data));
        mTweetAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_tweet,
                R.id.list_item_tweet_textview,
                new ArrayList<String>()
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tweet);
        listView.setAdapter(mTweetAdapter);

        // Now simulate the HTTP fetch of data:
        String httpResult = "All the http data";

        return rootView;
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
