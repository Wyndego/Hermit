package com.assignment.mike.hermit.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.assignment.mike.hermit.TwitterWallActivity;

/**
 * Created by Mike on 1/22/17.
 */

public class Tweet {
    private long userId;
    private String handle;
    private String displayName;
    private String iconLocation;
    private long postTimestamp;
    private String content;
    private long numLikes;
    private long numReplies;
    private long numRetweets;

    public Tweet(ContentValues content) {
        if (content != null) {
            setUserId((Long)content.get(TweetContract.TweetEntry.COLUMN_USER_ID));
            setHandle((String)content.get(TweetContract.TweetEntry.COLUMN_HANDLE));
            setDisplayName((String)content.get(TweetContract.TweetEntry.COLUMN_DISPLAY_NAME));
            setIconLocation((String)content.get(TweetContract.TweetEntry.COLUMN_USER_ICON));
            setPostTimestamp((Long)content.get(TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP));
            setContent((String)content.get(TweetContract.TweetEntry.COLUMN_CONTENT));
            setNumLikes((Long)content.get(TweetContract.TweetEntry.COLUMN_NUM_LIKES));
            setNumReplies((Long)content.get(TweetContract.TweetEntry.COLUMN_NUM_REPLIES));
            setNumRetweets((Long)content.get(TweetContract.TweetEntry.COLUMN_NUM_RETWEETS));
        }

    }

    public Tweet (Cursor cursor) {
        if (cursor != null) {
            setUserId(cursor.getLong(TwitterWallActivity.COL_TWEET_USER_ID));
            setHandle(cursor.getString(TwitterWallActivity.COL_TWEET_HANDLE));
            setDisplayName(cursor.getString(TwitterWallActivity.COL_TWEET_DISPLAY_NAME));
            setIconLocation(cursor.getString(TwitterWallActivity.COL_TWEET_USER_ICON));
            setPostTimestamp(cursor.getLong(TwitterWallActivity.COL_TWEET_POST_TIMESTAMP));
            setContent(cursor.getString(TwitterWallActivity.COL_TWEET_CONTENT));
            setNumLikes(cursor.getLong(TwitterWallActivity.COL_TWEET_NUM_LIKES));
            setNumReplies(cursor.getLong(TwitterWallActivity.COL_TWEET_NUM_REPLIES));
            setNumRetweets(cursor.getLong(TwitterWallActivity.COL_TWEET_NUM_RETWEETS));
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getIconLocation() {
        return iconLocation;
    }

    public void setIconLocation(String iconLocation) {
        this.iconLocation = iconLocation;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public long getNumReplies() {
        return numReplies;
    }

    public void setNumReplies(long numReplies) {
        this.numReplies = numReplies;
    }

    public long getNumRetweets() {
        return numRetweets;
    }

    public void setNumRetweets(long numRetweets) {
        this.numRetweets = numRetweets;
    }

    public long getPostTimestamp() {
        return postTimestamp;
    }

    public void setPostTimestamp(long postTimestamp) {
        this.postTimestamp = postTimestamp;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (userId != tweet.userId) return false;
        if (postTimestamp != tweet.postTimestamp) return false;
        if (numLikes != tweet.numLikes) return false;
        if (numReplies != tweet.numReplies) return false;
        if (numRetweets != tweet.numRetweets) return false;
        if (handle != null ? !handle.equals(tweet.handle) : tweet.handle != null) return false;
        if (displayName != null ? !displayName.equals(tweet.displayName) : tweet.displayName != null)
            return false;
        if (iconLocation != null ? !iconLocation.equals(tweet.iconLocation) : tweet.iconLocation != null)
            return false;
        return content != null ? content.equals(tweet.content) : tweet.content == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (handle != null ? handle.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (iconLocation != null ? iconLocation.hashCode() : 0);
        result = 31 * result + (int) (postTimestamp ^ (postTimestamp >>> 32));
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (int) (numLikes ^ (numLikes >>> 32));
        result = 31 * result + (int) (numReplies ^ (numReplies >>> 32));
        result = 31 * result + (int) (numRetweets ^ (numRetweets >>> 32));
        return result;
    }
}
