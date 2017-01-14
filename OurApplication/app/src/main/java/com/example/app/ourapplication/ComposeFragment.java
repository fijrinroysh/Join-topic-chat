package com.example.app.ourapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.wss.WebSocketClient;
import com.roughike.bottombar.BottomBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComposeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private final String TAG = ComposeActivity.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST = 3;
    private Bitmap mBitmap;
    private BottomBar bottomBar;
    private String imagemessage;
    private String msg_type;
    private String mReceiverid;
    private ImageView img;
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
    private WebSocketClient mWebSocketClient;
    public static View view;
    public static Activity activity;
    public static Context thiscontext;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri; // file url to store image/video

    private ImageView imgPreview;
    private VideoView videoPreview;
    public static RelativeLayout layout;

    final String location = PreferenceEditor.getInstance(getContext()).getLocation();


    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.compose, container, false);

        layout = (RelativeLayout) view.findViewById(R.id.msg_send_lyt);

        img = (ImageView) view.findViewById(R.id.img);
        mSendButton = (Button) view.findViewById(R.id.send_button);
        mMessageBox = (EditText) view.findViewById(R.id.msg_box);
        camera_button = (ImageButton) view.findViewById(R.id.camera_button);
        gallery_button =(ImageButton) view.findViewById(R.id.gallery);
        //mReceiverid = getActivity().getIntent().getStringExtra(Keys.KEY_ID);
        showSoftKeyboard(mMessageBox);

        if(img.getDrawable() == null)
        {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.setLayoutParams(params);

        }
      /*  bottomBar = (BottomBar) view.findViewById(R.id.bottomBar);
        bottomBar.setEnabled(false);*/

        mWebSocketClient = OurApp.getClient();
        //mWebSocketClient.addWebSocketListener(this);
        thiscontext=getContext();

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMessageBox.getText().toString();
                try {
                    JSONObject jsonObject = new JSONObject(location);
                    String longitude = jsonObject.optString("longitude");
                    String latitude = jsonObject.optString("latitude");

                if (!TextUtils.isEmpty(msg)) {
                    String token = OurApp.getUserToken();
                    Log.d(TAG, "Messaage:" + msg);
                    Log.d(TAG, "Token:" + token);
                    Log.d(TAG, "Receiver:" + location);
                    Log.d(TAG, "Bitmap:" + mBitmap);
                    feedmessage = Helper.formFeedMessage("F", msg, token, longitude , latitude, mBitmap);
                    Log.d(TAG, "Formfeedmessage:" + feedmessage);
                    mWebSocketClient.sendMessage(feedmessage);
                    mMessageBox.setText(null);

                    //finish();
                }
                } catch (JSONException e) {
                    e.printStackTrace();
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

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
      /*  if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

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
            mBitmap=Helper.scaleBitmap(bitmap);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
            imagemessage = Helper.getStringImage(mBitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMAGE_REQUEST:

                if (resultCode == activity.RESULT_OK && data != null
                        && data.getData() != null) {
                    Uri filePath = data.getData();
                    try {
                        //Getting the Bitmap from Gallery
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), filePath);
                        mBitmap=Helper.scaleBitmap(bitmap);
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
                if (resultCode == activity.RESULT_OK) {
                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                } else if (resultCode == activity.RESULT_CANCELED) {
                    // user cancelled Image capture
                    Toast.makeText(getContext().getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to capture image
                    Toast.makeText(getContext().getApplicationContext(),
                            "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT)
                            .show();
                }

        }

    }

    public void showSoftKeyboard(View v)
    {


        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imgr.showSoftInput(v, 0);
        v.requestFocus();
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


    }





}
