package com.example.app.ourapplication;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import com.example.app.ourapplication.rest.model.request.HomeFeedReqModel;
import com.example.app.ourapplication.rest.model.request.ProfileFeedReqModel;
import com.example.app.ourapplication.rest.model.request.ProfileUpdateModel;
import com.example.app.ourapplication.rest.model.response.FeedRespModel;
import com.example.app.ourapplication.rest.model.response.Person;
import com.example.app.ourapplication.rest.model.response.ProfileRespModel;
import com.example.app.ourapplication.util.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

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

        //mFeeds = mDBHelper.getFeedDataAll();
        mFeedListAdapter = new FeedRVAdapter(getActivity(),mFeeds);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(mFeedListAdapter);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_profile);

        mUserId = PreferenceEditor.getInstance((getActivity().getApplicationContext())).getLoggedInUserName();

        Log.d(TAG, "User ID is" + mUserId);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.profile_collapse);
        collapsingToolbar.setTitle(mDBHelper.getProfileInfo(mUserId, 1));
        profileImgView = (ImageView) view.findViewById(R.id.image_profile);
        Log.d(TAG, "Image data : " + mDBHelper.getProfileInfo(mDBHelper.getProfileInfo(mUserId, 2), 2));
        profileImgView.setImageBitmap(Helper.decodeImageString(mDBHelper.getProfileInfo(mUserId, 2)));
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });


        getUpdatedFeeds();
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
                            if(!TextUtils.isEmpty(mUserId)) {
                                String imageProfileString = Helper.getStringImage(bitmapRef);
                                Log.d(TAG, "Image message value length : " + imageProfileString.length());
                                Log.d(TAG, "Image message value is : " + imageProfileString);
                                ProfileUpdateModel model = new ProfileUpdateModel(mUserId, Keys.KEY_PROFIMG, imageProfileString);
                                updateProfile(model);
                                mDBHelper.updateProfile(model.toString());
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

    private void updateProfile(ProfileUpdateModel reqModel){
        Call<ProfileRespModel> profileUpdater = ((OurApplication)getActivity().getApplicationContext()).getRestApi().updateProfile(reqModel);
        profileUpdater.enqueue(new Callback<ProfileRespModel>() {
            @Override
            public void onResponse(Response<ProfileRespModel> response, Retrofit retrofit) {
                if (response.body().isSuccess()) {
                    Log.d(TAG, response.body() + "Profile information Updated");
                    Snackbar.make(profileImgView, "Profile information Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(profileImgView, "Profile information not Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Log.d(TAG, response.body() + "Profile information not Updated");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Snackbar.make(profileImgView, "Profile information not Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Log.d(TAG, "Profile information not Updated");
            }
        });
    }


    private void getUpdatedFeeds(){
        ProfileFeedReqModel reqModel = new ProfileFeedReqModel(mUserId,"2020-12-31 12:00:00");

        Call<FeedRespModel> queryProfileFeeds = ((OurApplication)getActivity().getApplicationContext())
                .getRestApi().queryProfileFeed(reqModel);
        queryProfileFeeds.enqueue(new Callback<FeedRespModel>() {
            @Override
            public void onResponse(Response<FeedRespModel> response, Retrofit retrofit) {
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
            public void onFailure(Throwable t) {
                Log.d(TAG, "Query failed for the reson: " + t);
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