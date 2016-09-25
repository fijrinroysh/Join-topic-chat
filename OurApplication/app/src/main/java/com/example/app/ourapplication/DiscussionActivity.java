package com.example.app.ourapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.wss.WebSocketClient;
import com.example.app.ourapplication.wss.WebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ROYSH on 8/3/2016.
 */
public class DiscussionActivity extends AppCompatActivity implements WebSocketListener {

    static List<Person> mComments = new ArrayList<>();
    private RVAdapter mCommentListAdapter;
    private WebSocketClient mWebSocketClient;
    private final String TAG = DiscussionActivity.class.getSimpleName();
    private String keyid;
    private String to;
    private DBHelper mDBHelper = new DBHelper(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_discussion);
        setSupportActionBar(toolbar);
        //TextView senderName = (TextView) findViewById(R.id.sender_name);
        //TextView receiverName = (TextView) findViewById(R.id.receiver_name);
        //TextView senderMessage = (TextView) findViewById(R.id.sender_message);
        //ImageView senderPhoto = (ImageView) findViewById(R.id.sender_photo);
        //ImageView messagePhoto = (ImageView) findViewById(R.id.message_photo);
        Button  mSendButton = (Button) findViewById(R.id.send_button);
        final EditText  mMessageBox = (EditText) findViewById(R.id.msg_box);

        mWebSocketClient = OurApp.getClient();
        mWebSocketClient.addWebSocketListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        mCommentListAdapter = new RVAdapter(mComments);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mCommentListAdapter);
        mComments.clear();
        Bundle extras = getIntent().getExtras();
        //Intent extras = getIntent();
        if (extras != null) {

        Person item = extras.getParcelable(Keys.KEY_PERSON);
        mComments.add(item);

           //senderName.setText(extras.getString(Keys.KEY_NAME));
           //receiverName.setText(extras.getString(Keys.KEY_TO));
           //senderMessage.setText(extras.getString(Keys.KEY_MESSAGE));
           //messagePhoto.setImageBitmap(Helper.decodeImageString(extras.getString(Keys.KEY_IMAGE)));
           //messagePhoto.setImageBitmap(null);
           //senderPhoto.setImageBitmap(Helper.decodeImageString(extras.getString(Keys.KEY_PROFIMG)));
           keyid = item.getPostId();
           to = mDBHelper.getProfileId(item.getReceiverName());
        }

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMessageBox.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    String token = OurApp.getUserToken();
                    Log.d(TAG, "Messaage:" + msg);
                    Log.d(TAG, "Token:" + token);
//                    Log.d(TAG, "Receivere:" + HomeFeedActivity.mRecvr);
                    msg = Helper.formCommentMessage("C",keyid, token, to, msg);

                    Log.d(TAG, "Formfeedmessage" + msg);
                    mWebSocketClient.sendMessage(msg);
                    mMessageBox.setText(null);

                }
            }
        });
    }

    @Override
    public void onOpen() {

    }


    @Override
    public void onClose() {

    }

    @Override
    public void onTextMessage(String message)  {
        //List<Person> mComments = new ArrayList<>(DiscussionActivity.mComments);
        JSONObject commentObject = null;
        try {

            commentObject = new JSONObject(message);
            Log.d(TAG, "TYPEE:" + commentObject.optString(Keys.KEY_TYPE) + ":");

            if (commentObject.optString(Keys.KEY_TYPE).equals("C")){
//                Log.d(TAG, "I am message type C:" + HomeFeedActivity.mRecvr +":" +commentObject.optString(Keys.KEY_NAME)+ ":" + commentObject.optString(Keys.KEY_TO)+":" );

                if(commentObject.optString(Keys.KEY_ID).equals(keyid)) {
                    //Add to Comment array if it belongs to same post id and notify dataset changed
//                    Log.d(TAG, "I am here" + HomeFeedActivity.mRecvr +":" +commentObject.optString(Keys.KEY_NAME)+ ":" + commentObject.optString(Keys.KEY_TO) );
                    mComments.add(parseFeeds(message));
                    mCommentListAdapter.notifyDataSetChanged();
                }
                //Insert into Database
                //mDBHelper.insertData(message);

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
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME),1) ,
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_TO),1) ,
                    msgObject.optString(Keys.KEY_MESSAGE),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME),2),
                    msgObject.optString(Keys.KEY_IMAGE),
                    msgObject.optString(Keys.KEY_TIME));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message_return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocketClient.removeWebSocketListener(this);
    }
}