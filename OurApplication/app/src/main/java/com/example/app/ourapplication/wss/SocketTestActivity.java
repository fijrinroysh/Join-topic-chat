package com.example.app.ourapplication.wss;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app.ourapplication.R;

import java.util.ArrayList;

/**
 * Created by sarumugam on 20/03/16.
 */
public class SocketTestActivity extends AppCompatActivity implements WebSocketListener{

    private final String WS_URL = "ws://ec2-54-254-185-153.ap-southeast-1.compute.amazonaws.com:8080";
    private int mCount = 0;
    private boolean mIsConnected;
    private TextView mStatusText;
    private EditText mUrlTextBox;
    private Button mConnectButton;
    private Button mDisconnectButton;
    private ArrayAdapter mArrayAdapter;

    private WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wss);

        mStatusText = (TextView) findViewById(R.id.status);
        mUrlTextBox = (EditText) findViewById(R.id.url_box);
        mConnectButton = (Button) findViewById(R.id.connect);
        mDisconnectButton = (Button) findViewById(R.id.disconnect);
        ListView infoList = (ListView) findViewById(R.id.info);
        mArrayAdapter = new ArrayAdapter<String>(SocketTestActivity.this,android.R.layout.simple_list_item_1,new ArrayList<String>());
        infoList.setAdapter(mArrayAdapter);

        mWebSocketClient = new WebSocketClient(this);

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsConnected){
                    String msg = mUrlTextBox.getText().toString();
                    if(!TextUtils.isEmpty(msg)) {
                        mWebSocketClient.sendMessage(msg);
                        mUrlTextBox.setText(null);
                    }
                }else {
                    mWebSocketClient.connectToWSS(mUrlTextBox.getText().toString());
                    mIsConnected = true;
                    mConnectButton.setText("Send");
                }
            }
        });

        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebSocketClient.disconnect();
                mIsConnected = false;
                mConnectButton.setText("Connect");
                mUrlTextBox.setHint("Provide the WSS url to connect...");
                mUrlTextBox.setText(WS_URL);
            }
        });
        mUrlTextBox.setText(WS_URL);
    }

    @Override
    public void onOpen() {
        mStatusText.setText("Recent Status is : Open");
        mUrlTextBox.setHint("Type ur message here...");
        mUrlTextBox.setText(null);
    }

    @Override
    public void onClose() {
        mStatusText.setText("Recent Status is : Close");
    }

    @Override
    public void onTextMessage(String message) {
        mStatusText.setText("Recent Status is : New Message");
        mCount++;
//        mInfoText.setText("Message "+mCount +" is : "+message);
        mArrayAdapter.add("Message "+mCount +" is : "+message);
    }
}
