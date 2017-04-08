package com.example.app.ourapplication.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by sarumugam on 16/07/16.
 */
public class UI {

    private static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context,String message) {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public static void dismissProgress(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public static void showSoftKeyboard(Activity activity, View v) {
        InputMethodManager imgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        v.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        imgr.showSoftInput(v,InputMethodManager.SHOW_FORCED);

    }


    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}