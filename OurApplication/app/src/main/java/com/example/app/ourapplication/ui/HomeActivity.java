package com.example.app.ourapplication.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.app.ourapplication.ComposeFragment;
import com.example.app.ourapplication.HomeFeedFragment;
import com.example.app.ourapplication.LocationFragment;
import com.example.app.ourapplication.ProfileFragment;
import com.example.app.ourapplication.R;
import com.ncapdevi.fragnav.FragNavController;
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
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;
    private final int TAB_FOURTH = FragNavController.TAB4;

    private FragNavController mBottomBarController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        List<Fragment> fragments = new ArrayList<>(4);

        //add fragments to list
        fragments.add(HomeFeedFragment.newInstance());
        fragments.add(LocationFragment.newInstance());
        fragments.add(ComposeFragment.newInstance());
        fragments.add(ProfileFragment.newInstance());

        //link fragments to container
        mBottomBarController = new FragNavController(getSupportFragmentManager(),R.id.contentContainer,fragments);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(this);
        bottomBar.setOnTabReselectListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mBottomBarController.getCurrentStack().size() > 1) {
            mBottomBarController.pop();
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        switch (tabId) {
            case R.id.add_location:
                mBottomBarController.switchTab(TAB_SECOND);
                break;
            case R.id.add_message:
                mBottomBarController.switchTab(TAB_THIRD);
                break;
            case R.id.add_profile:
                mBottomBarController.switchTab(TAB_FOURTH);
                break;
            default:
                mBottomBarController.switchTab(TAB_FIRST);
        }
    }

    @Override
    public void onTabReSelected(@IdRes int tabId) {
        Log.d(TAG,"onTabReSelected : "+tabId);
    }
}