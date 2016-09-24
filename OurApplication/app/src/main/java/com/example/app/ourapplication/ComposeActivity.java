package com.example.app.ourapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;


/**
 * Created by ROYSH on 7/24/2016.
 */
public class ComposeActivity extends AppCompatActivity {

    private final String TAG = ComposeActivity.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST = 3;
    private Bitmap mBitmap;
    //private WebSocketClient mWebSocketClient;
    private String imagemessage;
    String msg_type;
    ImageView img;
    private EditText mMessageBox;
    public static Button mSendButton;
    public static ImageButton camera_button;
    public static ImageButton gallery_button;
    public static final int RETURN=8;
    public static String feedmessage;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    //mWebSocketClient = new WebSocketClient(this);

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri; // file url to store image/video

    private ImageView imgPreview;
    private VideoView videoPreview;
    private String PARENT_CLASS;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose);
        img = (ImageView) findViewById(R.id.img);
        mSendButton = (Button) findViewById(R.id.send_button);
        mMessageBox = (EditText) findViewById(R.id.msg_box);
        camera_button = (ImageButton) findViewById(R.id.camera_button);
        gallery_button =(ImageButton) findViewById(R.id.gallery);
       
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMessageBox.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    Log.d(TAG, "Messaage:" + msg);
                    Log.d(TAG, "Token:" + HomeFeedActivity.mToken);
                    Log.d(TAG, "Receiver:" + HomeFeedActivity.mRecvr);
                    Log.d(TAG, "Bitmap:" + mBitmap);
                    feedmessage = Helper.formFeedMessage("F", msg, HomeFeedActivity.mToken, HomeFeedActivity.mRecvr, mBitmap);
                    Log.d(TAG, "Formfeedmessage:" + feedmessage);
                    HomeFeedActivity.mWebSocketClient.sendMessage(feedmessage);
                    mMessageBox.setText(null);
                    finish();
                }
            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        gallery_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                showFileChooser();
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            msg_type = extras.getString("ITEM");
            PARENT_CLASS = extras.getString("PARENT_CLASS");

        }
        switch (msg_type) {
            case "add_image":

               // showFileChooser();

                break;

            case "add_message":


                //img.setVisibility(View.INVISIBLE);


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


    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    private final File getOutputMediaFile(int type) {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void previewCapturedImage() {
        try {

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            Log.d(TAG, "Image bitmap value is : " + bitmap);
            img.setImageBitmap(bitmap);

            //mBitmap=BITMAP_RESIZER(bitmap,200,200);
            mBitmap=scaleBitmap(bitmap);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
           // mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
            imagemessage = Helper.getStringImage(mBitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        mBitmap=scaleBitmap(bitmap);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                        img.setImageBitmap(mBitmap);
                        imagemessage = Helper.getStringImage(mBitmap);
                        Log.d(TAG, "Image message value length : " + imagemessage.length());
                        Log.d(TAG, "Image message value is : " + imagemessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                break;
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                // if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT)
                            .show();
                }

        }

    }

    public Bitmap BITMAP_RESIZER(Bitmap bitmap,int newWidth,int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;

    }


    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int maxWidth = 1024 ;
        int maxHeight = 512;


        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }
        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);
        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

}




