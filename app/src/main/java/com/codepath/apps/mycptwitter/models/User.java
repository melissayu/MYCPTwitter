package com.codepath.apps.mycptwitter.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by melissa on 3/21/17.
 */
@Parcel(analyze={User.class})
@Table(database = MyDatabase.class)
public class User extends BaseModel {

    @Column
    @PrimaryKey
    public long uid;

    @Column
    String name;

    @Column
    String screenName;

    @Column
    String profileImageUrl;

    @Column
    String tagline;

    @Column
    int followersCount;

    @Column
    int followingCount;

    public String getTagline() {
        return tagline;
    }
    public int getFollowersCount() {
        return followersCount;
    }
    public int getFollowingCount() {
        return followingCount;
    }
    public String getName() {
        return name;
    }
    public String getScreenName() {
        return screenName;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public long getUid() {
        return uid;
    }

    public static User fromJSON(JSONObject jsonObject){
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.screenName = "@"+jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.uid = jsonObject.getLong("id");
            user.tagline = jsonObject.getString("description");
            user.followersCount = jsonObject.getInt("followers_count");
            user.followingCount = jsonObject.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }


}

/*
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

 */
