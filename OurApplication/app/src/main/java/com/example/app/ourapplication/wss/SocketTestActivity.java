package com.example.app.ourapplication.wss;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app.ourapplication.R;

/**
 * Created by sarumugam on 20/03/16.
 */
public class SocketTestActivity extends AppCompatActivity implements WebSocketListener{

    private TextView mStatusText;
    private TextView mInfoText;
    private EditText mUrlTextBox;
    private Button mConnectButton;
    private Button mDisconnectButton;

    private WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wss);

        mStatusText = (TextView) findViewById(R.id.status);
        mInfoText = (TextView) findViewById(R.id.info);
        mUrlTextBox = (EditText) findViewById(R.id.url_box);
        mConnectButton = (Button) findViewById(R.id.connect);
        mDisconnectButton = (Button) findViewById(R.id.disconnect);

        mWebSocketClient = new WebSocketClient(this);

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebSocketClient.connectToWSS(mUrlTextBox.getText().toString());
            }
        });

        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebSocketClient.disconnect();
            }
        });
    }

    @Override
    public void onOpen() {
        mStatusText.setText("Recent Status is : Open");
    }

    @Override
    public void onClose() {
        mStatusText.setText("Recent Status is : Close");
    }

    @Override
    public void onTextMessage(String message) {
        mStatusText.setText("Recent Status is : New Message");
        mInfoText.setText(message);
    }
}
