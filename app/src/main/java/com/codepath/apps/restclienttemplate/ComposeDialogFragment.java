package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;


public class ComposeDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "ComposeDialogFragment";
    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;
    ImageView ivClose;
    TwitterClient client;

    Tweet result;

    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface ComposeDialogListener {
        void onFinishComposeDialog(Tweet tweet);
    }

    public static ComposeDialogFragment newInstance(String replyTo) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString("in_reply_to", replyTo);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etCompose = (EditText) view.findViewById(R.id.etCompose);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        ivClose = (ImageView) view.findViewById(R.id.ivClose);
        client = TwitterApp.getRestClient(getContext());
        // Fetch arguments from bundle and set string
        String replyTo = getArguments().getString("in_reply_to");
        if (!replyTo.equals("")) {
            etCompose.setHint("Replying to " + replyTo);
            etCompose.setText(replyTo + " ");
            btnTweet.setText("Reply");
        }

        // Show soft keyboard automatically and request focus to field
        etCompose.requestFocus();

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(getContext(), "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(getContext(), "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getContext(), tweetContent, Toast.LENGTH_LONG).show();
                // make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            result = Tweet.fromJson(json.jsonObject);
                            result.favorited = false;
                            result.retweeted = false;

                            Log.i(TAG, "Published tweet says: " + result.body);
                            ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                            listener.onFinishComposeDialog(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnTweet.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == i) {
            // Return input text back to activity through the implemented listener
            ComposeDialogListener listener = (ComposeDialogListener) getActivity();
            listener.onFinishComposeDialog(result);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }
}