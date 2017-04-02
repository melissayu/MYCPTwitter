package com.codepath.apps.mycptwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.mycptwitter.R;
import com.codepath.apps.mycptwitter.TwitterApplication;
import com.codepath.apps.mycptwitter.TwitterClient;
import com.codepath.apps.mycptwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by melissa on 4/1/17.
 */

public class ComposeDialogFragment extends DialogFragment{
    EditText etTweetBody;
    private TwitterClient client;

    public interface ComposeDialogListener {
        void onFinishCompose(Tweet newTweet);
    }

    public ComposeDialogFragment() {

    }

    public static ComposeDialogFragment newInstance(String replyTo) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString("atReply", replyTo);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client = TwitterApplication.getRestClient(); //singleton client

        return inflater.inflate(R.layout.dialog_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etTweetBody = (EditText) view.findViewById(R.id.etTweetBodyDialog);
        Button btnTweet = (Button) view.findViewById(R.id.btnSaveDialog);
        final TextView tvCharCount = (TextView) view.findViewById(R.id.tvCharCountDialog);
        TextView tvTitleDialog = (TextView) view.findViewById(R.id.tvTitleDialog);

        tvTitleDialog.setText("Compose New Tweet");

        // Fetch arguments from bundle and set title
        String atReply = getArguments().getString("atReply", "");
        if (!atReply.equals("")) {
            tvTitleDialog.setText("Reply to Tweet");
            etTweetBody.setText(atReply + " ");
        }

        tvCharCount.setText(Integer.toString(140-etTweetBody.getText().length()));
        etTweetBody.setSelection(etTweetBody.getText().length());
        etTweetBody.addTextChangedListener(new TextWatcher() {
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


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //before

//        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        etTweetBody.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetBody = etTweetBody.getText().toString();
                postNewTweet(tweetBody);
            }
        });

    }

    private void postNewTweet(String tweetBody) {
//        getDialog().dismiss();
        postTweet(tweetBody);
    }

    private void postTweet(String tweetBody){
        client.postTweet(tweetBody, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("DEBUG", response.toString());

                Tweet newTweet = Tweet.fromJSON(response);

                //add newTweet to list
//                getAdapter().insert(newTweet,0);
//                getAdapter().notifyDataSetChanged();

                //persist new tweet
                newTweet.save();
//                HomeTimelineFragment.super.lvTweets.setSelectionAfterHeaderView();

                // Return input text back to activity through the implemented listener
                ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                listener.onFinishCompose(newTweet);
                // Close the dialog and return back to the parent activity
                dismiss();
//                return true;

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

}
