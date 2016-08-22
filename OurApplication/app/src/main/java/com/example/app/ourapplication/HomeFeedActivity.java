package com.example.app.ourapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.roughike.bottombar.OnMenuTabSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarumugam on 17/04/16.
 */
public class HomeFeedActivity extends AppCompatActivity implements WebSocketListener {

    public static final int REQUEST_LOGIN = 6;
    private final String TAG = HomeFeedActivity.class.getSimpleName();
    public static String mToken;
    public static String mRecvr;
    public static String mTitle;
    private ArrayList<String> mUsers = new ArrayList<>();
    private ArrayAdapter<String> mGroupListAdapter;
    public static WebSocketClient mWebSocketClient;
    private DBHelper mDBHelper = new DBHelper(this);
    private RVAdapter mFeedListAdapter;
    RecyclerView recyclerView;


    /*Views*/
    private View mMsgLayout;
    private TextView mNoFeedText;
    private ImageButton mLoginButton;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Person> mFeeds = new ArrayList<>();

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
        mFeedListAdapter = new RVAdapter(mFeeds);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(mFeedListAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView , new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Person item = mFeeds.get(position);
                final Intent discussionIntent = new Intent(HomeFeedActivity.this, DiscussionActivity.class);
                discussionIntent.putExtra(Keys.KEY_MESSAGE,item.age );
                discussionIntent.putExtra(Keys.KEY_NAME,item.name );
                discussionIntent.putExtra(Keys.KEY_PROFIMG,item.photoId );
                discussionIntent.putExtra(Keys.KEY_IMAGE, item.photoMsg );
                startActivity(discussionIntent);
                Toast.makeText(getApplicationContext(), item.age + " is selected!", Toast.LENGTH_SHORT).show();
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


        final Intent composeIntent = new Intent(HomeFeedActivity.this, ComposeActivity.class);

        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);

        bottomBar.setItemsFromMenu(R.menu.bottom_bar_menu, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {
                switch (itemId) {
                    case R.id.add_home:

                        break;
                    case R.id.add_location:
                        //Snackbar.make(tool_bar, "Add Location Item Selected", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.add_image:
                        //Snackbar.make(coordinatorLayout, " Add Image Item Selected", Snackbar.LENGTH_LONG).show();
                        //showFileChooser();
                        composeIntent.putExtra("ITEM","add_image");
                        startActivity(composeIntent);

                        break;
                    case R.id.add_message:
                        // Snackbar.make(coordinatorLayout, "Add Message Item Selected", Snackbar.LENGTH_LONG).show();
                        composeIntent.putExtra("ITEM","add_message");
                        startActivity(composeIntent);
                        break;
                    case R.id.add_profile:
                        startActivity(new Intent(HomeFeedActivity.this, ProfileActivity.class));
                        break;
                }
            }


        });


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
                    mToken = data.getStringExtra(Keys.KEY_TOKEN);
                    mUsers = data.getStringArrayListExtra(Keys.KEY_USERS);
                    onLoginSuccess();
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
       // ComposeActivity.mSendButton.setEnabled(true);
    }

    @Override
    public void onClose() {
      //  ComposeActivity.mSendButton.setEnabled(false);
    }

    @Override
    public void onTextMessage(String message  )  {
        mNoFeedText.setVisibility(View.INVISIBLE);
        mFeeds.add(0, parseFeeds(message));
        mFeedListAdapter.notifyDataSetChanged();

        mDBHelper.insertData(message);
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(message);
            Notify(mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 1), msgObject.optString(Keys.KEY_MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public Person parseFeeds(String message){
        JSONObject msgObject = null;
        Person message_return = null;
        try {
            msgObject = new JSONObject(message);

            message_return = new Person("Message from "+mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME),1) +" to "
                    + mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_TO),1) , msgObject.optString(Keys.KEY_MESSAGE), mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME),2), msgObject.optString(Keys.KEY_IMAGE) );
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
        mWebSocketClient = new WebSocketClient(this);
        mWebSocketClient.connectToWSS(AppUrl.WS_TEST_URL + "/" + mToken);

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


}

