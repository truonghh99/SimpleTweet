package com.codepath.apps.restclienttemplate;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
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

    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detail);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivProfileImage = (ImageView) binding.ivProfileImage;
        ivMedia = (ImageView) binding.ivMedia;
        ivReply = (ImageView) binding.ivReply;
        ivRetweet = (ImageView) binding.ivRetweet;
        ivFavorite = (ImageView) binding.ivFavorite;
        tvBody = (TextView) binding.tvBody;
        tvScreenName = (TextView) binding.tvScreenName;
        tvName = (TextView) binding.tvName;
        tvTime = (TextView) binding.tvTime;
        tvRetweet = (TextView) binding.tvRetweet;
        tvFavorite = (TextView) binding.tvFavorite;

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

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReplyDialog(tweet.user.screenName);
            }
        });
    }

    private void showReplyDialog(String screenName) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(screenName);
        composeDialogFragment.show(fm, "fragment_compose_dialog");
    }
}
