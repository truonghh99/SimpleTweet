package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity= User.class, parentColumns="id", childColumns="userId"))
public class Tweet {

    @PrimaryKey
    @ColumnInfo
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public String mediaUrl;

    @ColumnInfo
    public long userId;

    @ColumnInfo
    public int retweetCount;

    @ColumnInfo
    public int favoriteCount;

    @ColumnInfo
    public boolean retweeted = false;

    @ColumnInfo
    public boolean favorited = false;

    @Ignore
    public User user;

    // empty constructor needed by Parcel library
    public Tweet() {};

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        if (jsonObject.getJSONObject("entities").optJSONArray("media") != null) {
            tweet.mediaUrl = jsonObject.getJSONObject("entities").optJSONArray("media").getJSONObject(0).getString("media_url_https");
            Log.i("Tweet", "URL: " + jsonObject.getJSONObject("entities").optJSONArray("media").getJSONObject(0));
            //Log.i("Tweet", "URL: " + tweet.mediaUrl);
        } else {
            tweet.mediaUrl = null;
        }
        tweet.user = user;
        tweet.userId = user.id;
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
