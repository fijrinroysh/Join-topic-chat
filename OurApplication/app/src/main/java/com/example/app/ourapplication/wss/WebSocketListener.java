package com.example.app.ourapplication.wss;

/**
 * Created by sarumugam on 20/03/16.
 */
public interface WebSocketListener {
    void onOpen();
    void onClose();
    void onTextMessage(String message);
}
