package com.codepath.apps.mycptwitter.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.TwitterApplication;
import com.codepath.apps.mycptwitter.TwitterClient;
import com.codepath.apps.mycptwitter.fragments.ProfileHeaderFragment;
import com.codepath.apps.mycptwitter.fragments.UserTimelineFragment;
import com.codepath.apps.mycptwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {
    private TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        client = TwitterApplication.getRestClient(); //singleton client
        client.getUserInfo(new JsonHttpResponseHandler(){
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

        String screenName = getIntent().getStringExtra("screen_name");


        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            ProfileHeaderFragment profileHeaderFragment = ProfileHeaderFragment.newInstance(user);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.replace(R.id.flHeaderContainer, profileHeaderFragment, "PROFILE_HEADER");
            ft.commit();
        }
    }

}
