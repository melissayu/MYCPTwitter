package com.codepath.apps.mycptwitter.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.fragments.ComposeDialogFragment;
import com.codepath.apps.mycptwitter.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class TweetDetailActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener{

    Tweet tweet;

    ImageView ivProfilePic;
    ImageView ivMediaPic;
    TextView tvName;
    TextView tvUserName;
    TextView tvTweetBody;

    Dialog dialog;
    String tweetBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

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

        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 4, 0))
                .into(ivProfilePic);
        Glide.with(getContext()).load(tweet.getMediaImageUrl())
                .into(ivMediaPic);

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("user", Parcels.wrap(tweet.getUser()));
                startActivity(i);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miReply:
                composeReply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void composeReply(){
        FragmentManager fm = getSupportFragmentManager();
        String atReply = tweet.getUser().getScreenName();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(atReply);
        composeDialogFragment.show(fm, "fragment_edit_name");

/*        dialog = new Dialog(TweetDetailActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog_compose);

        final TextView tvCharCount = (TextView) dialog.findViewById(R.id.tvCharCountDialog);
        final TextView tvTitleDialog = (TextView) dialog.findViewById(R.id.tvTitleDialog);
        tvTitleDialog.setText("Reply to Tweet");

        final EditText etTweet = (EditText) dialog.findViewById(R.id.etTweetBodyDialog);
        String atReply = tweet.getUser().getScreenName();
        etTweet.setText(atReply.toString() + " ");
        tvCharCount.setText(Integer.toString(140-etTweet.getText().length()));
        etTweet.setSelection(etTweet.getText().length());
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charCount = 140-s.length();
                tvCharCount.setText(Integer.toString(charCount));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button btnPost = (Button) dialog.findViewById(R.id.btnSaveDialog);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweetBody = etTweet.getText().toString();
                returnTweetBody();
            }
        });

        dialog.show();
*/
    }

    private void returnTweet(Tweet newTweet) {
//        dialog.dismiss();
        Intent data = new Intent();
        // Pass relevant data back as a result
//        data.putExtra("tweetBody", tweetBody); // TODO: send back Tweet object
        data.putExtra("tweet", Parcels.wrap(newTweet));
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }

    @Override
    public void onFinishCompose(Tweet newTweet) {
        returnTweet(newTweet);
    }
}
