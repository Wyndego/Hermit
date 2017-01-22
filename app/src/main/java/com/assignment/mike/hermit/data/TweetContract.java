package com.assignment.mike.hermit.data;

/**
 * Created by Mike on 1/21/17.
 */

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the interface into the Tweet data model for
 * the Hermit app.
 */
public class TweetContract {

    // Variables used to identify the base level Content provider
    public static final String CONTENT_AUTHORITY = "com.assignment.mike.hermit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Available branches from the root content provider...for now just need Tweet path
    public static final String PATH_TWEET = "tweet";

    public static final class TweetEntry implements BaseColumns {
        // Variables to provide Content Provider access to this Table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TWEET).build();

        public static final String TABLE_NAME = "tweet";

        // Stores an address to an image that represents the user.
        public static final String COLUMN_USER_ICON = "user_icon";
        // Stores the human readable display name of the tweet's owner.
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        // Stores the unique identifier of the tweet's owner.
        public static final String COLUMN_HANDLE = "handle";
        // Stores the epoch timestamp when the tweet was posted.
        public static final String COLUMN_POST_TIMESTAMP = "post_timestamp";
        // Stores the content of the tweet.
        public static final String COLUMN_CONTENT = "content";
        // Stores the count of retweets for this tweet.
        public static final String COLUMN_NUM_RETWEETS = "num_retweets";
        // Stores the count of likes for this tweet.
        public static final String COLUMN_NUM_LIKES = "num_likes";
        // Stores the count of replies for this tweet.
        public static final String COLUMN_NUM_REPLIES = "num_replies";
    }
}
