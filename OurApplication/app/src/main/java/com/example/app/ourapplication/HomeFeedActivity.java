package com.example.app.ourapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.util.UI;
import com.example.app.ourapplication.wss.WebSocketClient;
import com.example.app.ourapplication.wss.WebSocketListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarumugam on 17/04/16.
 */
public class HomeFeedActivity extends AppCompatActivity implements WebSocketListener {

    public static final int REQUEST_LOGIN = 6;
    public static final int REQ_LOCATION = 7;
    private final String TAG = HomeFeedActivity.class.getSimpleName();
    public static String mToken;
    public static String mRecvr;
    public static String mTitle;
    private ArrayList<String> mUsers = new ArrayList<>();
    private ArrayAdapter<String> mGroupListAdapter;
    private WebSocketClient mWebSocketClient;
    private DBHelper mDBHelper = new DBHelper(this);
    RecyclerView recyclerView;
    private LocationManager locationManager;

    /*Views*/
    private View mMsgLayout;
    private TextView mNoFeedText;
    private ImageButton mLoginButton;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Person> mFeeds = new ArrayList<>();
    private RVAdapter mFeedListAdapter = new RVAdapter(mFeeds);
    private BottomBar mBottomBar;
    private FragNavController fragNavController;
    //indices to fragments
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;
    private final int TAB_FOURTH = FragNavController.TAB4;


    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        initializeViews();
        setUpFeedList();
        setUpDrawerLyt();
        setListeners();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(mFeedListAdapter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView , new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Person item = mFeeds.get(position);
                final Intent discussionIntent = new Intent(HomeFeedActivity.this, DiscussionActivity.class);
                //discussionIntent.putExtra("object", (Parcelable) item );
                discussionIntent.putExtra(Keys.KEY_ID,item.postid );
                discussionIntent.putExtra(Keys.KEY_MESSAGE,item.msg );
                discussionIntent.putExtra(Keys.KEY_NAME,item.sendername );
                discussionIntent.putExtra(Keys.KEY_TO,item.receivername );
                discussionIntent.putExtra(Keys.KEY_PROFIMG,item.photoId );
                discussionIntent.putExtra(Keys.KEY_IMAGE, item.photoMsg );
                discussionIntent.putExtra(Keys.KEY_TIME, item.timeMsg );
                startActivity(discussionIntent);
                Toast.makeText(getApplicationContext(), item.msg + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        String name = PreferenceEditor.getInstance(this).getLoggedInUserName();
        String password = PreferenceEditor.getInstance(this).getLoggedInPassword();

        if(!TextUtils.isEmpty(name)){
            String body = Helper.getLoginRequestBody(name,password);
            new AutoLoginTask().execute(body);
        }
        bottomBar();

       // BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
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


    public void bottomBar() {


        final Intent composeIntent = new Intent(this, ComposeActivity.class);
        final Intent profileIntent = new Intent(this, ProfileActivity.class);


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                switch (tabId) {
                    case R.id.add_home:

                        break;
                    case R.id.add_location:
                        //Snackbar.make(tool_bar, "Add Location Item Selected", Snackbar.LENGTH_LONG).show();
                        if(isLocationEnabled()){
                            checkInLocation();
                        }else{
                            showAlert();
                        }
                        break;
                  /*  case R.id.add_image:
                        //Snackbar.make(coordinatorLayout, " Add Image Item Selected", Snackbar.LENGTH_LONG).show();
                        //showFileChooser();
                        composeIntent.putExtra("ITEM","add_image");
                        startActivity(composeIntent);

                        break;*/
                    case R.id.add_message:
                        // Snackbar.make(coordinatorLayout, "Add Message Item Selected", Snackbar.LENGTH_LONG).show();
                        composeIntent.putExtra("ITEM","add_message");
                        composeIntent.putExtra("PARENT_CLASS", HomeFeedActivity.class);
                       // startActivityForResult(composeIntent, RETURN);
                        startActivity(composeIntent);
                        break;
                    case R.id.add_profile:
                        startActivity(profileIntent);
                        break;
                }

            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                // Toast.makeText(getApplicationContext(), TabMessage.get(tabId, true), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_LOGIN:
                if(resultCode == RESULT_OK && data != null){
                    mToken = data.getStringExtra(Keys.KEY_TOKEN);
                    mUsers = data.getStringArrayListExtra(Keys.KEY_USERS);
                    onLoginSuccess();
                }
                break;
            case REQ_LOCATION:
                if(isLocationEnabled()){
                    checkInLocation();
                }else{
                    Snackbar.make(mDrawer, "Location is not enabled", Snackbar.LENGTH_LONG).show();
                }
            }
        }


    @Override
    protected void onDestroy() {
        //mWebSocketClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onOpen() {
       // ComposeActivity.mSendButton.setEnabled(true);
    }

    @Override
    public void onClose() {
      //  ComposeActivity.mSendButton.setEnabled(false);
    }


        @Override
    public void onTextMessage(String message  )  {
        //List<Person> mComments = new ArrayList<>(DiscussionActivity.mComments);
        mNoFeedText.setVisibility(View.INVISIBLE);

        JSONObject msgObject = null;
        try {

            msgObject = new JSONObject(message);
            Log.d(TAG, "TYPE:" + msgObject.optString(Keys.KEY_TYPE) + ":");

            if (msgObject.optString(Keys.KEY_TYPE).equals("F")){
                Log.d(TAG, "I am message type F:" + mRecvr +":" +msgObject.optString(Keys.KEY_NAME)+ ":" + msgObject.optString(Keys.KEY_TO)+":" );

                if((msgObject.optString(Keys.KEY_TO).equals(mRecvr)) || (msgObject.optString(Keys.KEY_TO).equals(mRecvr))){
                Log.d(TAG, "I am here" + mRecvr +":" +msgObject.optString(Keys.KEY_NAME)+ ":" + msgObject.optString(Keys.KEY_TO) );
                mFeeds.add(0, parseFeeds(message));
                mFeedListAdapter.notifyDataSetChanged();
            }
                mDBHelper.insertData(message);
            Notify(mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 1),
                    msgObject.optString(Keys.KEY_MESSAGE),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 2));
            }
            else if  (msgObject.optString(Keys.KEY_TYPE).equals("C")){

                Log.d(TAG, "Add to Comment array if it belongs to same post id and notify dataset changed");
                //Perform actions on comment data
                //Insert into Database
                //Notify using Inbox style
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

    private void initializeViews() {
        mMsgLayout = findViewById(R.id.msg_send_lyt);
        mNoFeedText = (TextView) findViewById(R.id.no_feed_msg);
        mLoginButton = (ImageButton) findViewById(R.id.login_floater);
    }

    private void setUpFeedList(){
        ListView groupList = (ListView) findViewById(R.id.group_list);
        mGroupListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mUsers);
        groupList.setAdapter(mGroupListAdapter);
        groupList.setOnItemClickListener(new DrawerItemClickListener());
    }


    private void onLoginSuccess(){
        mLoginButton.setVisibility(View.GONE);
        mGroupListAdapter.addAll(mUsers);
        mFeeds.addAll(0,mDBHelper.getData(PreferenceEditor.getInstance(this).getLoggedInUserName()));
        mFeedListAdapter.notifyDataSetChanged();
        mNoFeedText.setVisibility(View.INVISIBLE);
        establishConnection();
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    // Swaps fragments in the main content view
    private void selectItem(int position) {
       mRecvr =  mDBHelper.getProfileId(mUsers.get(position));
        mTitle = mUsers.get(position);
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
                 getSupportActionBar().setTitle(mTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                mFeeds.clear();
                mFeeds.addAll(0,mDBHelper.getData(mRecvr));
                mFeedListAdapter.notifyDataSetChanged();

            }

            public void onDrawerOpened(View drawerView) {
//                getSupportActionBar().setTitle(getString(R.string.groups));
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                /*ArrayList<String> listdata = new ArrayList<String>();
                listdata.add(user.getString("username"));
                Log.d("USERS", listdata.toString());
                mUsers = listdata;
                mGroupListAdapter.addAll(mUsers);*/
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
    }

    private void setListeners() {

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(HomeFeedActivity.this, LoginActivity.class);
                startActivityForResult(loginIntent, REQUEST_LOGIN);
            }
        });
    }

