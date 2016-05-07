package com.example.app.ourapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sarumugam on 17/04/16.
 */
public class HomeFeedActivity extends AppCompatActivity implements WebSocketListener {

    public static final int REQUEST_LOGIN = 6;
    private final String TAG = HomeFeedActivity.class.getSimpleName();
    private String mToken;
    private String mRecvr;
    private ArrayList<String> mUsers = new ArrayList<>();
    private ArrayList<String> mFeeds = new ArrayList<>();





    private ArrayAdapter<String> mFeedListAdapter;
    private ArrayAdapter<String> mGroupListAdapter;
    private WebSocketClient mWebSocketClient;

    /*Views*/
    private View mMsgLayout;
    private TextView mNoFeedText;
    private EditText mMessageBox;
    private Button mSendButton;
    private ImageButton mLoginButton;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        initializeViews();
        setUpFeedList();
        setUpDrawerLyt();
        setListeners();

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
                    mUsers = data.getStringArrayListExtra(Keys.KEY_USERS);
                    mGroupListAdapter.addAll(mUsers);

                    establishConnection();

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
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(message);
            Notify(msgObject.optString(Keys.KEY_NAME), msgObject.optString(Keys.KEY_MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
        ListView groupList = (ListView) findViewById(R.id.group_list);
        mGroupListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mUsers);
        groupList.setAdapter(mGroupListAdapter);
        groupList.setOnItemClickListener(new DrawerItemClickListener());

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    // Swaps fragments in the main content view
    private void selectItem(int position) {
       mRecvr =  mUsers.get(position);
        mDrawer.closeDrawers();
    }



    private void setUpDrawerLyt(){
        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        // enable ActionBar app icon to behave as action to toggle nav drawer
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this,/* host Activity */
                mDrawer,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
//                getSupportActionBar().setTitle(getString(R.string.app_name));
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
//                getSupportActionBar().setTitle(getString(R.string.groups));
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
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
        mWebSocketClient.connectToWSS(AppUrl.WS_URL + "/" + mToken);

    }

    private String formFeedMessage(String message){
        JSONObject msgObject = new JSONObject();
        try {
            msgObject.put(Keys.KEY_MESSAGE,message);
            msgObject.put(Keys.KEY_TOKEN,mToken);
            msgObject.put(Keys.KEY_TO,mRecvr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgObject.toString();
    }



    private String parseFeeds(String message){
        Log.d(TAG,"Message : "+message);
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(message);
            message = msgObject.optString(Keys.KEY_MESSAGE)+" From : "+msgObject.optString(Keys.KEY_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    private void Notify(String notificationTitle, String notificationMessage){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        Intent notificationIntent = new Intent(this,NotificationView.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //Notification notification = new Notification(R.mipmap.ic_launcher,"New Message", System.currentTimeMillis());

        Notification notification = new Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

// hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // notification.setLatestEventInfo(MainActivity.this, notificationTitle,notificationMessage, pendingIntent);
        // notification.setLatestEventInfo(getApplicationContext(), notificationTitle, notificationMessage, pendingIntent);
        notificationManager.notify(0, notification);
    }




}
