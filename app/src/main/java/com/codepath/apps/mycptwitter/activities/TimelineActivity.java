package com.codepath.apps.mycptwitter.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mycptwitter.EndlessScrollListener;
import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.TweetsArrayAdapter;
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
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 20;

    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;

    long maxId;
    boolean firstLoad;
    boolean refreshAll;

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firstLoad = false;
                refreshAll = true;
                populateTimeline();
            }

        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


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

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet selectedTweet = tweets.get(position);
                Intent i = new Intent(getApplicationContext(), TweetDetailActivity.class);
                i.putExtra("tweet", Parcels.wrap(selectedTweet));
                startActivity(i);

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
//        Intent i = new Intent(this, ComposeActivity.class);
//        startActivityForResult(i, REQUEST_CODE);
        final Dialog dialog = new Dialog(TimelineActivity.this);

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
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
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
                postTweet(tweetBody);
            }
        });

        dialog.show();
    }

    private void postTweet(String tweetBody){
        client.postTweet(tweetBody, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);

                Log.d("DEBUG", response.toString());

                Tweet newTweet = Tweet.fromJSON(response);

                //add newTweet to list
                aTweets.insert(newTweet,0);
                aTweets.notifyDataSetChanged();

                //persist new tweet
                newTweet.save();

                finish(); // closes the activity, pass data to parent

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

//                super.onFailure(statusCode, headers, responseString, throwable);
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

    //send api request and fill listview by creating tweet objects from json
    private void populateTimeline(){
        //if first time loading, populate list from db
        if (firstLoad) {
            List<Tweet> dbTweets = SQLite.select()
                    .from(Tweet.class)
                    .orderBy(Tweet_Table.createdAt, false)
                    .queryList();
            aTweets.addAll(dbTweets);
            firstLoad = false;
        }
        else {

            client.getHomeTimeline(refreshAll, maxId, new JsonHttpResponseHandler() {
                //success
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());
//                super.onSuccess(statusCode, headers, response);

                    if (refreshAll) {
                        aTweets.clear();
                    }

                    int responseLength = response.length() - 1;
                    try {
                        JSONObject lastObj = response.getJSONObject(responseLength);
                        maxId = lastObj.getLong("id") - 1;
                        refreshAll = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //deserialize json
                    //create models
                    //load model data into listview
                    ArrayList<Tweet> fetchedTweets = Tweet.fromJSONArray(response);
                    aTweets.addAll(fetchedTweets);

                    //Persist tweets in db
                    persistTweets(fetchedTweets);


                    swipeContainer.setRefreshing(false);

                }

                //failure
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
//                super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
//                super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
    }

    // ActivityOne.java, time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
//            String newTweet = data.getExtras().getString("tweet"); //TODO: get tweet object and add to adapter.
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            aTweets.insert(newTweet,0);
            aTweets.notifyDataSetChanged();

            //persist new tweet
            newTweet.save();

//            int code = data.getExtras().getInt("code", 0);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, newTweet.getBody(), Toast.LENGTH_SHORT).show();
        }
    }
}
