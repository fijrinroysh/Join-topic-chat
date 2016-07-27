package com.example.app.ourapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.app.ourapplication.wss.WebSocketClient;

import com.example.app.ourapplication.util.Helper;

import java.io.IOException;

/**
 * Created by ROYSH on 7/24/2016.
 */
public class ComposeActivity extends AppCompatActivity {

    private final String TAG = ComposeActivity.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST = 1;
    public static Bitmap mBitmap;
    //private WebSocketClient mWebSocketClient;
    private String imagemessage;
    String msg_type;
    ImageView img;
    private EditText mMessageBox;
    public static Button mSendButton;
    //mWebSocketClient = new WebSocketClient(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose);
        img = (ImageView) findViewById(R.id.img);
        mSendButton = (Button) findViewById(R.id.send_button);
        mMessageBox = (EditText) findViewById(R.id.msg_box);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mMessageBox, InputMethodManager.SHOW_IMPLICIT);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMessageBox.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    Log.d(TAG,"Messaage:" + msg );
                    Log.d(TAG,"Token:" + HomeFeedActivity.mToken);
                    Log.d(TAG,"Receivere:" + HomeFeedActivity.mRecvr );
                    Log.d(TAG,"Bitmap:" + mBitmap);
                    msg = Helper.formFeedMessage(msg,HomeFeedActivity.mToken,HomeFeedActivity.mRecvr,mBitmap);

                    Log.d(TAG, "Formfeedmessage" + msg);
                    HomeFeedActivity.mWebSocketClient.sendMessage(msg);
                    mMessageBox.setText(null);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            msg_type = extras.getString("ITEM");

        }
        switch (msg_type) {
            case "add_image":

                showFileChooser();

                break;

            case "add_message":


                img.setVisibility(View.INVISIBLE);


                break;


        }

    }




    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMAGE_REQUEST:

                if (resultCode == RESULT_OK && data != null
                        && data.getData() != null) {
                    Uri filePath = data.getData();
                    try {
                        //Getting the Bitmap from Gallery
                        mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        img.setImageBitmap(mBitmap);
                        imagemessage = Helper.getStringImage(mBitmap);
                        Log.d(TAG, "Image message value length : " + imagemessage.length());
                        Log.d(TAG, "Image message value is : " + imagemessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                break;
        }

    }



}




