package com.codepath.apps.mycptwitter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.models.Tweet;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class TweetDetailActivity extends AppCompatActivity {

    Tweet tweet;

    ImageView ivProfilePic;
    ImageView ivMediaPic;
    TextView tvName;
    TextView tvUserName;
    TextView tvTweetBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

//        tweet = getIntent().getStringExtra("username");
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        setupViews();

    }

    private void setupViews(){
        ivProfilePic = (ImageView) findViewById(R.id.ivProfileImageDetail);
        tvName = (TextView) findViewById(R.id.tvNameDetail);
        tvUserName = (TextView) findViewById(R.id.tvUserNameDetail);
        tvTweetBody = (TextView) findViewById(R.id.tvTweetBodyDetail);
        ivMediaPic = (ImageView) findViewById(R.id.ivMediaImageDetail);

        tvName.setText(tweet.getUser().getName());
        tvUserName.setText(tweet.getUser().getScreenName());
        tvTweetBody.setText(tweet.getBody());

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfilePic);
        Picasso.with(getContext()).load(tweet.getMediaImageUrl()).into(ivMediaPic);

    }
}
