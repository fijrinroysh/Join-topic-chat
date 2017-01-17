package com.example.app.ourapplication;

import android.app.Application;

import com.example.app.ourapplication.rest.RetrofitClient;
import com.example.app.ourapplication.rest.api.RestApi;
import com.example.app.ourapplication.wss.WebSocketClient;

public class OurApplication extends Application {

    private String mToken;
    private WebSocketClient mClient;

    private RestApi mRestApi;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public RestApi getRestApi() {
        if(mRestApi == null){
            mRestApi = RetrofitClient.getRetroClient().create(RestApi.class);
        }
        return mRestApi;
    }

    public String getUserToken(){
        return mToken;
    }

    public void setUserToken(String token){
        mToken = token;
    }

    public WebSocketClient getClient(){
        if(mClient == null){
            mClient = new WebSocketClient();
        }
        return mClient;
    }
}
