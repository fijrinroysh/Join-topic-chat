package com.example.roysh.myapplication.wss;

/**
 * Created by sarumugam on 20/03/16.
 */
public interface WebSocketListener {
    void onOpen();
    void onClose();
    void onTextMessage(String message);
}
