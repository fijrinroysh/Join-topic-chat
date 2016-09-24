package com.example.app.ourapplication;

import android.app.Application;

import com.example.app.ourapplication.wss.WebSocketClient;

public class OurApp extends Application {

    private static String mToken;
    private static WebSocketClient mClient;

    public static String getUserToken(){
        return mToken;
    }

    public static void setUserToken(String token){
        mToken = token;
    }

    public static WebSocketClient getClient(){
        if(mClient == null){
            mClient = new WebSocketClient();
        }
        return mClient;
    }
}
