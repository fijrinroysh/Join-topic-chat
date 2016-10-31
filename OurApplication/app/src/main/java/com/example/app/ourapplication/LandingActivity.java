package com.example.app.ourapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.util.UI;
import com.example.app.ourapplication.wss.WebSocketClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import android.support.v4.widget.DrawerLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandingActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = LandingActivity.class.getSimpleName();

    private ImageButton mLoginButton;

    private ArrayAdapter<String> mUserListAdapter;
    private DBHelper mDBHelper = new DBHelper(this);
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

         /* Assinging the toolbar object ot the view
    and setting the the Action bar to our toolbar
     */
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        final String userid = PreferenceEditor.getInstance(this).getLoggedInUserName();
        String password = PreferenceEditor.getInstance(this).getLoggedInPassword();


        DrawerLayout Drawer = (DrawerLayout) findViewById(R.id.drawer_layout);        // Drawer object Assigned to the view
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }
        }; // Drawer Toggle Object Made
        Drawer.addDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        TextView txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
        txtName.setText(mDBHelper.getProfileInfo(userid, 1));
        CircleImageView circleView = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.circle_view);
        circleView.setImageBitmap(Helper.decodeImageString(mDBHelper.getProfileInfo(userid, 2)));
       // View headerLayout = navigationView.getHeaderView(0);

        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(LandingActivity.this, ProfileActivity.class);
                profileIntent.putExtra(Keys.KEY_ID, userid);
                startActivity(profileIntent);
            }
        });

      navigationView.setNavigationItemSelectedListener(this);


        mLoginButton = (ImageButton) findViewById(R.id.login_floater);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivityForResult(loginIntent, LoginActivity.REQUEST_LOGIN);
            }
        });

        ListView userListView = (ListView) findViewById(R.id.user_list);
        mUserListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        userListView.setAdapter(mUserListAdapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent = new Intent(LandingActivity.this,HomeFeedActivity.class);
             //   Intent intent = new Intent(LandingActivity.this,HomeFeedFragment.class);
                String title = mUserListAdapter.getItem(position);
                String userid = mDBHelper.getProfileId(title);
                intent.putExtra(Keys.KEY_ID,userid);
                intent.putExtra(Keys.KEY_TITLE,title);
                //intent.putExtra(Keys.PERSON_LIST,mDBHelper.getData(userid));
                startActivity(intent);
             /*   Fragment fragment=new HomeFeedFragment();
                if(fragment!=null)
                {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();

                }*/

            }
        });



        if (!TextUtils.isEmpty(userid)) {
            String body = Helper.getLoginRequestBody(userid, password);
            new AutoLoginTask().execute(body);
        }
    }



    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent profileIntent = new Intent(LandingActivity.this, ProfileActivity.class);
           // profileIntent.putExtra(Keys.KEY_ID, userid);
            startActivity(profileIntent);

        } else if (id == R.id.nav_group) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LoginActivity.REQUEST_LOGIN:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> users = data.getStringArrayListExtra(Keys.KEY_USERS);
                    onLoginSuccess(users);
                }
                break;
        }
    }

    private void onLoginSuccess(ArrayList<String> users) {
        mLoginButton.setVisibility(View.GONE);
        mUserListAdapter.addAll(users);

        WebSocketClient webSocketClient = OurApp.getClient();
        webSocketClient.connectToWSS(AppUrl.WS_TEST_URL + "/" + OurApp.getUserToken());
    }

    private class AutoLoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UI.showProgressDialog(LandingActivity.this, getString(R.string.login_progress));
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
            if (response != null) {
                UI.dismissProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean(Keys.KEY_SUCCESS);

                    if (isSuccess) {
                        ArrayList<String> usersList = new ArrayList<String>();

                        OurApp.setUserToken(jsonObject.getString(Keys.KEY_TOKEN));
                        // users= jsonObject.getJSONArray(Keys.KEY_USERS);
                        JSONArray users = jsonObject.getJSONArray(Keys.KEY_USERS);
                        if (users != null) {
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject user = new JSONObject(users.get(i).toString());
                                usersList.add(user.getString("username"));
                                mDBHelper.insertProfile(users.get(i).toString());
                            }
                        }
                        onLoginSuccess(usersList);
                    } else {
                        PreferenceEditor.getInstance(LandingActivity.this).setLoggedInUserName(null, null);
                    }
                } catch (JSONException e) {
                    PreferenceEditor.getInstance(LandingActivity.this).setLoggedInUserName(null, null);
                    e.printStackTrace();
                }
            } else {
                PreferenceEditor.getInstance(LandingActivity.this).setLoggedInUserName(null, null);
            }
        }
    }
}