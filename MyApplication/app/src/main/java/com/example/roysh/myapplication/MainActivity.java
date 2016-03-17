package com.example.roysh.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
   
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Starting point for our App, lets start our trials here.
 */
public class MainActivity extends AppCompatActivity {

    private ProgressDialog mLoginProgressDlg;

    ViewPager viewpgr;
    TabHost host;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.roysh.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.roysh.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class TabFactory implements TabHost.TabContentFactory {

        private View mView = null;

        public TabFactory(View mView) {
            this.mView = mView;
        }

        @Override
        public View createTabContent(String tag) {
            return mView;
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        host = (TabHost) findViewById(android.R.id.tabhost);
        viewpgr = (ViewPager) findViewById(R.id.pager);
        List<Fragment> fragments = new ArrayList();
        fragments.add(fragment.newInstance("NAME1"));
        fragments.add(fragment2.newInstance("NAME2"));
        fragments.add(fragment3.newInstance("NAME3"));
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        viewpgr.setAdapter(myPagerAdapter);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec1 = host.newTabSpec("Tab One");
        spec1.setContent(new TabFactory(findViewById(R.id.pager)));

        spec1.setIndicator("SIGN IN");
        host.addTab(spec1);

        //Tab 2
        TabHost.TabSpec spec2 = host.newTabSpec("Tab Two");
        spec2 = host.newTabSpec("Tab Two");
        spec2.setContent(new TabFactory(findViewById(R.id.pager)));
        spec2.setIndicator("FEEDS");
        host.addTab(spec2);

        //Tab 3
        TabHost.TabSpec spec3 = host.newTabSpec("Tab Three");
        spec3 = host.newTabSpec("Tab Three");
        spec3.setContent(new TabFactory(findViewById(R.id.pager)));
        spec3.setIndicator("SIGN UP");
        host.addTab(spec3);
        host.setCurrentTab(1);
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int pos = host.getCurrentTab();
                viewpgr.setCurrentItem(pos);

            }
        });

        viewpgr.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // on changing the page
               // make respected tab selected
               // position = viewpgr.getCurrentItem();
                host.setCurrentTab(position);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    /**
     * Description : The Adapter for the ViewPager which extends the FragmentPagerAdapter
     */
    /**
     * Description : The Adapter for the ViewPager which extends the FragmentPagerAdapter
     */
    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments = null;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public int getItemPosition(Object object) {
            int position = mFragments.indexOf((Fragment) object);

            if (position >= 0) {
                return position;
            } else {
                return POSITION_NONE;
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}




