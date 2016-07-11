package com.example.app.ourapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Intent;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app.ourapplication.pref.PrefKeys;
import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.wss.WebSocketClient;
import com.example.app.ourapplication.wss.WebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
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




    private ArrayAdapter<String> mGroupListAdapter;
    private WebSocketClient mWebSocketClient;
    private DBHelper mDBHelper = new DBHelper(this);
    private RVAdapter mFeedListAdapter;

    /*Views*/
    private View mMsgLayout;
    private TextView mNoFeedText;
    private EditText mMessageBox;
    private Button mSendButton;
    private ImageButton mLoginButton;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;



    List<Person> mFeeds = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);


        initializeViews();
        setUpFeedList();
        setUpDrawerLyt();
        setListeners();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        mFeedListAdapter = new RVAdapter(mFeeds);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mFeedListAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    PreferenceEditor preferenceEditor = new PreferenceEditor(this);
                    mFeeds.addAll(mDBHelper.getData(preferenceEditor.getLoggedInUserName()));
                    mNoFeedText.setVisibility(View.INVISIBLE);
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
    public void onTextMessage(String message)  {
        mNoFeedText.setVisibility(View.INVISIBLE);
        mFeedListAdapter.mFeeds.add(parseFeeds(message));
        //add(parseFeeds(message));
        mFeedListAdapter.notifyDataSetChanged();

        mDBHelper.insertData(message);
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
        mWebSocketClient.connectToWSS(AppUrl.WS_TEST_URL + "/" + mToken);

    }

    private String formFeedMessage(String message){
        JSONObject msgObject = new JSONObject();
        try {
            msgObject.put(Keys.KEY_MESSAGE,message);
            msgObject.put(Keys.KEY_TOKEN,mToken);
            msgObject.put(Keys.KEY_TO,mRecvr);
            msgObject.put(Keys.KEY_IMAGE,"kllk");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgObject.toString();
    }


    private Person parseFeeds(String message){
        Log.d(TAG,"Message : "+message);
        JSONObject msgObject = null;
        Person message_return = null;
        try {
            msgObject = new JSONObject(message);
            String rimgmessage = msgObject.optString(Keys.KEY_IMAGE);
            String msg = rimgmessage.substring(0, rimgmessage.length() - 1);
            byte[] decodedString = Base64.decode(msg, Base64.NO_PADDING);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            message_return = new Person("Message from "+msgObject.optString(Keys.KEY_NAME) +" to "+ msgObject.optString(Keys.KEY_TO) , msgObject.optString(Keys.KEY_MESSAGE), R.drawable.mickey, decodedByte );
           // message = "Message from "+msgObject.optString(Keys.KEY_NAME) +" to "+ msgObject.optString(Keys.KEY_TO) +" : "+ msgObject.optString(Keys.KEY_MESSAGE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message_return;
    }

    private void Notify(String notificationTitle, String notificationMessage){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
      //  Intent notificationIntent = new Intent(this,NotificationView.class);
        Intent notificationIntent = new Intent(this,HomeFeedActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
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
