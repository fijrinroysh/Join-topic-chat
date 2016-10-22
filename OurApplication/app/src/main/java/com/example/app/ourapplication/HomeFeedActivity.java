package com.example.app.ourapplication;

import android.Manifest;
import android.support.v4.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.wss.WebSocketClient;
import com.example.app.ourapplication.wss.WebSocketListener;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sarumugam on 17/04/16.
 */
public class HomeFeedActivity extends AppCompatActivity {

    public static final int REQ_LOCATION = 7;

    private final String TAG = HomeFeedActivity.class.getSimpleName();
    private DBHelper mDBHelper = new DBHelper(this);
    private WebSocketClient mWebSocketClient;
    private LocationManager locationManager;
    public Fragment fragment;

    /*Views*/
    private List<Person> mFeeds = new ArrayList<>();
    private FeedRVAdapter mFeedListAdapter;
//    private BottomBar mBottomBar;
    private FragNavController fragNavController;
    private String mReceiver;
    private String mReceiverid;

    //indices to fragments
   private final int TAB_FIRST = FragNavController.TAB1;
   private final int TAB_SECOND = FragNavController.TAB2;
  private final int TAB_THIRD = FragNavController.TAB3;
 private final int TAB_FOURTH = FragNavController.TAB4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_home);
        List<Fragment> fragments = new ArrayList<>(4);

        //add fragments to list
        fragments.add(HomeFeedFragment.newInstance("home","frag"));
        fragments.add(LocationFragment.newInstance("location","frag1"));
        fragments.add(ComposeFragment.newInstance("compose","frag2"));
        fragments.add(ProfileFragment.newInstance("profile","frag3"));

        //link fragments to container
        fragNavController = new FragNavController(getSupportFragmentManager(),R.id.frame,fragments);

       // fragNavController.switchTab(TAB_FIRST);


    /*    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mFeeds = getIntent().getParcelableArrayListExtra(Keys.PERSON_LIST);

        mReceiver = getIntent().getStringExtra(Keys.KEY_TITLE);
        mReceiverid = getIntent().getStringExtra(Keys.KEY_ID);
        mFeeds = mDBHelper.getFeedData(mReceiverid);
        mFeedListAdapter = new FeedRVAdapter(mFeeds);
        getSupportActionBar().setTitle(mReceiver);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);


        recyclerView.setAdapter(mFeedListAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Person item = mFeeds.get(position);
                final Intent discussionIntent = new Intent(HomeFeedActivity.this, DiscussionActivity.class);
                discussionIntent.putExtra(Keys.KEY_ID, item.mPostId);
                startActivity(discussionIntent);
                Toast.makeText(getApplicationContext(), item.mMessage + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/

        bottomBar();

      /*  locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mWebSocketClient = OurApp.getClient();
        mWebSocketClient.addWebSocketListener(this);*/
    }

    public void bottomBar() {
      //  final Intent composeIntent = new Intent(this, ComposeActivity.class);
        //final Intent profileIntent = new Intent(this, ProfileActivity.class);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
      // BottomBar bottomBar=BottomBar.attach(this,savedInstanceState);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                switch (tabId) {
                   /* case R.id.add_home:
                    fragNavController.switchTab(TAB_FIRST);
                      break;*/
                    case R.id.add_location:
                        //Snackbar.make(tool_bar, "Add Location Item Selected", Snackbar.LENGTH_LONG).show();
                      /*  if (isLocationEnabled()) {
                            checkInLocation();
                        } else {
                            showAlert();
                        }*/
                           fragNavController.switchTab(TAB_SECOND);
                        //fragment = new LocationFragment();
                        break;
                  /*  case R.id.add_image:
                        //Snackbar.make(coordinatorLayout, " Add Image Item Selected", Snackbar.LENGTH_LONG).show();
                        //showFileChooser();
                        composeIntent.putExtra("ITEM","add_image");
                        startActivity(composeIntent);

                        break;*/
                    case R.id.add_message:
                        // Snackbar.make(coordinatorLayout, "Add Message Item Selected", Snackbar.LENGTH_LONG).show();
                        //composeIntent.putExtra("ITEM", "add_message");
                        //composeIntent.putExtra(Keys.KEY_ID,mReceiverid);
                        // startActivityForResult(composeIntent, RETURN);
                        //startActivity(composeIntent);
                        fragNavController.switchTab(TAB_THIRD);
                        //fragment = new ComposeFragment();
                        break;
                    case R.id.add_profile:
                        //startActivity(profileIntent);
                        fragNavController.switchTab(TAB_FOURTH);
                        //fragment = new ProfileFragment();
                        break;
                   default:
                  fragNavController.switchTab(TAB_FIRST);
                      // fragment = new HomeFeedFragment();
                }

            }
        });

    /*    if(fragment!=null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();

        }*/
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                fragNavController.switchTab(TAB_FIRST);
                // Toast.makeText(getApplicationContext(), TabMessage.get(tabId, true), Toast.LENGTH_LONG).show();
              //  if(tabId==R.id.add_home)
                //{fragNavController.clearStack();}
            }
        });

    }




    public void onBackPressed() {
        if (fragNavController.getCurrentStack().size() > 1) {
            fragNavController.pop();
        } else {
            super.onBackPressed();
        }
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_LOCATION:
                if (isLocationEnabled()) {
                    checkInLocation();
                } else {
//                    Snackbar.make(mDrawer, "Location is not enabled", Snackbar.LENGTH_LONG).show();
                }
        }
    }*/

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocketClient.removeWebSocketListener(this);
    }

    @Override
    public void onOpen() {
    }

    @Override
    public void onClose() {
    }*/

  /*  @Override
    public void onTextMessage(String message) {
        JSONObject msgObject = null;
        try {

            msgObject = new JSONObject(message);
            Log.d(TAG, "TYPE:" + msgObject.optString(Keys.KEY_TYPE) + ":");*/

     /*       if (msgObject.optString(Keys.KEY_TYPE).equals("F")) {
                Log.d(TAG, "I am message type F:" + mReceiver + ":" + msgObject.optString(Keys.KEY_NAME) + ":" + msgObject.optString(Keys.KEY_TO) + ":");

                if ((msgObject.optString(Keys.KEY_NAME).equals(mReceiverid)) || (msgObject.optString(Keys.KEY_TO).equals(mReceiverid))) {
                    Log.d(TAG, "I am here" + mReceiver + ":" + msgObject.optString(Keys.KEY_NAME) + ":" + msgObject.optString(Keys.KEY_TO));
                    mFeeds.add(0, parseFeeds(message));
                    mFeedListAdapter.notifyDataSetChanged();
                }
                mDBHelper.insertFeedData(message);
            Notify(mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 1),
                    msgObject.optString(Keys.KEY_MESSAGE),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 2));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Person parseFeeds(String message) {
        Date now = new Date();
        JSONObject msgObject = null;
        Person message_return = null;
        try {
            msgObject = new JSONObject(message);

            message_return = new Person(msgObject.optString(Keys.KEY_TYPE),
                    msgObject.optString(Keys.KEY_ID),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 1),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_TO), 1),
                    msgObject.optString(Keys.KEY_MESSAGE),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 2),
                    msgObject.optString(Keys.KEY_IMAGE),
                    msgObject.optString(Keys.KEY_TIME)
                    );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message_return;
    }


    private void Notify(String notificationTitle, String notificationMessage, String notificationIcon) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        //  Intent notificationIntent = new Intent(this,NotificationView.class);
                Intent notificationIntent = new Intent(this, HomeFeedActivity.class);
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
                        startActivityForResult(myIntent, REQ_LOCATION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        Snackbar.make(mDrawer, "Location permission is denied", Snackbar.LENGTH_LONG).show();
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
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
        if(location == null){
            return;
        }
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
    }*/
}