package com.example.app.ourapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.app.ourapplication.database.DBHelper;
import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.model.request.SignInReqModel;
import com.example.app.ourapplication.rest.model.response.SignInRespModel;
import com.example.app.ourapplication.ui.HomeActivity;
import com.example.app.ourapplication.wss.WebSocketClient;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.app.ourapplication.rest.ApiUrls.WS_URL;

/**
 * Created by sarumugam on 15/01/17.
 */
public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userId = PreferenceEditor.getInstance(this).getLoggedInUserName();
        String password = PreferenceEditor.getInstance(this).getLoggedInPassword();

        if (TextUtils.isEmpty(userId)) {
            goToSignInScreen();
        } else{
            SignInReqModel signInModel = new SignInReqModel();
            signInModel.setUserId(userId);
            signInModel.setPassword(password);
            Call<SignInRespModel> signInRequest = ((OurApplication)getApplicationContext()).getRestApi().signIn(signInModel);
            signInRequest.enqueue(new Callback<SignInRespModel>() {
                @Override
                public void onResponse(Response<SignInRespModel> response, Retrofit retrofit) {
                    if(response.body().isSuccess()) {
                        ((OurApplication)getApplicationContext()).setUserToken(response.body().getToken());
                        goToHomeScreen();
                    }else{
                        goToSignInScreen();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    goToSignInScreen();
                }
            });
        }
    }

    private void goToHomeScreen(){
        WebSocketClient webSocketClient = ((OurApplication)getApplicationContext()).getClient();
        webSocketClient.connectToWSS(WS_URL + "/" + ((OurApplication)getApplicationContext()).getUserToken());
        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
    }

    private void goToSignInScreen(){
        PreferenceEditor.getInstance(SplashActivity.this).setLoggedInUserName(null,null);
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}