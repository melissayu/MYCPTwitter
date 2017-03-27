package com.codepath.apps.mycptwitter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mycptwitter.models.Tweet;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by melissa on 3/21/17.
 */

public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{


    public TweetsArrayAdapter( Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        Tweet tweet =  getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);

        }

        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserNameCompose);
        LinkifiedTextView tvBody = (LinkifiedTextView) convertView.findViewById(R.id.tvBody);
//        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);

        tvUserName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        ivProfileImage.setImageResource(android.R.color.transparent); //clear out image
        tvTimestamp.setText(tweet.getRelativeTimestamp());

        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 4, 0))
                .into(ivProfileImage);

        return convertView;
    }
}
