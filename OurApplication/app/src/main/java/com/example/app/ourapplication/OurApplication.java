package com.example.app.ourapplication;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.example.app.ourapplication.rest.RetrofitClient;
import com.example.app.ourapplication.rest.api.RestApi;
import com.example.app.ourapplication.util.NetworkReceiver;
import com.example.app.ourapplication.wss.WebSocketClient;

public class OurApplication extends Application implements NetworkReceiver.NetworkIntf {

    private String mToken;
    private WebSocketClient mClient;
    private RestApi mRestApi;
    private NetworkReceiver mNetworkReceiver;

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
            mNetworkReceiver = new NetworkReceiver(this);
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        return mClient;
    }

    @Override
    public void onConnected() {
        if(mClient != null){
            mClient.connectToWSS();
        }
    }

    @Override
    public void onDisconnected() {
        if(mClient != null && mClient.isConnected()){
            mClient.disconnect();
        }
    }
}