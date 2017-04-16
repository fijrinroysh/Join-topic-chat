package com.example.app.ourapplication;

import android.Manifest;
import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.OkHttpClient;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.api.FileUploadApi;
import com.example.app.ourapplication.rest.ApiUrls;
import com.example.app.ourapplication.rest.model.request.LocationModel;
import com.example.app.ourapplication.rest.model.response.CompleteFeedModel;
import com.example.app.ourapplication.rest.model.response.ComposeRespModel;
import com.example.app.ourapplication.ui.HomeActivity;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.util.UI;
import com.example.app.ourapplication.wss.WebSocketClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;*/

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.app.ProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComposeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

    private final String TAG = ComposeFragment.class.getSimpleName();
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int PICK_IMAGE_REQUEST = 3;
    private static final int RETURN = 8;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private OnFragmentInteractionListener mListener;
    private Bitmap mBitmap;
    private ImageView img;
    private EditText mMessageBox;
    //private BottomBar bottomBar;

    private WebSocketClient mWebSocketClient;
    private Uri fileUri; // file url to store image/video
    private static Uri filePath;
    private static String AbsolutefilePath;
    private static Bitmap bmThumbnail;
    private static String pathtype;
   // private Map<String, RequestBody> map = new HashMap<>();

   // private WebSocketClient mWebSocketClient;
   // private Uri fileUri; // file url to store image/video
    //RestApi service;
    private static ProgressDialog pDialog;
    private  ImageView iv ;
    private VideoView vv ;
    private RelativeLayout msg_send_lyt;
    private static String filetype;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ComposeFragment.
     */
    public static ComposeFragment newInstance() {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebSocketClient = ((OurApplication)getActivity().getApplicationContext()).getClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.compose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMessageBox = (EditText) view.findViewById(R.id.msg_box);

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.msg_send_lyt);
        HomeActivity.bottomBar.setVisibility(View.INVISIBLE);
        UI.showSoftKeyboard(getActivity(),mMessageBox);
//        HomeActivity.bottomBar.setActivated(false);
       // img = (ImageView) view.findViewById(R.id.img);
        Button sendButton = (Button) view.findViewById(R.id.send_button);
        msg_send_lyt = (RelativeLayout) view.findViewById(R.id.msg_send_lyt);
        ImageButton cameraButton = (ImageButton) view.findViewById(R.id.camera_button);
        ImageButton galleryButton = (ImageButton) view.findViewById(R.id.gallery);


