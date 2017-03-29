package com.codepath.apps.mycptwitter.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.mycptwitter.EndlessScrollListener;
import com.codepath.apps.mycptwitter.R;
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
    public void goToCompose(){
        dialog = new Dialog(getActivity());

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
    }

    private void postNewTweet(String tweetBody) {
        dialog.dismiss();
        postTweet(tweetBody);
    }

    private void postTweet(String tweetBody){
        client.postTweet(tweetBody, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("DEBUG", response.toString());

                Tweet newTweet = Tweet.fromJSON(response);

                //add newTweet to list
                getAdapter().insert(newTweet,0);
                getAdapter().notifyDataSetChanged();

                //persist new tweet
                newTweet.save();
                HomeTimelineFragment.super.lvTweets.setSelectionAfterHeaderView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
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


}
