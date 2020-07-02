package com.codepath.apps.restclienttemplate;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    Tweet tweet;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivReply = (ImageView) findViewById(R.id.ivReply);
        ivRetweet = (ImageView) findViewById(R.id.ivRetweet);
        ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvName = (TextView) findViewById(R.id.tvName);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvRetweet = (TextView) findViewById(R.id.tvRetweet);
        tvFavorite = (TextView) findViewById(R.id.tvFavorite);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.screenName);
        tvName.setText(tweet.user.name);
        tvTime.setText(tweet.createdAt);
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
        ivMedia.setImageDrawable (null);
        if (tweet.mediaUrl != null) {
            Glide.with(this).load(tweet.mediaUrl).into(ivMedia);
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
    }
}
