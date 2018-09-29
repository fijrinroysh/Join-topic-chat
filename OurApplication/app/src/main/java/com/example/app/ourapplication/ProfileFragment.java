package com.example.app.ourapplication;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app.ourapplication.database.DBHelper;
import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.ApiUrls;
import com.example.app.ourapplication.rest.api.FileUploadApi;
import com.example.app.ourapplication.rest.model.request.ProfileFeedReqModel;
import com.example.app.ourapplication.rest.model.request.ProfileUpdateModel;
import com.example.app.ourapplication.rest.model.response.CompleteFeedModel;
import com.example.app.ourapplication.rest.model.response.Person;
import com.example.app.ourapplication.rest.model.response.ProfileRespModel;
import com.example.app.ourapplication.rest.model.response.SuccessRespModel;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.util.UI;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private final String TAG = ProfileActivity.class.getSimpleName();
    private List<Person> mFeeds = new ArrayList<>();
    private FeedRVAdapter mFeedListAdapter;
    private DBHelper mDBHelper ;
    private OnFragmentInteractionListener mListener;
    private ImageView profileImgView;
    public String mUserId;
    private static final int UPDATE_PIC = 1;
    private RecyclerView recyclerView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBHelper = new DBHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userId = PreferenceEditor.getInstance(getContext().getApplicationContext()).getLoggedInUserName();
        String ImageURL = ApiUrls.HTTP_URL +"/file_download/Pictures/"+userId+".jpg";
        //mFeeds = mDBHelper.getFeedDataAll();

        mFeedListAdapter = new FeedRVAdapter(getActivity(),mFeeds);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mFeedListAdapter);

        mUserId = PreferenceEditor.getInstance((getActivity().getApplicationContext())).getLoggedInUserName();

        Log.d(TAG, "User ID is" + mUserId);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_profile);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.profile_collapse);
        collapsingToolbar.setTitle(" ");
        profileImgView = (ImageView) view.findViewById(R.id.image_profile);
