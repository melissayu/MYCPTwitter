package com.codepath.apps.mycptwitter.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mycptwitter.EndlessScrollListener;
import com.codepath.apps.mycptwitter.TwitterApplication;
import com.codepath.apps.mycptwitter.TwitterClient;
import com.codepath.apps.mycptwitter.models.Tweet;
import com.codepath.apps.mycptwitter.models.Tweet_Table;
import com.codepath.apps.mycptwitter.models.User;
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

public class HomeTimelineFragment extends TweetsListFragment {

    private TwitterClient client;

    long maxId;
    boolean firstLoad;
    boolean refreshAll;
    Dialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
//        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        client = TwitterApplication.getRestClient(); //singleton client

        super.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //delete all tweets in db
                List<Tweet> dbTweets = SQLite.select()
                        .from(Tweet.class)
                        .queryList();
                for (int i=0; i< dbTweets.size(); i++) {
                    dbTweets.get(i).delete();
                }
                firstLoad = false;
                refreshAll = true;
                populateTimeline();
            }

        });

        super.lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                populateTimeline();
                return true;
            }
        });


        firstLoad = true;
        refreshAll = true;
        populateTimeline();



        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        client = TwitterApplication.getRestClient(); //singleton client
//
//        getTweetsListView().setOnScrollListener(new EndlessScrollListener() {
//            @Override
//            public boolean onLoadMore(int page, int totalItemsCount) {
//                populateTimeline();
//                return true;
//            }
//        });
//
//
//        firstLoad = true;
//        refreshAll = true;
//        populateTimeline();
//


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

        client.getHomeTimeline(refreshAll, maxId, new JsonHttpResponseHandler() {
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
                persistTweets(fetchedTweets);

                swipeContainer.setRefreshing(false);

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


    private void persistTweets(ArrayList<Tweet> fetchedTweets) {
        for (int i = 0; i < fetchedTweets.size(); i++) {
            Tweet t = fetchedTweets.get(i);
            User u = t.getUser();
            u.save();
            t.save();
        }
    }

    public void newTweetComposed(Tweet newTweet) {
        getAdapter().insert(newTweet,0);
        getAdapter().notifyDataSetChanged();
        lvTweets.setSelectionAfterHeaderView();
    }

}