/*
        if(img.getDrawable() == null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.SOFT_INPUT_ADJUST_PAN);
            layout.setLayoutParams(params);

        }
*/
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mMessageBox.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    String token = ((OurApplication)getActivity().getApplicationContext()).getUserToken();
                    LocationModel location = PreferenceEditor.getInstance(getContext()).getLocation();

                    CompleteFeedModel model ;
                    //new CompleteFeedModel("F", token, msg,location,Helper.getStringImage(mBitmap));
                    if(filetype=="image")
                    {
                        model = new CompleteFeedModel("F", token, msg, location, null,filetype);}
                    else
                    {
                        model = new CompleteFeedModel("F", token, msg, location, Helper.getStringImage(bmThumbnail),filetype);
                    }
                    Log.d(TAG, "Form feed message:" + model.toString());

                  //  mWebSocketClient.sendMessage(model.toString());
                    //postFeed(model);
                    Log.d(TAG, "filePath:" + filePath);
                    fileupload(filePath, model, pathtype, AbsolutefilePath, filetype);
                    mMessageBox.setText(null);


                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                showFileChooser();
            }
        });
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

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*, video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

       /* Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
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

    private File getOutputMediaFile(int type) {

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

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
         /*   final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            Log.d(TAG, "Image bitmap value is : " + bitmap);
            img.setImageBitmap(bitmap);
            //mBitmap=BITMAP_RESIZER(bitmap,200,200);
            mBitmap=Helper.scaleBitmap(bitmap);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
            String imageMessage = Helper.getStringImage(mBitmap);*/
            Log.d(TAG, "getPath1_preview:" +fileUri.getPath());
            AbsolutefilePath=fileUri.getPath();
            pathtype="path";
            filetype="image";
            ImageView iv = new ImageView(getContext());
            final RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.compose_lyt);
            ImageButton gallery = (ImageButton) getView().findViewById(R.id.gallery);
            iv.setImageResource(R.drawable.location);
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            Bitmap mBitmap=bitmap;
            Log.d(TAG, "Image bitmap value is : " + mBitmap);
            mBitmap = Helper.scaleBitmap(mBitmap);
            ByteArrayOutputStream bytes1 = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes1);
            img.setImageBitmap(mBitmap);
            img.setVisibility(View.VISIBLE);
            mBitmap = Helper.scaleBitmap(bitmap);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            String imageMessage = Helper.getStringImage(mBitmap);
            iv.setImageBitmap(mBitmap);
            layout.addView(iv);
            iv.setVisibility(View.VISIBLE);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            int height = ((display.getHeight()*40)/100);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(width, height);
            mMessageBox.setVisibility(View.VISIBLE);
            lp1.addRule(RelativeLayout.BELOW, mMessageBox.getId());
            lp1.addRule(RelativeLayout.ABOVE, gallery.getId());
            iv.setLayoutParams(lp1);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            msg_send_lyt.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMAGE_REQUEST:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getData() != null) {
                     filePath = data.getData();
                    ContentResolver cR = getContext().getContentResolver();
                    pathtype="uri";
                    String type=cR.getType(filePath);
                    ImageView iv = new ImageView(getContext());
                    final RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.compose_lyt);
                    ImageButton gallery = (ImageButton) getView().findViewById(R.id.gallery);
                    iv.setImageResource(R.drawable.location);
                    layout.addView(iv);
                    try {

                        if(type.toString().contains("image")) {
                            filetype="image";
                            Log.d(TAG, "image file path is:" + getContext().getFilesDir().getAbsolutePath() + "," +
                                    Environment.getDataDirectory() + "," + Environment.getExternalStorageState());
                            //  Log.d(TAG, "getRealPathFromURI_API19:" + getFileName(filePath));
                            Log.d(TAG, "getPath1:" + getFilePath(filePath,type));
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                            mBitmap = Helper.scaleBitmap(bitmap);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                            //img.setImageBitmap(mBitmap);
                            iv.setImageBitmap(mBitmap);
                            String imageMessage = Helper.getStringImage(mBitmap);
                            iv.setVisibility(View.VISIBLE);
                            Display display = getActivity().getWindowManager().getDefaultDisplay();
                            int width = display.getWidth();
                            int height = ((display.getHeight()*40)/100);
                            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(width, height);
                            lp1.addRule(RelativeLayout.BELOW, mMessageBox.getId());
                            lp1.addRule(RelativeLayout.ABOVE, gallery.getId());
                            iv.setLayoutParams(lp1);
                            iv.setVisibility(View.VISIBLE);
                            iv.setScaleType(ImageView.ScaleType.FIT_XY);
                            msg_send_lyt.setVisibility(View.VISIBLE);
                        }
                        else{
                            filetype="video";
                            Log.d(TAG, "video file path is:" + getContext().getFilesDir().getAbsolutePath() + "," +
                                    Environment.getDataDirectory() + "," + Environment.getExternalStorageState());
                            Log.d(TAG, "getPath1:" + getFilePath(filePath,type));
                            final VideoView videoView = new VideoView(getContext());
                            final RelativeLayout layout1 = (RelativeLayout) getView().findViewById(R.id.compose_lyt);
                            layout1.addView(videoView);
                            // Create a progressbar
                            pDialog = new ProgressDialog(getContext());
                            // Set progressbar title
                            pDialog.setTitle("Video is loading");
                            // Set progressbar message
                            pDialog.setMessage("Buffering...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(false);
                            // Show progressbar
                            pDialog.show();
                            //Creating MediaController
                            videoView.setVisibility(View.VISIBLE);
                            iv.setVisibility(View.INVISIBLE);
                            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
                            metaRetriver.setDataSource(getContext(), filePath);
                            bmThumbnail = metaRetriver.getFrameAtTime(2 * 1000000, MediaMetadataRetriever.OPTION_CLOSEST);
                            //bmThumbnail = ThumbnailUtils.createVideoThumbnail(getFilePath(filePath, "video"), MediaStore.Video.Thumbnails.MINI_KIND);
                            //ffmpeg -itsoffset -1 -i (getFilePath(filePath, "video") -vframes 1 -filter:v scale="280:-1"  bmThumbnail;
                            Log.d(TAG,"Video Thumbnail"+bmThumbnail);
                            try {
                                // Start the MediaController
                                MediaController mediacontroller = new MediaController(
                                        getContext());
                                mediacontroller.setAnchorView(videoView);
                                // Get the URL from String VideoURL
                                // Uri video = Uri.parse("http://ec2-54-254-164-222.ap-southeast-1.compute.amazonaws.com/video/testvideo.mp4");
                                videoView.setMediaController(mediacontroller);
                                //videoView.setVideoURI(video);
                                videoView.setVideoURI(filePath);

                            } catch (Exception e) {
                                Log.d(TAG,"Error"+ e.getMessage());
                                e.printStackTrace();
                            }

                            videoView.requestFocus();
                            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                // Close the progress bar and play the video
                                public void onPrepared(MediaPlayer mp) {
                                    pDialog.dismiss();
                                    videoView.requestFocus();
                                    videoView.setZOrderOnTop(false);
                                    videoView.seekTo(100);
                                    // videoView.start();
                                    // mp.setLooping(true);
                                }
                            });

                            videoView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    //  videoView.start();
                                    return false;
                                }
                            });

                            Display display = getActivity().getWindowManager().getDefaultDisplay();
                            int width = display.getWidth();
                            int height = ((display.getHeight()*40)/100);
                            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(width, height);
                            lp1.addRule(RelativeLayout.BELOW, mMessageBox.getId());
                            lp1.addRule(RelativeLayout.ABOVE, gallery.getId());
                            videoView.setLayoutParams(lp1);
                            videoView.setVisibility(View.VISIBLE);
                            msg_send_lyt.setVisibility(View.VISIBLE);
                        }
                        //Getting the Bitmap from Gallery
                      /*  Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                        mBitmap=Helper.scaleBitmap(bitmap);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                        img.setImageBitmap(mBitmap);
                        String imageMessage = Helper.getStringImage(mBitmap);
                        Log.d(TAG, "Image message value length : " + imageMessage.length());
                        Log.d(TAG, "Image message value is : " + imageMessage);*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                // if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // user cancelled Image capture
                    Toast.makeText(getContext().getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT).show();
                } else {
                    // failed to capture image
                    Toast.makeText(getContext().getApplicationContext(), "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }



   /* private void postFeed(CompleteFeedModel postModel){


        Call<ComposeRespModel> composeFeed = ((OurApplication)getActivity().getApplicationContext())
                .getRestApi().ComposeFeed(postModel);
        composeFeed.enqueue(new Callback<ComposeRespModel>() {
            @Override
            public void onResponse(Response<ComposeRespModel> response, Retrofit retrofit) {

                Boolean data = response.body().isSuccess();
                if (data) {

                    //UI.showSoftKeyboard(getActivity(),mMessageBox);


                    HomeActivity.bottomBar.selectTabAtPosition(0);

                    UI.closeKeyboard(getActivity(), mMessageBox.getWindowToken());


                } else {
                    Toast.makeText(getActivity(), "Message post failed", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Connection Failed " + t);
                Toast.makeText(getActivity(), "Connection Failed. Please Try again!", Toast.LENGTH_LONG).show();
            }
        });

    }*/

    public String getFilePath(Uri uri,String type) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        String path="";
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        if(type=="image")
        {
            cursor = getContext().getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        else if(type=="video")
        {
            cursor = getContext().getContentResolver().query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            cursor.close();
        }
        return path;
    }

    private void fileupload(Uri uri,CompleteFeedModel completeFeedModel,String pathtype,String AbsolutefilePath,String type) {
        Log.d(TAG, "pathtype==" + pathtype);
        String filePath="";
        if(pathtype== "uri")
        { filePath= getFilePath(uri,type);}
        else if(pathtype=="path")
        {filePath=AbsolutefilePath;
        }
        Log.d(TAG, "filePath:" + filePath);
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            // Change base URL to your upload server URL.
            FileUploadApi service = new Retrofit.Builder().baseUrl(ApiUrls.HTTP_URL).client(client).build().create(FileUploadApi.class);
            String descriptionString = "Sample description";
            if (file.exists()) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiUrls.HTTP_URL)

                        .build();

                RequestBody fileBody = RequestBody.create(MediaType.parse("video/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                RequestBody completefeedmodel = RequestBody.create(MediaType.parse("text/plain"), completeFeedModel.toString());
                Log.d(TAG, "RequestBody is " + fileBody);

                retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, name,completefeedmodel);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        HomeActivity.bottomBar.selectTabAtPosition(0);

                        UI.closeKeyboard(getActivity(), mMessageBox.getWindowToken());
                        Toast.makeText(getContext(), "File Upload Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getContext(), "File Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }
    }

}