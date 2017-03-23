package com.codepath.apps.mycptwitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mycptwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;

    long maxId;
    boolean firstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //Find ListView
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadNextDataFromApi(page);
                return false;
            }
        });
        client = TwitterApplication.getRestClient(); //singleton client
        firstLoad = true;
        populateTimeline();
    }

    public void loadNextDataFromApi(int offset) {
        populateTimeline();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                goToCompose();
                return true;
//            case R.id.miProfile:
//                showProfileView();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void goToCompose(){
        Intent i = new Intent(this, ComposeActivity.class);
        startActivity(i);
    }

    //send api request and fill listview by creating tweet objects from json
    private void populateTimeline(){
        client.getHomeTimeline(firstLoad, maxId, new JsonHttpResponseHandler(){
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
//                super.onSuccess(statusCode, headers, response);

                int responseLength = response.length()-1;
                try {
                    JSONObject lastObj = response.getJSONObject(responseLength);
                    maxId = lastObj.getLong("id")-1;
                    firstLoad = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //deserialize json
                //create models
                //load model data into listview
                aTweets.addAll(Tweet.fromJSONArray(response));
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
//                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
