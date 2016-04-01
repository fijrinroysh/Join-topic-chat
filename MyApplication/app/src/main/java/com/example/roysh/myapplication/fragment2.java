package com.example.roysh.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.roysh.myapplication.wss.WebSocketClient;
import com.example.roysh.myapplication.wss.WebSocketListener;

import java.util.ArrayList;

/**
 * Created by ROYSH on 3/9/2016.
 */
public class fragment2 extends Fragment {


    public static fragment2 newInstance(String name) {

        Bundle args = new Bundle();
        args.putString("name", name);

        fragment2 fragment = new fragment2();
        fragment.setArguments(args);
        return fragment;
    }


    public class SocketTestActivity extends AppCompatActivity implements WebSocketListener {

        private final String WS_URL = "ws://ec2-52-77-255-151.ap-southeast-1.compute.amazonaws.com:8080";
        private int mCount = 0;
        private boolean mIsConnected;
        private TextView mStatusText;
        private EditText mUrlTextBox;
        private Button mConnectButton;
        private Button mDisconnectButton;
        private ArrayAdapter mArrayAdapter;

        private WebSocketClient mWebSocketClient;



                /*@Nullable
                public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                    View view = inflater.inflate(R.layout.fragment_main2, container, false);
                    //TextView txtview = (TextView) view.findViewById(R.id.text);
                    // txtview.setText(getArguments().getString("name"));
                    return view;
                    */
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_main2);

            mStatusText = (TextView) findViewById(R.id.status);
            mUrlTextBox = (EditText) findViewById(R.id.url_box);
            mConnectButton = (Button) findViewById(R.id.connect);
            mDisconnectButton = (Button) findViewById(R.id.disconnect);
            ListView infoList = (ListView) findViewById(R.id.info);
            mArrayAdapter = new ArrayAdapter<String>(SocketTestActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
            infoList.setAdapter(mArrayAdapter);

            mWebSocketClient = new WebSocketClient(this);

            mConnectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsConnected) {
                        String msg = mUrlTextBox.getText().toString();
                        if (!TextUtils.isEmpty(msg)) {
                            mWebSocketClient.sendMessage(msg);
                            mUrlTextBox.setText(null);
                        }
                    } else {
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
            mArrayAdapter.add("Message " + mCount + " is : " + message);
        }
    }



}

