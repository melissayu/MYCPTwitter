package com.codepath.apps.mycptwitter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "cFVsDWV808gnmINl71DRg2LOn";       // Change this
	public static final String REST_CONSUMER_SECRET = "uAd5JiUPOF1gV4DR4Umh5wKJUX6xZWDSu6xRsGmnP4NaV7JgYp"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://mycptwitter"; // Change this (here and in manifest)

    public int maxId;

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) TwitterApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    //Home - Timeline: gets timeline
//    GET statuses/home_timeline.json
//        count=25
//        since_id=1
    public void getHomeTimeline(Boolean firstLoad, long maxId, AsyncHttpResponseHandler handler){
        if (isNetworkAvailable()) {
            String apiUrl = getApiUrl("statuses/home_timeline.json");
            RequestParams params = new RequestParams();
            params.put("count", 25);
            params.put("since_id", 1);
            if (!firstLoad) {
                params.put("max_id", maxId);
            }

            //Execute request
            getClient().get(apiUrl, params, handler);
        }
    }

    //Compose Tweet
    public void postTweet(String statusText, AsyncHttpResponseHandler handler) {
        if (isNetworkAvailable()) {
            String apiUrl = getApiUrl("statuses/update.json");
            RequestParams params = new RequestParams();
            params.put("status", statusText);

            getClient().post(apiUrl, params, handler);
        }
    }

    public void getUserTimeline(String screenName, long maxId, AsyncHttpResponseHandler handler) {
        if (isNetworkAvailable()) {
            String apiUrl = getApiUrl("statuses/user_timeline.json");
            RequestParams params = new RequestParams();
            params.put("count", 25);
            params.put("screen_name", screenName);
            if (maxId > 0) {
                params.put("max_id", maxId);
            }
            //Execute request
            getClient().get(apiUrl, params, handler);
        }
    }
    public void getUserInfo(AsyncHttpResponseHandler handler) {
        if (isNetworkAvailable()) {
            String apiUrl = getApiUrl("account/verify_credentials.json");
            //Execute request
            getClient().get(apiUrl, null, handler);
        }
    }


    public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler){
        if (isNetworkAvailable()) {
            String apiUrl = getApiUrl("statuses/mentions_timeline.json");
            RequestParams params = new RequestParams();
            params.put("count", 25);
            if (maxId > 0) {
                params.put("max_id", maxId);
            }
            //Execute request
            getClient().get(apiUrl, params, handler);
        }
    }


	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
