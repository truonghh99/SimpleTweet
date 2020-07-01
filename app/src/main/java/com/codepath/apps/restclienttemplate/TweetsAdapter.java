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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRetweet = itemView.findViewById(R.id.tvRetweet);
            tvFavorite = itemView.findViewById(R.id.tvFavorite);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvTime.setText(getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            ivMedia.setImageDrawable (null);
            if (tweet.mediaUrl != null) {
                Glide.with(context).load(tweet.mediaUrl).into(ivMedia);
                ivMedia.setVisibility(View.VISIBLE);
            }

            tvBody.setTextColor(Color.BLACK);
            tvName.setTextColor(Color.BLACK);

            tvRetweet.setText(String.valueOf(tweet.retweetCount));
            tvFavorite.setText(String.valueOf(tweet.favoriteCount));

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
