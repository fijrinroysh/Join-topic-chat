package com.example.app.ourapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.util.UI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sarumugam on 17/04/16.
 */
public class LoginActivity extends AppCompatActivity {

    enum RequestType{
        LOGIN(0),
        SIGNUP(1);

        private int value;

        RequestType(int value) {
            this.value = value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private final String TAG = FirstActivity.class.getSimpleName();

    private EditText mUserNameBox;
    private EditText mPasswordBox;
    private Button mLoginButton;
    private EditText mSignUpNameBox;
    private EditText mSignUpPasswordBox;
    private Button mSignUpButton;
    private TextView mNewUserText;
    private ViewFlipper mScreenFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login));
        initializeViews();
        setUpListeners();
    }

    @Override
    public void onBackPressed() {
        if(mScreenFlipper.getDisplayedChild() == 1){
            mScreenFlipper.setDisplayedChild(0);
            setTitle(getString(R.string.login));
            return;
        }
        super.onBackPressed();
    }

//    TODO
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                if(mScreenFlipper.getDisplayedChild() == 1){
//                    mScreenFlipper.setDisplayedChild(0);
//                    return false;
//                }
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void initializeViews() {
        mUserNameBox = (EditText) findViewById(R.id.username_field);
        mPasswordBox = (EditText) findViewById(R.id.password_field);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mSignUpNameBox = (EditText) findViewById(R.id.sign_up_username_field);
        mSignUpPasswordBox = (EditText) findViewById(R.id.sign_up_password_field);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mNewUserText = (TextView) findViewById(R.id.new_user_text);
        mScreenFlipper = (ViewFlipper) findViewById(R.id.flipper);
    }

    private void setUpListeners() {
        mNewUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreenFlipper.setDisplayedChild(1);
                setTitle(getString(R.string.sign_up));
            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = Helper.getLoginRequestBody(mUserNameBox.getText().toString(),
                        mPasswordBox.getText().toString());
                new ServerTask().execute(body);
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = Helper.getSignUpRequestBody(mSignUpNameBox.getText().toString(),
                        mSignUpPasswordBox.getText().toString());
                new ServerTask(RequestType.SIGNUP).execute(body);
            }
        });
    }

    private class ServerTask extends AsyncTask<String, Void, String> {

        private RequestType mRequestType;

        public ServerTask(RequestType type){
            mRequestType = type;
        }

        public ServerTask(){
            this(RequestType.LOGIN);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UI.showProgressDialog(LoginActivity.this,getString((mRequestType == RequestType.LOGIN)?
                    R.string.login_progress:R.string.sign_up_progress));
        }

        @Override
        protected String doInBackground(String... params) {
            // The web services part to be plugged here.
            String body = params[0];
            String response = null;
            try {
                switch (mRequestType){
                    case LOGIN:
                        response = Helper.login(body);
                        break;
                    case SIGNUP:
                        response = Helper.signUp(body);
                        break;
                }
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
                switch (mRequestType){
                    case LOGIN:
                        UI.dismissProgress();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean isSuccess = jsonObject.getBoolean(Keys.KEY_SUCCESS);

                            if(isSuccess) {
                                PreferenceEditor.getInstance(LoginActivity.this)
                                        .setLoggedInUserName(mUserNameBox.getText().toString(),
                                        mPasswordBox.getText().toString());
                                Intent data = new Intent();
                                String token = null;
                                ArrayList<String> listdata = new ArrayList<String>();

                                token = jsonObject.getString(Keys.KEY_TOKEN);
                                // users= jsonObject.getJSONArray(Keys.KEY_USERS);

                                JSONArray users = jsonObject.getJSONArray(Keys.KEY_USERS);
                                if (users != null) {
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = new JSONObject(users.get(i).toString());
                                        listdata.add(user.getString("username"));
                                    }
                                }
                                Log.d("USERS", listdata.toString());
                                data.putExtra(Keys.KEY_TOKEN, token);
                                data.putStringArrayListExtra(Keys.KEY_USERS, listdata);
                                setResult(RESULT_OK,data);
                                finish();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case SIGNUP:
                        String body = Helper.getLoginRequestBody(mSignUpNameBox.getText().toString(),
                                mSignUpPasswordBox.getText().toString());
                        new ServerTask().execute(body);
                        return;
                }
            }
            UI.dismissProgress();
            Toast.makeText(LoginActivity.this, getString((mRequestType == RequestType.LOGIN)?
                    R.string.login_failed:R.string.sign_up_failed), Toast.LENGTH_SHORT).show();
        }
    }
}