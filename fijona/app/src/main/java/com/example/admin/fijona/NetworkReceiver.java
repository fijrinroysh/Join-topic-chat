package com.example.admin.fijona;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by admin on 9/17/2018.
 */
public class NetworkReceiver extends BroadcastReceiver {

    private final String TAG = NetworkReceiver.class.getSimpleName();
    private static NetworkIntf mNetworkIntf;


    public NetworkReceiver(NetworkIntf intf) {
        mNetworkIntf = intf;

    }


    public interface NetworkIntf{
        void onConnected();
        void onDisconnected();
    }
        @Override
        public void onReceive(Context context, Intent intent) {
            // assumes WordService is a registered service
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Log.d(TAG, "Network state identified as connected and is passed to the Network interface");
                dispatchConnected();
            }else {
                Log.d(TAG, "Network state identified as disconnected and is passed to the Network interface");
                displatchDisconnected();
            }
        }

    private void dispatchConnected(){

        mNetworkIntf.onConnected();

    }

    private void displatchDisconnected(){

        mNetworkIntf.onDisconnected();

    }

}
