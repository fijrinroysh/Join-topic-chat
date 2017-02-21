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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app.ourapplication.database.DBHelper;
import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.ApiUrls;
import com.example.app.ourapplication.rest.model.request.ProfileFeedReqModel;
import com.example.app.ourapplication.rest.model.request.ProfileUpdateModel;
import com.example.app.ourapplication.rest.model.response.FeedRespModel;
import com.example.app.ourapplication.rest.model.response.Person;
import com.example.app.ourapplication.rest.model.response.ProfileRespModel;
import com.example.app.ourapplication.util.Helper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by ROYSH on 8/8/2016.
 */
public class ProfileActivity extends AppCompatActivity{

    private final String TAG = ProfileActivity.class.getSimpleName();
    private List<Person> mFeeds = new ArrayList<>();
    private FeedRVAdapter mFeedListAdapter;
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
        mUserId = mDBHelper.getFeedDataColumn(mPostId,1);
        String ImageURL = ApiUrls.HTTP_URL +"/images/"+mUserId+".jpg";


        mFeedListAdapter = new FeedRVAdapter(getApplicationContext(),mFeeds);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext().getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mFeedListAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.profile_collapse);
        collapsingToolbar.setTitle(mDBHelper.getFeedDataColumn(mPostId, 2));
        Log.d(TAG, "Title : " + mDBHelper.getFeedDataColumn(mPostId, 2));
        profileImgView = (ImageView) findViewById(R.id.image_profile);
        Log.d(TAG, "Image data : " + mDBHelper.getFeedDataColumn(mPostId, 4));
       // profileImgView.setImageBitmap(Helper.decodeImageString(mDBHelper.getFeedDataColumn(mPostId,4)));

        Picasso.with((getApplicationContext())).load(ImageURL).into(profileImgView);
        getUpdatedFeeds();

    }



    private void getUpdatedFeeds(){
        ProfileFeedReqModel reqModel = new ProfileFeedReqModel(mUserId,"2020-12-31 12:00:00");

        Call<FeedRespModel> queryProfileFeeds = ((OurApplication)getApplicationContext())
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

                    Toast.makeText(getApplicationContext(), "No more Feeds to Load", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Query failed for the reson: " + t);
                Toast.makeText(getApplicationContext(), "Loading Feeds Failed", Toast.LENGTH_LONG).show();
            }
        });
    }


}