    private void establishConnection(){
        mWebSocketClient = OurApp.getClient();
        mWebSocketClient.addWebSocketListener(this);
        mWebSocketClient.connectToWSS(AppUrl.WS_TEST_URL + "/" + mToken);
    }

    private void Notify(String notificationTitle, String notificationMessage,String notificationIcon){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
      //  Intent notificationIntent = new Intent(this,NotificationView.class);
        Intent notificationIntent = new Intent(this,HomeFeedActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Helper.decodeImageString(notificationIcon))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(notificationMessage))
               // .setSmallIcon(setImageBitmap(Helper.decodeImageString(notificationIcon)))
                .setContentIntent(pendingIntent).build();
        // hide the notification after its selected
       // notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);
    }

    private class AutoLoginTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UI.showProgressDialog(HomeFeedActivity.this,getString(R.string.login_progress));
        }

        @Override
        protected String doInBackground(String... params) {
            String body = params[0];
            String response = null;
            try {
                response = Helper.login(body);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "Response : " + response);
            if(response != null){
                UI.dismissProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean(Keys.KEY_SUCCESS);

                    if(isSuccess) {
                        ArrayList<String> listdata = new ArrayList<String>();

                        mToken = jsonObject.getString(Keys.KEY_TOKEN);
                        // users= jsonObject.getJSONArray(Keys.KEY_USERS);
                        JSONArray users = jsonObject.getJSONArray(Keys.KEY_USERS);
                        if (users != null) {
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject user = new JSONObject(users.get(i).toString());
                                listdata.add(user.getString("username"));
                                mDBHelper.insertProfile(users.get(i).toString());
                            }
                        }
                        Log.d("USERS", listdata.toString());
                        mUsers = listdata;
                        onLoginSuccess();
                    }else{
                        PreferenceEditor.getInstance(HomeFeedActivity.this).setLoggedInUserName(null,null);
                    }
                } catch (JSONException e) {
                    PreferenceEditor.getInstance(HomeFeedActivity.this).setLoggedInUserName(null,null);
                    e.printStackTrace();
                }
            }else{
                PreferenceEditor.getInstance(HomeFeedActivity.this).setLoggedInUserName(null,null);
            }
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent,REQ_LOCATION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Snackbar.make(mDrawer, "Location permission is denied", Snackbar.LENGTH_LONG).show();
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private Location getCurrentLocation() {
        Location currLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d(TAG,"Current GPS location : "+currLoc);
        if (currLoc == null) {
            currLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d(TAG,"Current Network location : "+currLoc.toString());
        }
        return currLoc;
    }

    private void checkInLocation(){
        Location location = getCurrentLocation();
        JSONObject checkInObj = new JSONObject();
        JSONObject locationObj = new JSONObject();
        try {
            locationObj.put("longitude",location.getLongitude());
            locationObj.put("latitude",location.getLatitude());
            checkInObj.put("check_in",locationObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebSocketClient.sendMessage(checkInObj.toString());
    }

}

