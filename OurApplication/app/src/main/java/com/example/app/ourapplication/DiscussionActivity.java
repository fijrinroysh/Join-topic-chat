package com.example.app.ourapplication;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.ourapplication.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ROYSH on 8/3/2016.
 */
public class DiscussionActivity extends AppCompatActivity{

    static List<Person> mComments = new ArrayList<>();
    private RVAdapter mFeedListAdapter;
    RecyclerView recyclerView;
    private final String TAG = DiscussionActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_discussion);
        setSupportActionBar(toolbar);
        TextView senderName = (TextView) findViewById(R.id.sender_name);
        TextView receiverName = (TextView) findViewById(R.id.receiver_name);
        TextView senderMessage = (TextView) findViewById(R.id.sender_message);
        ImageView senderPhoto = (ImageView) findViewById(R.id.sender_photo);
        ImageView messagePhoto = (ImageView) findViewById(R.id.message_photo);

        Button  mSendButton = (Button) findViewById(R.id.send_button);
        final EditText  mMessageBox = (EditText) findViewById(R.id.msg_box);

        mSendButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               String msg = mMessageBox.getText().toString();
                                               if (!TextUtils.isEmpty(msg)) {
                                                   Log.d(TAG, "Messaage:" + msg);
                                                   Log.d(TAG, "Token:" + HomeFeedActivity.mToken);
                                                   Log.d(TAG, "Receivere:" + HomeFeedActivity.mRecvr);
                                                   msg = Helper.formFeedMessage(msg, HomeFeedActivity.mToken, HomeFeedActivity.mRecvr, null);

                                                   Log.d(TAG, "Formfeedmessage" + msg);
                                                   HomeFeedActivity.mWebSocketClient.sendMessage(msg);
                                                   mMessageBox.setText(null);

                                               }
                                           }
                                       });
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        mFeedListAdapter = new RVAdapter(mComments);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mFeedListAdapter);




        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            //Person msg_type = new Person(extras.getString(Keys.KEY_NAME),extras.getString(Keys.KEY_MESSAGE),extras.getString(Keys.KEY_PROFIMG),extras.getString(Keys.KEY_IMAGE));
           // mFeedListAdapter.mComments.add(msg_type);

            senderName.setText(extras.getString(Keys.KEY_NAME));

            receiverName.setText(extras.getString(Keys.KEY_TO));

            senderMessage.setText(extras.getString(Keys.KEY_MESSAGE));
            
            messagePhoto.setImageBitmap(Helper.decodeImageString(extras.getString(Keys.KEY_IMAGE)));

            senderPhoto.setImageBitmap(Helper.decodeImageString(extras.getString(Keys.KEY_PROFIMG)));
        }


    }
}
