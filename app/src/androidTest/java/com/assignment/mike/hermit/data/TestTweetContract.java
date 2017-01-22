package com.assignment.mike.hermit.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Mike on 1/22/17.
 */

public class TestTweetContract extends AndroidTestCase {

    private static final long TEST_USER_ID = 123456L;
    private static final long TEST_TWEET_DATE = 145673489L;

    public void testBuildWeatherLocation() {
        Uri tweetWithUserIdUri = TweetContract.TweetEntry.buildTweetsByUserIdUri(TEST_USER_ID);
        assertNotNull("Error: Null Uri returned.  Make sure TweetContract.TweetEntry.buildTweetsByUserIdUri is defined.",
                tweetWithUserIdUri);
        assertEquals("Error: User Id was not found at the end of the URI",
                TEST_USER_ID, Long.valueOf(tweetWithUserIdUri.getLastPathSegment()).longValue());
        assertEquals("Error: URI with userId does not match expected value",
                tweetWithUserIdUri.toString(),
                "content://com.assignment.mike.hermit/tweet/" + TEST_USER_ID);
        assertEquals("Error: did not retrieve the proper userId from uri using TweetContract.TweetEntry.getUserIdFromUri",
                TEST_USER_ID,
                TweetContract.TweetEntry.getUserIdFromUri(tweetWithUserIdUri));

        Uri tweetWithUserIdAndStartTimeUri = TweetContract.TweetEntry.buildTweetsByUserWithStartTimeUri(TEST_USER_ID, TEST_TWEET_DATE);
        assertNotNull("Error: Null Uri returned.  Make sure TweetContract.TweetEntry.buildTweetsByUserWithStartTimeUri is defined.",
                tweetWithUserIdAndStartTimeUri);
        assertEquals("Error: Tweet Date was not found at the end of the URI",
                TEST_TWEET_DATE, Long.valueOf(tweetWithUserIdAndStartTimeUri.getLastPathSegment()).longValue());
        assertEquals("Error: URI with userId does not match expected value",
                tweetWithUserIdAndStartTimeUri.toString(),
                "content://com.assignment.mike.hermit/tweet/" + TEST_USER_ID + "/" + TEST_TWEET_DATE);
        assertEquals("Error: did not retrieve the proper userId from uri using TweetContract.TweetEntry.getStartTimeFromUri",
                TEST_TWEET_DATE,
                TweetContract.TweetEntry.getStartTimeFromUri(tweetWithUserIdAndStartTimeUri));
    }
}
