package com.example.admin.fijona;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.admin.fijona.WSClient.ConnectionStatus;
import okhttp3.WebSocket;


public class MainActivity extends AppCompatActivity  implements WSClientInterface,NetworkReceiver.NetworkIntf {
    private Button start;
    private Button  sendMessageButton;
    private TextView output;
    private EditText messageEditText;
    private WSClient mWSClient = new WSClient(this) ;
    private NetworkReceiver mNetworkReceiver  = new NetworkReceiver(this);
    private String statusMsg;
    TextView mConnectionStatus;
    //WebSocketEcho.getInstance(this);
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        output = (TextView) findViewById(R.id.output);
        sendMessageButton = (Button ) findViewById(R.id.sendMessageButton);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        mConnectionStatus =  (TextView) findViewById(R.id.server_connection_status);
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //client = new OkHttpClient();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "I have clicked the connect button");
                mWSClient.start();
            }
        });


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = messageEditText.getEditableText().toString();
                if (!message.equalsIgnoreCase("")) {
                    messageEditText.setText("");
                    Log.d(TAG, "I have typed the message and clicked the send button");
                    Log.d(TAG, "The message I have typed is: " + message);
                    mWSClient.sendMessage(message);

                }

            }
        });
    }

    public void onConnected(){
        if (statusMsg == getString(R.string.disconnected)) {
            Log.d(TAG, "The device is now connected to the internet...Reconnecting to websocket");
            mWSClient.start();
        }
    }

    public void onDisconnected(){
        mWSClient.disconnect();
        Log.d(TAG, "The device has lost network connectivity");
    }




    @Override
    public void onNewMessage(String message) {
        Log.d(TAG, "Message received from Interface to Main activity: " + message);

        print_result("Receiving : " + message);
    }

    @Override
    public void onStatusChange(ConnectionStatus status) {

         statusMsg = (status == ConnectionStatus.CONNECTED ?
                getString(R.string.connected) : getString(R.string.disconnected));
        mConnectionStatus.setText(statusMsg);
        sendMessageButton.setEnabled(status == ConnectionStatus.CONNECTED);
    }



    private void print_result(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }
}



