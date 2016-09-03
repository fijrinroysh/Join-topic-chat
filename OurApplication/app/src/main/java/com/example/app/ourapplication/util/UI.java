package com.example.app.ourapplication.util;

import android.app.ProgressDialog;
import android.content.Context;

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


}
