package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.renderscript.RenderScript;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;

        client = TwitterApp.getRestClient(context);
    }

    // For each row, inflate the layout for a tweet
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (relativeDate.contains("seconds")) {
            relativeDate = relativeDate.substring(0, relativeDate.length() - 12);
            relativeDate = relativeDate + "s";
        } else if (relativeDate.contains("minutes")) {
            relativeDate = relativeDate.substring(0, relativeDate.length() - 12);
            relativeDate = relativeDate + "m";
        } else if (relativeDate.contains("hours")) {
            relativeDate = relativeDate.substring(0, relativeDate.length() - 10);
            relativeDate = relativeDate + "h";
        } else if (relativeDate.contains("days")) {
            relativeDate = relativeDate.substring(0, relativeDate.length() - 9);
            relativeDate = relativeDate + "d";
        } else if (relativeDate.contains("second")) {
            relativeDate = relativeDate.substring(0, relativeDate.length() - 11);
            relativeDate = relativeDate + "s";
        } else if (relativeDate.contains("minute")) {
            relativeDate = relativeDate.substring(0, relativeDate.length() - 11);
            relativeDate = relativeDate + "m";
        } else if (relativeDate.contains("hour")) {
            relativeDate = relativeDate.substring(0, relativeDate.length() - 9);
            relativeDate = relativeDate + "h";
        } else if (relativeDate.contains("day")) {
            relativeDate = relativeDate.substring(0, relativeDate.length() - 8);
            relativeDate = relativeDate + "d";
        }

        return relativeDate;
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        ImageView ivMedia;
        ImageView ivReply;
        ImageView ivRetweet;
        ImageView ivFavorite;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTime;
        TextView tvRetweet;
        TextView tvFavorite;

        // an itemView represents one row in the RV, or one tweet
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivReply = itemView.findViewById(R.id.ivReply);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRetweet = itemView.findViewById(R.id.tvRetweet);
            tvFavorite = itemView.findViewById(R.id.tvFavorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tweet tweet = tweets.get(getAdapterPosition());

                    Intent intent = new Intent(((TimelineActivity) context), DetailActivity.class);
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    ((TimelineActivity) context).startActivityForResult(intent, 40);
                }
            });
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvTime.setText(getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
            ivMedia.setImageDrawable (null);
            if (tweet.mediaUrl != null) {
                Glide.with(context).load(tweet.mediaUrl).into(ivMedia);
                ivMedia.setVisibility(View.VISIBLE);
            }

            tvBody.setTextColor(Color.BLACK);
            tvName.setTextColor(Color.BLACK);

            tvRetweet.setText(String.valueOf(tweet.retweetCount));
            tvFavorite.setText(String.valueOf(tweet.favoriteCount));

            if (tweet.retweeted) {
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                ivRetweet.setTag(R.drawable.ic_vector_retweet);
            } else {
                ivRetweet.setTag(R.drawable.ic_vector_retweet_stroke);
            }

            if (tweet.favorited) {
                ivFavorite.setImageResource(R.drawable.ic_vector_heart);
                ivFavorite.setTag(R.drawable.ic_vector_heart);
            } else {
                ivFavorite.setTag(R.drawable.ic_vector_heart_stroke);
            }


            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tweet tweet = tweets.get(getAdapterPosition());

                    if ((int) ivRetweet.getTag() == R.drawable.ic_vector_retweet_stroke) {
                        // tweet has not been retweeted yet, so this click must handle a retweet
                        client.retweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {

                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                        ivRetweet.setTag(R.drawable.ic_vector_retweet);
                        tvRetweet.setText(String.valueOf(tweet.retweetCount + 1));
                    } else {
                        // tweet has been retweeted, so we have to undo the retweet
                        client.unretweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {

                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                        ivRetweet.setTag(R.drawable.ic_vector_retweet_stroke);
                        tvRetweet.setText(String.valueOf(tweet.retweetCount - 1));
                    }
                }
            });

            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tweet tweet = tweets.get(getAdapterPosition());

                    if ((int) ivFavorite.getTag() == R.drawable.ic_vector_heart_stroke) {
                        // tweet has not been liked yet, so this click must handle a like
                        client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {

                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        ivFavorite.setImageResource(R.drawable.ic_vector_heart);
                        ivFavorite.setTag(R.drawable.ic_vector_heart);
                        tvFavorite.setText(String.valueOf(tweet.favoriteCount + 1));
                    } else {
                        // tweet has been liked, so we have to unlike the tweet
                        client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {

                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
                        ivFavorite.setTag(R.drawable.ic_vector_heart_stroke);
                        tvFavorite.setText(String.valueOf(tweet.favoriteCount - 1));
                    }
                }
            });
//            ivReply.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, ComposeActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                    intent.putExtra("video_id", Parcels.wrap(videoId));
//                    context.startActivity(intent);
//                }
//            });
        }
    }
}
