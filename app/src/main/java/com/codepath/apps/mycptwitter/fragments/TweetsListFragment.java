package com.codepath.apps.mycptwitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.TweetsArrayAdapter;
import com.codepath.apps.mycptwitter.activities.TweetDetailActivity;
import com.codepath.apps.mycptwitter.models.Tweet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melissa on 3/27/17.
 */

public class TweetsListFragment extends Fragment{
    private final int REQUEST_CODE = 20;

    public SwipeRefreshLayout swipeContainer;

    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    public ListView lvTweets;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //delete all tweets in db
//                List<Tweet> dbTweets = SQLite.select()
//                        .from(Tweet.class)
//                        .queryList();
//                for (int i=0; i< dbTweets.size(); i++) {
//                    dbTweets.get(i).delete();
//                }
//                firstLoad = false;
//                refreshAll = true;
//            }
//
//        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        //Find ListView
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet selectedTweet = tweets.get(position);
                Intent i = new Intent(getActivity().getApplicationContext(), TweetDetailActivity.class);
                i.putExtra("tweet", Parcels.wrap(selectedTweet));
                startActivityForResult(i, REQUEST_CODE);

            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }

    public TweetsArrayAdapter getAdapter() {
        return aTweets;
    }
    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

}
