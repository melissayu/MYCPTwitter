package com.codepath.apps.mycptwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.TwitterApplication;
import com.codepath.apps.mycptwitter.TwitterClient;
import com.codepath.apps.mycptwitter.fragments.ComposeDialogFragment;
import com.codepath.apps.mycptwitter.fragments.ProfileHeaderFragment;
import com.codepath.apps.mycptwitter.fragments.UserTimelineFragment;
import com.codepath.apps.mycptwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {
    private TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        String screenName = null;
        if(user != null) {
            screenName = user.getScreenName();
            getSupportActionBar().setTitle(screenName);

        }

        if (user == null){
            client = TwitterApplication.getRestClient(); //singleton client
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
                    user = User.fromJSON(response);
                    getSupportActionBar().setTitle(user.getScreenName());
                    ProfileHeaderFragment profileHeaderFragment = (ProfileHeaderFragment)
                            getSupportFragmentManager().findFragmentByTag("PROFILE_HEADER");
                    profileHeaderFragment.populateHeader(user);
                }
            });
        }

        //        String screenName = getIntent().getStringExtra("screen_name");
        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            ProfileHeaderFragment profileHeaderFragment = ProfileHeaderFragment.newInstance(user);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.replace(R.id.flHeaderContainer, profileHeaderFragment, "PROFILE_HEADER");
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    public void goToCompose() {

        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance("");
        composeDialogFragment.show(fm, "fragment_edit_name");

    }

    public void goToProfile(){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);

    }


}
