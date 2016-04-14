package com.example.roysh.myapplication.wss;

import android.util.Log;

import java.net.URI;

import com.example.roysh.myapplication.autobahn.WebSocket;
import com.example.roysh.myapplication.autobahn.WebSocketConnection;
import com.example.roysh.myapplication.autobahn.WebSocketException;
import com.example.roysh.myapplication.autobahn.WebSocketOptions;

/**
 * Created by sarumugam on 20/03/16.
 */
public class WebSocketClient implements WebSocket.WebSocketConnectionObserver {

    private final String TAG = WebSocketClient.class.getSimpleName();
    private URI mWssUri;

    private WebSocketConnection mWebSocketConnection;
    private WebSocketListener mWebSocketListener;

    public WebSocketClient(WebSocketListener webSocketListener){
        mWebSocketListener = webSocketListener;
    }

    public void connectToWSS(String wssUrl) {
        mWebSocketConnection = new WebSocketConnection();
        try {
			/*forming the chat-URL.*/
            try{
                URI chatUrl = new URI(wssUrl);
                Log.d(TAG, "url recieved from Token Manager: "+chatUrl);

                //String path=chatUrl.getPath();
                String scheme=chatUrl.getScheme();
                String schemeSpecificPart=chatUrl.getSchemeSpecificPart();

                //Log.d(TAG, "path from the url: "+path);
                Log.d(TAG, "scheme specific part: "+schemeSpecificPart);
                Log.d(TAG, "scheme from the url: "+scheme);

                if(scheme!=null){
                    if(scheme.equals("https")){
                        scheme="wss";
                    }else if(scheme.equals("http")){
                        scheme="ws";
                    }
                }

                  //this.mWssUri = new URI(wssUrl);
                this.mWssUri = new URI(scheme,schemeSpecificPart,null);
                Log.i(TAG, "Connecting to the URL : " + mWssUri);
            }catch (Exception e) {
                Log.i(TAG, "Connecting failed.");
                e.printStackTrace();
            }
            WebSocketOptions webSocketOptions=new WebSocketOptions();
            webSocketOptions.setReconnectInterval(1000);
            webSocketOptions.setReceiveTextMessagesRaw(true);
            Log.e(TAG, "MaxFramePayloadSize:" + webSocketOptions.getMaxFramePayloadSize());


            String echoMsg[] = {"echo-protocol"};
            mWebSocketConnection.connect(mWssUri,echoMsg, this, webSocketOptions);
        } catch (WebSocketException e) {
            String message = e.getLocalizedMessage();
            Log.e(TAG, message);
        }
    }

    /**
     * Disconnecting the chat connection.
     */
    public void disconnect() {
        if(mWebSocketConnection != null)
            mWebSocketConnection.disconnect();
    }

    @Override
    public void onOpen() {
        mWebSocketListener.onOpen();
//        String echoMsg = "echo-protocol";
//
//        mWebSocketConnection.sendTextMessage(echoMsg);
    }

    @Override
    public void onClose(WebSocketCloseNotification code, String reason) {
        mWebSocketListener.onClose();
    }

    @Override
    public void onTextMessage(String payload) {
        mWebSocketListener.onTextMessage(payload);
    }

    @Override
    public void onRawTextMessage(byte[] payload) {}

    @Override
    public void onBinaryMessage(byte[] payload) {}

    public void sendMessage(String message) {
        mWebSocketConnection.sendTextMessage(message);
    }
}