package com.example.admin.fijona;

import okhttp3.WebSocket;

/**
 * Created by admin on 9/17/2018.
 */
public interface WSClientInterface {


    void onNewMessage(String message);
    void onStatusChange(WSClient.ConnectionStatus status);

}