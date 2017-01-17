package com.example.app.ourapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.model.request.SignInReqModel;
import com.example.app.ourapplication.rest.model.request.SignUpReqModel;
import com.example.app.ourapplication.rest.model.response.SignInRespModel;
import com.example.app.ourapplication.ui.HomeActivity;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.util.UI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by sarumugam on 17/04/16.
 */
public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    //private EditText mUserNameBox;
    private EditText mUserIdBox;
    private EditText mPasswordBox;
    private Button mLoginButton;
    private EditText mSignUpUserIdBox;
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
            mScreenFlipper.setInAnimation(AnimationUtils.loadAnimation(LoginActivity.this,R.anim.right_out_short));
            mScreenFlipper.setOutAnimation(AnimationUtils.loadAnimation(LoginActivity.this,R.anim.left_in_short));
            mScreenFlipper.setDisplayedChild(0);
            setTitle(getString(R.string.login));
            return;
        }
        super.onBackPressed();
    }

    private void initializeViews() {
        mUserIdBox = (EditText) findViewById(R.id.userid_field);
        //mUserNameBox = (EditText) findViewById(R.id.username_field);
        mPasswordBox = (EditText) findViewById(R.id.password_field);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mSignUpUserIdBox = (EditText) findViewById(R.id.sign_up_userid_field);
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
                mScreenFlipper.setInAnimation(AnimationUtils.loadAnimation(LoginActivity.this,R.anim.right_in_short));
                mScreenFlipper.setOutAnimation(AnimationUtils.loadAnimation(LoginActivity.this,R.anim.left_out_long));
                mScreenFlipper.setDisplayedChild(1);
                setTitle(getString(R.string.sign_up));
            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(mUserIdBox.getText().toString(), mPasswordBox.getText().toString());
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(mSignUpUserIdBox.getText().toString(), mSignUpNameBox.getText().toString(),
                        mSignUpPasswordBox.getText().toString());
            }
        });
    }

    private void login(String userId, String password){
        SignInReqModel reqModel = new SignInReqModel();
        reqModel.setUserId(userId);
        reqModel.setPassword(password);
        Call<SignInRespModel> login = ((OurApplication)getApplicationContext()).getRestApi().signIn(reqModel);

        login.enqueue(new Callback<SignInRespModel>() {
            @Override
            public void onResponse(Response<SignInRespModel> response, Retrofit retrofit) {
                if(response.body().isSuccess()){
                    PreferenceEditor.getInstance(LoginActivity.this).setLoggedInUserName(mUserIdBox.getText().toString(),
                            mPasswordBox.getText().toString());
                    ((OurApplication)getApplicationContext()).setUserToken(response.body().getToken());
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signUp(final String userId, String name, final String password){
        SignUpReqModel reqModel = new SignUpReqModel();
        reqModel.setUserId(userId);
        reqModel.setName(name);
        reqModel.setPassword(password);
        final Call<Void> login = ((OurApplication)getApplicationContext()).getRestApi().signUp(reqModel);

        login.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                login(userId,password);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, getString(R.string.sign_up_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}