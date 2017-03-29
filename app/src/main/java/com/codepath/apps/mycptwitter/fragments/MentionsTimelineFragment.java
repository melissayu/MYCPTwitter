package com.codepath.apps.mycptwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mycptwitter.TwitterApplication;
import com.codepath.apps.mycptwitter.TwitterClient;
import com.codepath.apps.mycptwitter.models.Tweet;
import com.codepath.apps.mycptwitter.models.Tweet_Table;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by melissa on 3/27/17.
 */

public class MentionsTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    long maxId;
    boolean firstLoad;
    boolean refreshAll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient(); //singleton client

        firstLoad = false;
        refreshAll = true;
        populateTimeline();

    }

    //send api request and fill listview by creating tweet objects from json
    private void populateTimeline(){
        //if first time loading, populate list from db
        if (firstLoad) {
            List<Tweet> dbTweets = SQLite.select()
                    .from(Tweet.class)
                    .orderBy(Tweet_Table.uid, false)
                    .queryList();
            if (dbTweets.size() > 0) {
                addAll(dbTweets);
                firstLoad = false;
                refreshAll = false;
                maxId = dbTweets.get(dbTweets.size()-1).getUid()-1;
                return;
            }
        }

        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
//                super.onSuccess(statusCode, headers, response);

                if (refreshAll) {
                    getAdapter().clear();
                }

                int responseLength = response.length() - 1;
                try {
                    JSONObject lastObj = response.getJSONObject(responseLength);
                    maxId = lastObj.getLong("id") - 1;
                    refreshAll = false;
                    firstLoad = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //deserialize json
                //create models
                //load model data into listview
                ArrayList<Tweet> fetchedTweets = Tweet.fromJSONArray(response);
                addAll(fetchedTweets);

                //Persist tweets in db
                //persistTweets(fetchedTweets);

                //swipeContainer.setRefreshing(false);

            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

}
