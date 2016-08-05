package com.example.app.ourapplication;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.app.ourapplication.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ROYSH on 8/3/2016.
 */
public class DiscussionActivity extends AppCompatActivity{

    private List<Person> mFeeds = new ArrayList<>();
    private RVAdapter mFeedListAdapter;
    RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        mFeedListAdapter = new RVAdapter(mFeeds);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mFeedListAdapter);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Person msg_type = new Person(extras.getString(Keys.KEY_NAME),extras.getString(Keys.KEY_MESSAGE),R.drawable.mickey,extras.getString(Keys.KEY_IMAGE));
            mFeedListAdapter.mFeeds.add(msg_type);
        }


    }
}
