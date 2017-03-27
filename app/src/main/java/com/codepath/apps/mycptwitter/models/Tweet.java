package com.codepath.apps.mycptwitter.models;

import android.net.ParseException;
import android.text.format.DateUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by melissa on 3/21/17.
 *
 *
 */

//parse the json and store the data,
//state logic or display logic
@Parcel(analyze={Tweet.class})
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {

    @Column
    String body;

    @Column
    @PrimaryKey
    long uid; //Id of the tweet

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    User user;

    @Column
    String createdAt;

    @Column
    String relativeTimestamp;

    String mediaImageUrl;

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getRelativeTimestamp() {
        return relativeTimestamp;
    }

    public String getMediaImageUrl() { return mediaImageUrl; }

    // empty constructor needed by the Parceler library
    public Tweet() {
    }

    //Deserialize the JSON
    //Tweet.fromJSON("{...}") ==> Tweet
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();

        //Extract values from JSON & store
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.relativeTimestamp = getRelativeTimeAgo(tweet.createdAt);

            JSONObject entities = jsonObject.getJSONObject("entities");
            if (entities.has("media")) {
                JSONArray media = entities.getJSONArray("media");
                if (media != null) {
                    for (int i = 0; i < media.length(); i++) {
                        //get photo url
                        String mediaType = media.getJSONObject(i).getString("type");
                        if (mediaType.equals("photo")) {
                            tweet.mediaImageUrl = media.getJSONObject(i).getString("media_url");
                        } else if (mediaType.equals("video")) {
                            tweet.mediaImageUrl = media.getJSONObject(i).getString("media_url");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return tweet;
    }


    public static ArrayList<Tweet> fromJSONArray(JSONArray response) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for(int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                Tweet tweet = fromJSON(jsonObject);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }

        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) throws java.text.ParseException {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}


/*
[
        {
        "coordinates": null,
        "truncated": false,
        "created_at": "Tue Aug 28 21:16:23 +0000 2012",
        "favorited": false,
        "id_str": "240558470661799936",
        "in_reply_to_user_id_str": null,
        "entities": {
        "urls": [

        ],
        "hashtags": [

        ],
        "user_mentions": [

        ]
        },
        "text": "just another test",
        "contributors": null,
        "id": 240558470661799936,
        "retweet_count": 0,
        "in_reply_to_status_id_str": null,
        "geo": null,
        "retweeted": false,
        "in_reply_to_user_id": null,
        "place": null,
        "source": "OAuth Dancer Reborn",
        "user": {
            "name": "OAuth Dancer",
            "profile_sidebar_fill_color": "DDEEF6",
            "profile_background_tile": true,
            "profile_sidebar_border_color": "C0DEED",
            "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
            "created_at": "Wed Mar 03 19:37:35 +0000 2010",
            "location": "San Francisco, CA",
            "follow_request_sent": false,
            "id_str": "119476949",
            "is_translator": false,
            "profile_link_color": "0084B4",
            "entities": {
                "url": {
                    "urls": [
                        {
                            "expanded_url": null,
                            "url": "http://bit.ly/oauth-dancer",
                            "indices": [
                                0,
                                26
                            ],
                            "display_url": null
                        }
                    ]
                },
                "description": null
            },
            "default_profile": false,
            "url": "http://bit.ly/oauth-dancer",
            "contributors_enabled": false,
            "favourites_count": 7,
            "utc_offset": null,
            "profile_image_url_https": "https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
            "id": 119476949,
            "listed_count": 1,
            "profile_use_background_image": true,
            "profile_text_color": "333333",
            "followers_count": 28,
            "lang": "en",
            "protected": false,
            "geo_enabled": true,
            "notifications": false,
            "description": "",
            "profile_background_color": "C0DEED",
            "verified": false,
            "time_zone": null,
            "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/80151733/oauth-dance.png",
            "statuses_count": 166,
            "profile_background_image_url": "http://a0.twimg.com/profile_background_images/80151733/oauth-dance.png",
            "default_profile_image": false,
            "friends_count": 14,
            "following": false,
            "show_all_inline_media": false,
            "screen_name": "oauth_dancer"
        },
        "in_reply_to_screen_name": null,
        "in_reply_to_status_id": null
        },
    { ... }
    ]

*/