package com.example.app.ourapplication.pref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sarumugam on 21/06/16.
 */
public class PreferenceEditor {

    private static PreferenceEditor mEditor;
    private SharedPreferences mSharedPrefs;

    public static PreferenceEditor getInstance(Context context){
        if(mEditor == null){
            mEditor = new PreferenceEditor(context);

        }
        return mEditor;
    }

    public PreferenceEditor(Context context){
        mSharedPrefs = context.getSharedPreferences(PrefKeys.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getLoggedInUserName() {
        return mSharedPrefs.getString(PrefKeys.LOGGED_IN_USER_NAME, null);
    }

    public void setLoggedInUserName(String loginId) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(PrefKeys.LOGGED_IN_USER_NAME, loginId);
        editor.apply();
    }
}