//        Picasso.with((getActivity().getApplicationContext())).load(ImageURL).into(profileImgView);
    
        Picasso.with((getActivity().getApplicationContext())).load(ImageURL)
                .placeholder(R.drawable.mickey)
                .error(R.drawable.mickey)
                .into(profileImgView);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        //Log.d(TAG, "Image data : " + mDBHelper.getProfileInfo(mDBHelper.getProfileInfo(mUserId, 2), 2));




        getUpdatedFeeds();
    }



    @Override
    public void onPause() {
        super.onPause();

        recyclerView.setAdapter(null);


    }

    @Override
    public void onResume() {
        super.onPause();
        recyclerView.setAdapter(mFeedListAdapter);
    }


    private void showFileChooser() {
        //Intent intent = new Intent();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        try {
            //startActivityForResult(Intent.createChooser(intent,"Complete action using"), UPDATE_PIC);
            startActivityForResult(intent, UPDATE_PIC);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(profileImgView, "Activity not found", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case UPDATE_PIC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Bundle extras = data.getExtras();
                    Uri filePath = data.getData();
                    Log.d(TAG, "Data : " + filePath);
                    try {
                        // mBitmap = data.getParcelableExtra("data");
                        Bitmap bitmap  = MediaStore.Images.Media.getBitmap((getActivity().getApplicationContext()).getContentResolver(), filePath);
                        if (bitmap != null) {
                            Bitmap bitmapRef = Helper.scaleBitmap(bitmap);
                            Log.d(TAG,"L : "+bitmapRef.getWidth()+ "  : "+bitmapRef.getScaledHeight(getResources().getDisplayMetrics()));
                            profileImgView.setImageBitmap(bitmapRef);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmapRef.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                            if(!TextUtils.isEmpty(mUserId)) {
                               // String imageProfileString = Helper.getStringImage(bitmapRef);
                               // Log.d(TAG, "Image message value length : " + imageProfileString.length());
                               // Log.d(TAG, "Image message value is : " + imageProfileString);
                                ProfileUpdateModel model = new ProfileUpdateModel(mUserId, Keys.KEY_PROFIMG, imageProfileString);
                                updateProfile(model);
                             //   mDBHelper.updateProfile(model.toString());
                            }
                        }else{
                            Snackbar.make(profileImgView, "Bitmap is null", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            Log.d(TAG, "Bitmap is null");}
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    public String getFilePath(Uri uri,String type) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        String path="";
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        if(type.equals("image")) {
            cursor = getContext().getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        else if(type.equals("video")) {
            cursor = getContext().getContentResolver().query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            cursor.close();
        }
        return path;
    }

    private void fileupload(Uri uri,CompleteFeedModel completeFeedModel,String filepathtype,String AbsolutefilePath,String type) {
        Log.d(TAG, "pathtype==" + filepathtype);
        String DevicefilePath="";
        if(filepathtype != null && !filepathtype.isEmpty() && filepathtype.equals("uri")) {
            DevicefilePath= getFilePath(uri,type);
        } else if(filepathtype != null && !filepathtype.isEmpty() && filepathtype.equals("path")) {
            DevicefilePath=AbsolutefilePath;
        }
        Log.d(TAG, "filePath:" + DevicefilePath);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        RequestBody completefeedmodel = RequestBody.create(MediaType.parse("text/plain"), completeFeedModel.toString());
        // Change base URL to your upload server URL.
        FileUploadApi service = new Retrofit.Builder().baseUrl(ApiUrls.HTTP_URL).client(client).build().create(FileUploadApi.class);
        String descriptionString = "Sample description";

        if (DevicefilePath != null && !DevicefilePath.isEmpty()) {
            File file = new File(DevicefilePath);
            if (file.exists()) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiUrls.HTTP_URL)
                        .build();

                RequestBody fileBody = RequestBody.create(MediaType.parse("video/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                Log.d(TAG, "RequestBody is " + fileBody);

                retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, name,completefeedmodel);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

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

    private void updateProfile(ProfileUpdateModel reqModel){
        Call<ProfileRespModel> profileUpdater = ((OurApplication)getActivity().getApplicationContext()).getRestApi().
                updateProfile(reqModel);
        profileUpdater.enqueue(new Callback<ProfileRespModel>() {
            @Override
            public void onResponse(Call<ProfileRespModel> call, Response<ProfileRespModel> response) {
                if (response.body().isSuccess()) {
                    Log.d(TAG, response.body() + "Profile information Updated");
                    Snackbar.make(profileImgView, "Profile information Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(profileImgView, "Profile information not Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Log.d(TAG, response.body() + "Profile information not Updated");
                }
            }

            @Override
            public void onFailure(Call<ProfileRespModel> call,Throwable t) {
                t.printStackTrace();
                Snackbar.make(profileImgView, "Profile information not Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Log.d(TAG, "Profile information not Updated");
            }
        });
    }


    private void getUpdatedFeeds(){
        ProfileFeedReqModel reqModel = new ProfileFeedReqModel(mUserId,"2020-12-31 12:00:00");

        Call<SuccessRespModel> queryProfileFeeds = ((OurApplication)getActivity().getApplicationContext())
                .getRestApi().queryProfileFeed(reqModel);
        queryProfileFeeds.enqueue(new Callback<SuccessRespModel>() {
            @Override
            public void onResponse(Call<SuccessRespModel> call,Response<SuccessRespModel> response) {
                if (response.body().isSuccess()) {
                    ArrayList<Person> data = response.body().getData();

                    if(data.size() > 0) {
                        for (int i = 0; i < data.size(); i++) {

                            mFeeds.add(0, data.get(i));
                            mFeedListAdapter.notifyDataSetChanged();
                        }
                    }

                    Toast.makeText(getActivity(), "No more Feeds to Load", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SuccessRespModel> call,Throwable t) {
                Log.d(TAG, "Query failed for the reason: " + t);
                Toast.makeText(getActivity(), "Loading Feeds Failed", Toast.LENGTH_LONG).show();
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
       /* if (context instanceof OnFragmentInteractionListener) {
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
}