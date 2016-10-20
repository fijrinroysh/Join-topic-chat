package com.example.app.ourapplication;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.util.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static View view;
    private final String TAG = ProfileActivity.class.getSimpleName();
    public static Bitmap mBitmap;
    private String imageprofilestring;
    private ImageView profileImgView;
    public static final int UPDATE_PIC = 1;
    public DBHelper mDBHelper ;
    public static Activity activity;
    String userid ;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        view =  inflater.inflate(R.layout.activity_profile, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_profile);
        Context thiscontext=container.getContext();
        mDBHelper = new DBHelper(thiscontext);
        userid = PreferenceEditor.getInstance(thiscontext).getLoggedInUserName();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.profile_collapse);
        collapsingToolbar.setTitle("My Toolbar Tittle");
        profileImgView = (ImageView) view.findViewById(R.id.image_profile);
        Log.d(TAG, "Image data : " + mDBHelper.getProfileInfo(userid, 2));
        profileImgView.setImageBitmap(Helper.decodeImageString(mDBHelper.getProfileInfo(userid, 2)));

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                if (resultCode == getActivity().RESULT_OK && data != null) {
                    // Bundle extras = data.getExtras();
                    Uri filePath = data.getData();
                    Log.d(TAG, "Data : " + filePath);
                    try {
                        // mBitmap = data.getParcelableExtra("data");
                        Bitmap bitmap  = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                        if (bitmap != null) {
                            mBitmap=Helper.scaleBitmap(bitmap);
                            Log.d(TAG,"L : "+mBitmap.getWidth()+ "  : "+mBitmap.getScaledHeight(getResources().getDisplayMetrics()));
                            profileImgView.setImageBitmap(mBitmap);
                            imageprofilestring = Helper.getStringImage(mBitmap);
                            Log.d(TAG, "Image message value length : " + imageprofilestring.length());
                            Log.d(TAG, "Image message value is : " + imageprofilestring);
                            if(!TextUtils.isEmpty(userid)) {
                                String body = Helper.getUpdateProfileBody(userid, Keys.KEY_PROFIMG, imageprofilestring);
                                new ProfileUpdateTask().execute(body);
                                mDBHelper.updateProfile(body);

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

    private class ProfileUpdateTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // UI.showProgressDialog(HomeFeedActivity.this, getString(R.string.login_progress));
        }

        @Override
        protected String doInBackground(String... params) {
            String body = params[0];
            String response = null;
            try {
                response = Helper.updateProfile(body);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "Response : " + response);
            if(response != null){

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean(Keys.KEY_SUCCESS);

                    if(isSuccess) {

                        Log.d(TAG, "Profile information Updated");
                        Snackbar.make(profileImgView, "Profile information Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    }else{
                        Snackbar.make(profileImgView, "Profile information not Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        Log.d(TAG, "Profile information not Updated");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Log.d(TAG, "Update response is NULL");
            }
        }
    }



}
