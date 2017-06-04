package com.example.app.ourapplication.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

/**
 * Created by sarumugam on 27/05/17.
 */
public class NetworkReceiver extends BroadcastReceiver {

    public interface NetworkIntf{
        void onConnected();
        void onDisconnected();
    }

    private ArrayList<NetworkIntf> mNetworkIntfs;

    public NetworkReceiver() {
        this(null);
    }

    public NetworkReceiver(NetworkIntf intf) {
        mNetworkIntfs = new ArrayList<>();
        addNetworkIntf(intf);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            dispatchConnected();
        }else {
            displatchDisconnected();
        }
    }

    public void addNetworkIntf(NetworkIntf intf){
        if(intf == null){
            return;
        }
        if(!mNetworkIntfs.contains(intf)){
            mNetworkIntfs.add(intf);
        }
    }

    public void removeNetworkIntf(NetworkIntf intf){
        if(mNetworkIntfs.contains(intf)){
            mNetworkIntfs.remove(intf);
        }
    }

    private void dispatchConnected(){
        for (NetworkIntf intf : mNetworkIntfs) {
            intf.onConnected();
        }
    }

    private void displatchDisconnected(){
        for (NetworkIntf intf : mNetworkIntfs) {
            intf.onDisconnected();
        }
    }
}