package com.example.app.ourapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.wss.WebSocketClient;
import com.example.app.ourapplication.wss.WebSocketListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ROYSH on 8/3/2016.
 */
public class DiscussionActivity extends AppCompatActivity implements WebSocketListener {

    static List<Person> mComments = new ArrayList<>();
    private FeedRVAdapter mCommentListAdapter;
    private WebSocketClient mWebSocketClient;
    private final String TAG = DiscussionActivity.class.getSimpleName();
    private String keyid;
    private DBHelper mDBHelper = new DBHelper(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_discussion);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.discussion_collapse);
        collapsingToolbar.setTitle(" ");

        TextView senderName = (TextView) findViewById(R.id.sender_name);
        TextView senderMessage = (TextView) findViewById(R.id.sender_message);
        ImageView senderPhoto = (ImageView) findViewById(R.id.sender_photo);
        TextView messageTime = (TextView) findViewById(R.id.message_time);
        ImageView messagePhoto = (ImageView) findViewById(R.id.message_photo);
        Button  mSendButton = (Button) findViewById(R.id.send_button);
        final EditText  mMessageBox = (EditText) findViewById(R.id.msg_box);

        mWebSocketClient = OurApp.getClient();
        mWebSocketClient.addWebSocketListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        mCommentListAdapter = new FeedRVAdapter(mComments);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

         keyid = getIntent().getStringExtra(Keys.KEY_ID);
        if (keyid != null) {
            senderName.setText(mDBHelper.getFeedDataColumn(keyid, 1));
            senderMessage.setText(mDBHelper.getFeedDataColumn(keyid, 3));
            messagePhoto.setImageBitmap(Helper.decodeImageString(mDBHelper.getFeedDataColumn(keyid, 5)));
            senderPhoto.setImageBitmap(Helper.decodeImageString(mDBHelper.getFeedDataColumn(keyid, 4)));
            messageTime.setText(Helper.getRelativeTime(mDBHelper.getFeedDataColumn(keyid, 6)));
        }
        mComments.clear();
        mComments.addAll(mDBHelper.getCommentData(keyid));
        recyclerView.setAdapter(mCommentListAdapter);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMessageBox.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    String token = OurApp.getUserToken();
                    Log.d(TAG, "Messaage:" + msg);
                    Log.d(TAG, "Token:" + token);

                    msg = Helper.formCommentMessage("C", keyid, token, msg);

                    Log.d(TAG, "Formfeedmessage" + msg);
                    mWebSocketClient.sendMessage(msg);
                    mMessageBox.setText(null);

                }
            }
        });
        String body = Helper.getCommentFeedRequest(keyid , "2016-12-11 17:00:00");
        new CommentfeedHTTPRequest().execute(body);

    }



    @Override
    public void onOpen() {

    }


    @Override
    public void onClose() {

    }

    @Override
    public void onTextMessage(String message)  {
        JSONObject commentObject = null;
        try {

            commentObject = new JSONObject(message);
            Log.d(TAG, message );

            if (commentObject.optString(Keys.KEY_TYPE).equals("C")){
               Log.d(TAG, "I am message type C:" + ":" +commentObject.optString(Keys.KEY_NAME));
                Log.d(TAG,commentObject.optString(Keys.KEY_ID)+ ":" + keyid );

                if(commentObject.optString(Keys.KEY_ID).equals(keyid)) {
                    //Add to Comment array if it belongs to same post id and notify dataset changed
                    Log.d(TAG, "I am here"  +":" +commentObject.optString(Keys.KEY_NAME) );
                    mComments.add(parseFeeds(message));
                    mCommentListAdapter.notifyDataSetChanged();
                }


                //Notify using Inbox style
                //Notify(mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 1),
                  //      msgObject.optString(Keys.KEY_MESSAGE),
                    //    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 2));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public Person parseFeeds(String message){
        JSONObject msgObject = null;
        Person message_return = null;
        try {
            msgObject = new JSONObject(message);

            message_return = new Person(msgObject.optString(Keys.KEY_TYPE),
                    msgObject.optString(Keys.KEY_ID),
                    msgObject.optString(Keys.KEY_NAME),
                   // mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_USERID), 1),
                    msgObject.optString(Keys.KEY_MESSAGE),
                    msgObject.optString(Keys.KEY_PROFIMG),
                    msgObject.optString(Keys.KEY_IMAGE),
                    msgObject.optString(Keys.KEY_TIME));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message_return;
    }



    private class CommentfeedHTTPRequest extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // UI.showProgressDialog(HomeFeedActivity.this, getString(R.string.login_progress));
        }

        @Override
        protected String doInBackground(String... params) {
            String body = params[0];
            String response = null;
            try {
                response = Helper.getCommentfeed(body);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            //Log.d(TAG, "Response : " + response);
            if(response != null){

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean(Keys.KEY_SUCCESS);

                    if(isSuccess) {

                        Log.d(TAG, "Query returned records");

                        JSONArray mFeedJSONArray = jsonObject.getJSONArray("data");
                        if (mFeedJSONArray != null) {
                            for (int i = 0; i < mFeedJSONArray.length(); i++) {
                                JSONObject feed = new JSONObject(mFeedJSONArray.get(i).toString());
                                Log.d(TAG, "Response : " + feed.toString());
                                mComments.add(0, parseFeeds(feed.toString()));
                                mCommentListAdapter.notifyDataSetChanged();
                                mDBHelper.insertCommentData(feed.toString());
                            }
                        }



                    }else{

                        Log.d(TAG, "Query didn't return records");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Log.d(TAG, "Query failed");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocketClient.removeWebSocketListener(this);
    }
}