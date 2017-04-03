package com.codepath.apps.mycptwitter.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.TweetsArrayAdapter;
import com.codepath.apps.mycptwitter.fragments.ComposeDialogFragment;
import com.codepath.apps.mycptwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.mycptwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.mycptwitter.models.Tweet;
import com.codepath.apps.mycptwitter.models.User;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener, TweetsArrayAdapter.OnProfilePhotoClickedListener {
    private final int REQUEST_CODE = 20;
    Dialog dialog;

    private HomeTimelineFragment homeTimelineFragment;
    private MentionsTimelineFragment mentionsTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //Set up viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pagerSlidingTabStrip.setViewPager(viewPager);

        if (savedInstanceState == null) {
//            fragmentTweetsList = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


    }

    public void goToProfile(){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);

    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        // Store instance of the menu item containing progress
//        miActionProgressItem = menu.findItem(R.id.miActionProgress);
//        // Extract the action-view from the menu item
//        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                goToCompose();
                return true;
            case R.id.miProfile:
                goToProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToCompose(){

        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance("");
        composeDialogFragment.show(fm, "fragment_edit_name");

        /*        dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog_compose);
        dialog.setTitle("Compose new Tweet");

        final TextView tvCharCount = (TextView) dialog.findViewById(R.id.tvCharCountDialog);

        final EditText etTweet = (EditText) dialog.findViewById(R.id.etTweetBodyDialog);
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
                String tweetBody = etTweet.getText().toString();
                postNewTweet(tweetBody);
            }
        });

        dialog.show();
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK) {
            // Extract tweet body from result extras
//            String tweetBody = data.getExtras().getString("tweetBody");

            //postTweet(tweetBody); TODO: MOVE THIS OUT OF HERE INTO FRAGMENT

            Tweet newTweet = (Tweet) Parcels.unwrap(data.getExtras().getParcelable("tweet"));
            if (homeTimelineFragment != null) {
                homeTimelineFragment.newTweetComposed(newTweet);
            }

        }
    }

    @Override
    public void onFinishCompose(Tweet newTweet) {
        if (homeTimelineFragment != null) {
            homeTimelineFragment.newTweetComposed(newTweet);
        }
    }

    @Override
    public void onPhotoClicked(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("user", Parcels.wrap(user));
        startActivity(i);

    }

    //return order of the fragments in the viewpager
    public class TweetsPagerAdapter extends FragmentPagerAdapter{
        final int PAGE_COUNT = 2;
        final String[] tabTitles = {"Home", "Mentions"};
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if ( position==0 ) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    homeTimelineFragment = (HomeTimelineFragment) createdFragment;
                    break;
                case 1:
                    mentionsTimelineFragment = (MentionsTimelineFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return super.getPageTitle(position);
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
