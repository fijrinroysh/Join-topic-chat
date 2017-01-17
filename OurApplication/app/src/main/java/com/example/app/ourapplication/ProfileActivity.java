package com.example.app.ourapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.app.ourapplication.database.DBHelper;
import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.model.request.ProfileUpdateModel;
import com.example.app.ourapplication.rest.model.response.ProfileRespModel;
import com.example.app.ourapplication.util.Helper;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by ROYSH on 8/8/2016.
 */
public class ProfileActivity extends AppCompatActivity{

    private final String TAG = ProfileActivity.class.getSimpleName();
    private static final int UPDATE_PIC = 1;
    private ImageView profileImgView;
    private DBHelper mDBHelper = new DBHelper(this);
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mUserId = PreferenceEditor.getInstance(this).getLoggedInUserName();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.profile_collapse);
        collapsingToolbar.setTitle(mDBHelper.getProfileInfo(mUserId,1));
        profileImgView = (ImageView) findViewById(R.id.image_profile);
        Log.d(TAG, "Image data : " + mDBHelper.getProfileInfo(mUserId,2));
        profileImgView.setImageBitmap(Helper.decodeImageString(mDBHelper.getProfileInfo(mUserId,2)));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case UPDATE_PIC:
                if (resultCode == RESULT_OK && data != null) {
                    // Bundle extras = data.getExtras();
                    Uri filePath = data.getData();
                    Log.d(TAG, "Data : " + filePath);
                    try {
                        // mBitmap = data.getParcelableExtra("data");
                        Bitmap bitmap  = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
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
        Call<ProfileRespModel> profileUpdater = ((OurApplication)getApplicationContext()).getRestApi().updateProfile(reqModel);
        profileUpdater.enqueue(new Callback<ProfileRespModel>() {
            @Override
            public void onResponse(Response<ProfileRespModel> response, Retrofit retrofit) {
                if(response.body().isSuccess()) {
                    Log.d(TAG, "Profile information Updated");
                    Snackbar.make(profileImgView, "Profile information Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }else{
                    Snackbar.make(profileImgView, "Profile information not Updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Log.d(TAG, "Profile information not Updated");
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
}