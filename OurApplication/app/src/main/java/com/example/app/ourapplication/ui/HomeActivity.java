package com.example.app.ourapplication.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.app.ourapplication.ComposeFragment;
import com.example.app.ourapplication.HomeFeedFragment;
import com.example.app.ourapplication.LocationFragment;
import com.example.app.ourapplication.ProfileActivity;
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

    public void onSenderPhotoClick(View v) {

          /* Below code is to open the Profile of the sender but now it opens the profile of the user */

        final Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
       // profileIntent.putExtra(Keys.KEY_ID, item.mPostId);
        v.getContext().startActivity(profileIntent);

        /* Below code is to open the Sender Photo onclick */

        /*ImageView senderPhoto = (ImageView) v.findViewById(R.id.sender_photo);
        BitmapDrawable imagedrawable = (BitmapDrawable) senderPhoto.getDrawable();
        Bitmap imagebitmap = imagedrawable.getBitmap();
        Dialog builder = new Dialog(v.getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(v.getContext());
        imageView.setImageBitmap(imagebitmap) ;
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();*/
    }

    public void onMessagePhotoClick(View v) {
        ImageView messagePhoto = (ImageView) v.findViewById(R.id.message_photo);
        BitmapDrawable imagedrawable = (BitmapDrawable) messagePhoto.getDrawable();
        Bitmap imagebitmap = imagedrawable.getBitmap();
        Dialog builder = new Dialog(v.getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(v.getContext());
        imageView.setImageBitmap(imagebitmap) ;
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
}