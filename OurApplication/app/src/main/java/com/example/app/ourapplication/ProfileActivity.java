package com.example.app.ourapplication;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.example.app.ourapplication.database.DBHelper;
import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.ApiUrls;
import com.example.app.ourapplication.rest.model.request.ProfileFeedReqModel;
import com.example.app.ourapplication.rest.model.request.SubscribeReqModel;
import com.example.app.ourapplication.rest.model.response.Person;
import com.example.app.ourapplication.rest.model.response.Subscriber;
import com.example.app.ourapplication.rest.model.response.SubscriberDataRespModel;
import com.example.app.ourapplication.rest.model.response.SuccessRespModel;
import com.squareup.picasso.Picasso;
import com.example.app.ourapplication.util.Helper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private FloatingActionButton fabsubscribe;
    private FloatingActionButton fabunsubscribe;
    private String token;
   // private String token = ((OurApplication)getActivity().getApplicationContext()).getUserToken();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        fabsubscribe= (FloatingActionButton) findViewById(R.id.fabsubscribe);
        fabunsubscribe= (FloatingActionButton) findViewById(R.id.fabunsubscribe);
       /* mDBHelper.RefreshUserSubscription();
        getSubscribers();*/
        final String userid =  PreferenceEditor.getInstance(getApplicationContext()).getLoggedInUserName();
         token = ((OurApplication)getApplicationContext()).getUserToken();
        mPostId = getIntent().getStringExtra(Keys.KEY_ID);
        Log.d(TAG, "Post ID : " + mPostId);
        mUserId = mDBHelper.getFeedDataColumn(mPostId,1);
        String ImageURL = ApiUrls.HTTP_URL +"/images/"+mUserId+".jpg";

        String sid=mDBHelper.getUserSubscription(mUserId, userid);
        Log.d(TAG, "S ID : " + sid);

        if(sid!=null && sid.equals("nosubscription")) {fabsubscribe.setVisibility(View.VISIBLE);
            fabunsubscribe.setVisibility(View.INVISIBLE);}
        else {fabsubscribe.setVisibility(View.INVISIBLE);
            fabunsubscribe.setVisibility(View.VISIBLE);}

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
        getSubscribers();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBHelper.RefreshUserSubscription();
        //getSubscribers();
    }

    private void getUpdatedFeeds(){
        ProfileFeedReqModel reqModel = new ProfileFeedReqModel(mUserId,"2020-12-31 12:00:00");

        Call<SuccessRespModel> queryProfileFeeds = ((OurApplication)getApplicationContext())
                .getRestApi().queryProfileFeed(reqModel);
        queryProfileFeeds.enqueue(new Callback<SuccessRespModel>() {
            @Override
            public void onResponse(Call<SuccessRespModel> call, Response<SuccessRespModel> response) {
                if (response.body().isSuccess()) {
                    ArrayList<Person> data = response.body().getData();

                    if (data.size() > 0) {
                        for (int i = 0; i < data.size(); i++) {

                            mFeeds.add(0, data.get(i));
                            mFeedListAdapter.notifyDataSetChanged();
                        }
                    }

                    Toast.makeText(getApplicationContext(), "No more Feeds to Load", Toast.LENGTH_LONG).show();
                }

                fabsubscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Helper.PostSubscription(v,token, mFeeds.get(0).getUserId(), "Y");
                        fabsubscribe.setVisibility(View.INVISIBLE);
                        fabunsubscribe.setVisibility(View.VISIBLE);
                    }

                });

                fabunsubscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Helper.PostSubscription(v, token,mFeeds.get(0).getUserId(), "N");
                        fabsubscribe.setVisibility(View.VISIBLE);
                        fabunsubscribe.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Call<SuccessRespModel> call, Throwable t) {
                Log.d(TAG, "Query failed for the reson: " + t);
                Toast.makeText(getApplicationContext(), "Loading Feeds Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getSubscribers(){

        Call<SubscriberDataRespModel> querySubscriberData = ((OurApplication) getApplicationContext())
                .getRestApi().querySubscriberData();
        querySubscriberData.enqueue(new Callback<SubscriberDataRespModel>() {
            @Override
            public void onResponse(Call<SubscriberDataRespModel> call, Response<SubscriberDataRespModel> response) {

                ArrayList<Subscriber> data = response.body().getData();
                if (data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        mDBHelper.insertSubscriberData(data.get(i));
                        Log.d(TAG, "insertSubscriberData :" + data.get(i));
                    }
                }
            }

            @Override
            public void onFailure(Call<SubscriberDataRespModel> call, Throwable t) {
                Log.d(TAG, "Query failed: " + t);
                Toast.makeText(getApplicationContext(), "Getting Subscriber Data Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

}