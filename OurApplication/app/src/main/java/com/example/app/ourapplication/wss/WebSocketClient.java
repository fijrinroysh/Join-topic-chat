package com.example.app.ourapplication.wss;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * Created by sarumugam on 20/03/16.
 */
public class WebSocketClient extends okhttp3.WebSocketListener {

    private final String TAG = WebSocketClient.class.getSimpleName();
    private String mSocketUrl;
    private WebSocket mWebSocket;

    private ArrayList<WebSocketListener> mWebSocketListeners = new ArrayList<>();

    public void addWebSocketListener(WebSocketListener webSocketListener){
        if(!mWebSocketListeners.contains(webSocketListener)) {
            mWebSocketListeners.add(webSocketListener);
        }
    }

    public void removeWebSocketListener(WebSocketListener webSocketListener){
        if(mWebSocketListeners.contains(webSocketListener)) {
            mWebSocketListeners.remove(webSocketListener);
        }
    }

    public void connectToWSS(){
        if(mSocketUrl == null){
            return;
        }
        connectToWSS(mSocketUrl);
    }

    public void connectToWSS(String socketUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS) // TODO these values are picked randomly, need to be verified.
                .pingInterval(5,  TimeUnit.SECONDS)
                .readTimeout(30,  TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .build();
        mSocketUrl = socketUrl;

        Log.d(TAG,"Connecting to the socket url : "+mSocketUrl);

        Request request = new Request.Builder()
                .url(mSocketUrl)
                .build();
        client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }

    public boolean isConnected(){
        return mWebSocket != null;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        mWebSocket = webSocket;
        for (WebSocketListener listener : mWebSocketListeners) {
            listener.onOpen();
        }
//        String echoMsg = "echo-protocol";
//        mWebSocketConnection.sendTextMessage(echoMsg);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        super.onMessage(webSocket, message);
        Log.d(TAG, "Message is :" + message);
        for (WebSocketListener listener : mWebSocketListeners) {
            listener.onTextMessage(message);
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        for (WebSocketListener listener : mWebSocketListeners) {
            listener.onClose();
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
    }

    public void sendMessage(String message) {
        if(mWebSocket == null){
            return;
        }
        mWebSocket.send(message);
    }

    /**
     * Disconnecting the chat connection.
     */
    public void disconnect() {
        if(mWebSocket != null) {
            mWebSocket.cancel();
            mWebSocket = null;
        }
    }
}