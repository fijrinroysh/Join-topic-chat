package com.example.app.ourapplication;

import android.app.Application;

import com.example.app.ourapplication.wss.WebSocketClient;

/**
 * Created by sarumugam on 24/09/16.
 */
public class OurApp extends Application {

    private static WebSocketClient mClient;

    public static WebSocketClient getClient(){
        if(mClient == null){
            mClient = new WebSocketClient();
        }
        return mClient;
    }
}
