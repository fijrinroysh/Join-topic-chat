package com.example.app.ourapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app.ourapplication.wss.WebSocketClient;
import com.example.app.ourapplication.wss.WebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sarumugam on 17/04/16.
 */
public class HomeFeedActivity extends AppCompatActivity implements WebSocketListener {

    public static final int REQUEST_LOGIN = 6;
    private final String TAG = HomeFeedActivity.class.getSimpleName();
    private String mToken;
    private ArrayList<String> mFeeds = new ArrayList<>();
    private ArrayAdapter<String> mFeedListAdapter;
    private WebSocketClient mWebSocketClient;

    /*Views*/
    private View mMsgLayout;
    private TextView mNoFeedText;
    private EditText mMessageBox;
    private Button mSendButton;
    private ImageButton mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        initializeViews();
        setUpFeedList();
        setListeners();
        establishConnection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_LOGIN:
                if(resultCode == RESULT_OK && data != null){
                    mMsgLayout.setVisibility(View.VISIBLE);
                    mLoginButton.setVisibility(View.GONE);
                    mToken = data.getStringExtra(Keys.KEY_TOKEN);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mWebSocketClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onOpen() {
        mSendButton.setEnabled(true);
    }

    @Override
    public void onClose() {
        mSendButton.setEnabled(false);
    }

    @Override
    public void onTextMessage(String message) {
        mNoFeedText.setVisibility(View.INVISIBLE);
        mFeedListAdapter.add(parseFeeds(message));
    }

    private void initializeViews() {
        mMsgLayout = findViewById(R.id.msg_send_lyt);
        mNoFeedText = (TextView) findViewById(R.id.no_feed_msg);
        mMessageBox = (EditText) findViewById(R.id.msg_box);
        mSendButton = (Button) findViewById(R.id.send_button);
        mLoginButton = (ImageButton) findViewById(R.id.login_floater);
    }

    private void setUpFeedList(){
        ListView feedList = (ListView) findViewById(R.id.feed_list);
        mFeedListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mFeeds);
        feedList.setAdapter(mFeedListAdapter);
    }

    private void setListeners() {
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMessageBox.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    msg = formFeedMessage(msg);
                    mWebSocketClient.sendMessage(msg);
                    mMessageBox.setText(null);
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(HomeFeedActivity.this, LoginActivity.class);
                startActivityForResult(loginIntent, REQUEST_LOGIN);
            }
        });
    }

    private void establishConnection(){
        mWebSocketClient = new WebSocketClient(this);
        mWebSocketClient.connectToWSS(AppUrl.WS_URL);
    }

    private String formFeedMessage(String message){
        JSONObject msgObject = new JSONObject();
        try {
            msgObject.put(Keys.KEY_MESSAGE,message);
            msgObject.put(Keys.KEY_TOKEN,mToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgObject.toString();
    }

    private String parseFeeds(String message){
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(message);
            message = msgObject.optString(Keys.KEY_MESSAGE)+" From : "+msgObject.optString(Keys.KEY_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }
}