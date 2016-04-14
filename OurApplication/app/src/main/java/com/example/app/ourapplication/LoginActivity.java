package com.example.app.ourapplication;

import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

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
    private ProgressDialog mLoginProgressDlg;

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
                String body = getLoginRequestBody();
                new ServerTask().execute(body);
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = getSignUpRequestBody();
                new ServerTask(RequestType.SIGNUP).execute(body);
            }
        });
    }

    private void showProgressDialog(String message) {
        if(mLoginProgressDlg == null){
            mLoginProgressDlg = new ProgressDialog(LoginActivity.this);
            mLoginProgressDlg.setCanceledOnTouchOutside(false);
            mLoginProgressDlg.setCancelable(false);
        }
        mLoginProgressDlg.setMessage(message);
        mLoginProgressDlg.show();
    }

    private String getLoginRequestBody(){
        JSONObject body = new JSONObject();
        try {
            body.put("name",mUserNameBox.getText());
            body.put("password",mPasswordBox.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

    private String getSignUpRequestBody(){
        JSONObject body = new JSONObject();
        try {
            body.put("name",mSignUpNameBox.getText());
            body.put("password",mSignUpPasswordBox.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body.toString();
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
            showProgressDialog(getString((mRequestType == RequestType.LOGIN)?
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
                        response = login(body);
                        break;
                    case SIGNUP:
                        response = signUp(body);
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
            mLoginProgressDlg.dismiss();
            Log.d(TAG, "Response : " + response);
            if(response != null){
                switch (mRequestType){
                    case LOGIN:
                    case SIGNUP:
                        setResult(RESULT_OK);
                        finish();
                        break;
                }
            }else{
                Toast.makeText(LoginActivity.this, getString((mRequestType == RequestType.LOGIN)?
                        R.string.login_failed:R.string.sign_up_failed), Toast.LENGTH_SHORT).show();
            }
        }

        private String login(String reqBody) throws IOException {
            HttpURLConnection connection = Util.getHttpConnection(AppUrl.SERVER_URL,"POST");
            Util.writeToStream(connection, reqBody);

            return Util.readInputStream(connection);
        }

        private String signUp(String reqBody) throws IOException {
            HttpURLConnection connection = Util.getHttpConnection(AppUrl.SERVER_URL,"POST");
            Util.writeToStream(connection, reqBody);

            return Util.readInputStream(connection);
        }
    }
}