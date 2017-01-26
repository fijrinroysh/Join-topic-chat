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

    private ImageView profileImgView;
    private DBHelper mDBHelper = new DBHelper(this);
    private String mUserId;
    private String mPostId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mPostId = getIntent().getStringExtra(Keys.KEY_ID);
        Log.d(TAG, "Post ID : " + mPostId);

        mUserId = mDBHelper.getFeedDataColumn(mPostId,2);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.profile_collapse);
        collapsingToolbar.setTitle(mDBHelper.getFeedDataColumn(mPostId, 1));
        Log.d(TAG, "Title : " + mDBHelper.getFeedDataColumn(mPostId, 1));
        profileImgView = (ImageView) findViewById(R.id.image_profile);
        Log.d(TAG, "Image data : " + mDBHelper.getFeedDataColumn(mPostId,4));
        profileImgView.setImageBitmap(Helper.decodeImageString(mDBHelper.getFeedDataColumn(mPostId,4)));


    }


}