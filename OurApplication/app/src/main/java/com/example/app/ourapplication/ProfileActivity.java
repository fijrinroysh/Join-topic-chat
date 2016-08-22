package com.example.app.ourapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.util.UI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ROYSH on 8/8/2016.
 */
public class ProfileActivity extends AppCompatActivity{

    private final String TAG = ProfileActivity.class.getSimpleName();
    public static Bitmap mBitmap;
    private String imageprofilestring;
    ImageView profileImgView;
    public static final int UPDATE_PIC = 1;
    public DBHelper mDBHelper = new DBHelper(this);
    String userid = PreferenceEditor.getInstance(this).getLoggedInUserName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
            setSupportActionBar(toolbar);
            profileImgView = (ImageView) findViewById(R.id.image_profile);
            Log.d(TAG, "Image data : " + mDBHelper.getProfileInfo(userid,2));
            profileImgView.setImageBitmap(Helper.decodeImageString(mDBHelper.getProfileInfo(userid,2)));

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    showFileChooser();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), UPDATE_PIC );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case UPDATE_PIC:

        if (resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImgView.setImageBitmap(mBitmap);
                imageprofilestring = Helper.getStringImage(mBitmap);
                Log.d(TAG, "Image message value length : " + imageprofilestring.length());
                Log.d(TAG, "Image message value is : " + imageprofilestring);

                if(!TextUtils.isEmpty(userid)) {
                    String body = Helper.getUpdateProfileBody(userid, Keys.KEY_PROFIMG, imageprofilestring);
                    new ProfileUpdateTask().execute(body);
                    mDBHelper.updateProfile(body);
                }


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

                }else{
                  //  Snackbar.make(view, "Profile information Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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