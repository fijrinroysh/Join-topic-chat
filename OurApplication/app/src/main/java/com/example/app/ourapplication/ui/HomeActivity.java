package com.example.app.ourapplication.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.app.ourapplication.ComposeFragment;
import com.example.app.ourapplication.HomeFeedFragment;
import com.example.app.ourapplication.LocationFragment;
import com.example.app.ourapplication.ProfileFragment;
import com.example.app.ourapplication.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarumugam on 17/01/17.
 */
public class HomeActivity extends AppCompatActivity implements OnTabSelectListener, OnTabReselectListener {

    private final String TAG = HomeActivity.class.getSimpleName();

    private BottomBar bottomBar;
    private List<Fragment> fragments = new ArrayList<>(4);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //add fragments to list
        fragments.add(HomeFeedFragment.newInstance());
        fragments.add(LocationFragment.newInstance());
        fragments.add(ComposeFragment.newInstance());
        fragments.add(ProfileFragment.newInstance());

        //link fragments to container
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(this);
        bottomBar.setOnTabReselectListener(this);
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        int index = 0;
        switch (tabId) {
            case R.id.add_location:
                index = 1;
                bottomBar.setVisibility(View.VISIBLE);
                break;
            case R.id.add_message:
                index = 2;
                bottomBar.setVisibility(View.GONE);
                break;
            case R.id.add_profile:
                index = 3;
                bottomBar.setVisibility(View.VISIBLE);
                break;
            default:
                bottomBar.setVisibility(View.VISIBLE);
                break;
        }

        Log.d(TAG, "onTabSelected : " + tabId);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.contentContainer, fragments.get(index)).commit();
    }

    @Override
    public void onTabReSelected(@IdRes int tabId) {
        Log.d(TAG, "onTabReSelected : " + tabId);
    }

    @Override
    public void onBackPressed() {
        if ( bottomBar.getCurrentTabPosition()!=0) {
            bottomBar.selectTabAtPosition(0);
/*
            entry = fm.getBackStackEntryCount();
            Log.i(TAG, "BackStackEntryCount after set Tab:" + fm.getBackStackEntryCount() + "\"");
            Log.i(TAG, "Current Tab position after set Tab:" + Integer.toString(bottomBar.getCurrentTabPosition()) + "\"");
            Log.i(TAG, "Previous fragment after set Tab:" + fm.getBackStackEntryAt(entry - 2).getName()+"\"");
            Log.i(TAG, "Are they equal after set Tab:" + (Integer.toString(bottomBar.getCurrentTabPosition()).equals(fm.getBackStackEntryAt(entry - 2).getName())));
*/
        } else {
            moveTaskToBack(true);
        }
    }
}