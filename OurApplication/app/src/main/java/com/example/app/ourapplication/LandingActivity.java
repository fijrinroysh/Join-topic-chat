package com.example.app.ourapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

public class LandingActivity extends AppCompatActivity {

    private static final String TAG = LandingActivity.class.getSimpleName();

    private ImageButton mLoginButton;

    private ArrayAdapter<String> mUserListAdapter;
    private DBHelper mDBHelper = new DBHelper(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);
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
                String title = mUserListAdapter.getItem(position);
                String userid = mDBHelper.getProfileId(title);
                intent.putExtra(Keys.KEY_ID,userid);
                intent.putExtra(Keys.KEY_TITLE,title);
                //intent.putExtra(Keys.PERSON_LIST,mDBHelper.getData(userid));
                startActivity(intent);
            }
        });

        String name = PreferenceEditor.getInstance(this).getLoggedInUserName();
        String password = PreferenceEditor.getInstance(this).getLoggedInPassword();

        if (!TextUtils.isEmpty(name)) {
            String body = Helper.getLoginRequestBody(name, password);
            new AutoLoginTask().execute(body);
        }
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