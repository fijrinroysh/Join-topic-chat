package com.example.admin.fijona;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by admin on 9/16/2018.
 */
public class WSClient extends WebSocketListener {

    public OkHttpClient client;
    public WebSocket ws;
    private static WSClient mWSClient = null;
    private static WSClientInterface mWSClientInterface;
    public static final int NORMAL_CLOSURE_STATUS = 1000;
    private final String TAG = WSClient.class.getSimpleName();
    private Handler mMessageHandler;
    private Handler mStatusHandler;


    public enum ConnectionStatus {
        DISCONNECTED,
        CONNECTED
    }

    public  WSClient (WSClientInterface wsClientInterface){
        if(mWSClient==null){
            mWSClientInterface = wsClientInterface;
            mWSClient = this;

        }

    }

    @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(TAG, "Method is called after connection and is passed to the interface");
        Message m = mStatusHandler.obtainMessage(0, ConnectionStatus.CONNECTED);
        mStatusHandler.sendMessage(m);

        Log.d(TAG, "Message from  server on open : "  + response);
            //  webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
            Log.d(TAG, "Received message from server and is passed to the interface");
            Message m = mMessageHandler.obtainMessage(0, text);
            mMessageHandler.sendMessage(m);


            //print_result("Receiving : " + text);
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            //print_result("Receiving bytes : " + bytes.hex());
        }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Message m = mStatusHandler.obtainMessage(0, ConnectionStatus.DISCONNECTED);
        mStatusHandler.sendMessage(m);
    }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Log.d(TAG, "Message from  server on close : " + code + " / " + reason);

           // print_result("Closing : " + code + " / " + reason);
            //start();
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            disconnect();
            t.printStackTrace();

        }



    public void start() {
        Log.d(TAG, "Sending a request to connect to server");
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS) // TODO these values are picked randomly, need to be verified.
                .pingInterval(5, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                //.writeTimeout(0, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url("ws://ec2-13-127-207-158.ap-south-1.compute.amazonaws.com:8080/9894231831").build();
        //EchoWebSocketListener listener = new EchoWebSocketListener();
        ws = client.newWebSocket(request, this);

        client.dispatcher().executorService().shutdown();

        mMessageHandler =new Handler(){
            @Override
            public void handleMessage(Message msg){
                mWSClientInterface.onNewMessage((String) msg.obj);
            }
        };

        mStatusHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                mWSClientInterface.onStatusChange((ConnectionStatus) msg.obj);
            }
        };



    }



    public void disconnect() {
        ws.cancel();
        mWSClientInterface = null;
        mMessageHandler.removeCallbacksAndMessages(null);
        mStatusHandler.removeCallbacksAndMessages(null);
    }


    public void sendMessage(String message) {
        ws.send(message);
    }

}
