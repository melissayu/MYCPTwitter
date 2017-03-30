package com.codepath.apps.mycptwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.models.User;

import org.parceler.Parcels;

/**
 * Created by melissa on 3/28/17.
 */

public class ProfileHeaderFragment extends Fragment {

    TextView tvNameProfile;
    TextView tvTaglineProfile;
    TextView tvFollowersCount;
    TextView tvFollowingCount;
    ImageView ivImageProfile;

    User user;

    public static ProfileHeaderFragment newInstance(User user) {
        ProfileHeaderFragment profileHeaderFragment = new ProfileHeaderFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        profileHeaderFragment.setArguments(args);
        return profileHeaderFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = (User) Parcels.unwrap(getArguments().getParcelable("user"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_profile_header, parent, false);

        tvNameProfile = (TextView) v.findViewById(R.id.tvNameProfile);
        tvTaglineProfile = (TextView) v.findViewById(R.id.tvTaglineProfile);
        tvFollowersCount = (TextView) v.findViewById(R.id.tvFollowers);
        tvFollowingCount = (TextView) v.findViewById(R.id.tvFollowing);
        ivImageProfile = (ImageView) v.findViewById(R.id.ivImageProfile);

        if (user != null) {
            populateHeader(user);
        }
//
//        tvNameProfile.setText(user.getName());
//        tvTaglineProfile.setText(user.getTagline());
//        tvFollowersCount.setText(user.getFollowersCount() + " Followers");
//        tvFollowingCount.setText(user.getFollowingCount() + " Following");
//
//        Glide.with(this).load(user.getProfileImageUrl()).fitCenter().into(ivImageProfile);

        return v;

    }

    public void populateHeader(User user) {

//        TextView tvNameProfile = (TextView) getContext().findViewById(R.id.tvNameProfile);
//        TextView tvTaglineProfile = (TextView) getActivity().findViewById(R.id.tvTaglineProfile);
//        TextView tvFollowersCount = (TextView) getActivity().findViewById(R.id.tvFollowers);
//        TextView tvFollowingCount = (TextView) getActivity().findViewById(R.id.tvFollowing);
//        ImageView ivImageProfile = (ImageView) getActivity().findViewById(R.id.ivImageProfile);

        tvNameProfile.setText(user.getName());
        tvTaglineProfile.setText(user.getTagline());
        tvFollowersCount.setText(user.getFollowersCount() + " Followers");
        tvFollowingCount.setText(user.getFollowingCount() + " Following");

        Glide.with(this).load(user.getProfileImageUrl()).fitCenter().into(ivImageProfile);


    }
}
