package com.example.app.ourapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.app.ourapplication.pref.PreferenceEditor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by ROYSH on 6/8/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = DBHelper.class.getSimpleName();
    public static final String MESSAGE_ID_COLUMN_NAME = "POSTID";
    public static final String MESSAGE_COLUMN_NAME = "MESSAGE";
    public static final String MESSAGE_FROM_COLUMN_NAME = "MESSAGE_FROM";
    public static final String MESSAGE_TO_COLUMN_NAME = "MESSAGE_TO";
    public static final String MESSAGE_IMAGE_COLUMN_NAME = "MESSAGE_IMAGE";
    public static final String MESSAGE_TIME_COLUMN_NAME = "MESSAGETIME";
    public static final String PROFILE_IMAGE_COLUMN_NAME = "PROFILEIMAGE";
    public static final String PROFILE_USER_COLUMN_NAME = "PROFILEUSER";
    public static final String PROFILE_ID_COLUMN_NAME = "PROFILEID";


    SQLiteDatabase mydatabase;


    public DBHelper(Context context)
    {
        super(context, "FEED" , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase mydatabase) {
        // TODO Auto-generated method stub
        mydatabase.execSQL(
                "create table MESSAGE_DATA ("+MESSAGE_ID_COLUMN_NAME+" VARCHAR,"+
                        MESSAGE_FROM_COLUMN_NAME+" VARCHAR,"+
                        MESSAGE_TO_COLUMN_NAME+" VARCHAR,"+
                        MESSAGE_COLUMN_NAME+" VARCHAR,"+
                        MESSAGE_IMAGE_COLUMN_NAME+" VARCHAR,"+
                        MESSAGE_TIME_COLUMN_NAME+" VARCHAR)"

        );

        mydatabase.execSQL(
                "create table PROFILE_DATA (" + PROFILE_ID_COLUMN_NAME + " VARCHAR PRIMARY KEY," +
                        PROFILE_USER_COLUMN_NAME + " VARCHAR ," +
                        PROFILE_IMAGE_COLUMN_NAME + " VARCHAR )"
                //"CREATE TABLE IF NOT EXISTS DATA(FROM VARCHAR,TO VARCHAR,MESSAGE VARCHAR );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase mydatabase, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        mydatabase.execSQL("DROP TABLE IF EXISTS MESSAGE_DATA");
        mydatabase.execSQL("DROP TABLE IF EXISTS PROFILE_DATA");
        onCreate(mydatabase);
    }

    public boolean insertData (String message) {
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_ID_COLUMN_NAME, msgObject.optString(Keys.KEY_ID));
        contentValues.put(MESSAGE_FROM_COLUMN_NAME, msgObject.optString(Keys.KEY_NAME));
        contentValues.put(MESSAGE_TO_COLUMN_NAME, msgObject.optString(Keys.KEY_TO));
        contentValues.put(MESSAGE_COLUMN_NAME, msgObject.optString(Keys.KEY_MESSAGE));
        contentValues.put(MESSAGE_IMAGE_COLUMN_NAME, msgObject.optString(Keys.KEY_IMAGE));
        contentValues.put(MESSAGE_TIME_COLUMN_NAME, msgObject.optString(Keys.KEY_TIME));
        mydatabase.insert("MESSAGE_DATA", null, contentValues);
        return true;
    }

    public boolean insertProfile (String profile) {
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(profile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_ID_COLUMN_NAME, msgObject.optString("phonenumber"));
        contentValues.put(PROFILE_USER_COLUMN_NAME, msgObject.optString("username"));
        contentValues.put(PROFILE_IMAGE_COLUMN_NAME, msgObject.optString(Keys.KEY_PROFIMG));
        mydatabase.insertWithOnConflict("PROFILE_DATA", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public boolean updateProfile (String profile) {
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(profile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(PROFILE_USER_COLUMN_NAME, msgObject.optString(Keys.KEY_NAME));
        contentValues.put(msgObject.optString("columnname"), msgObject.optString("columndata"));
        mydatabase.update("PROFILE_DATA", contentValues, PROFILE_USER_COLUMN_NAME + "= \"" + msgObject.optString(Keys.KEY_NAME) + "\" ", null);

        return true;
    }


    public ArrayList<Person> getData(String id)
    {
        ArrayList<Person> array_list = new ArrayList<Person>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor msg_res =  db.rawQuery( "select * from MESSAGE_DATA where " +MESSAGE_FROM_COLUMN_NAME+ " = \"" + id + "\" or " +MESSAGE_TO_COLUMN_NAME+ " = \""+id+"\" ORDER BY "+ MESSAGE_TIME_COLUMN_NAME+" DESC", null );
        //Cursor res =  db.rawQuery( "select * from MESSAGE_DATA" , null );
        msg_res.moveToFirst();

        while(msg_res.isAfterLast() == false){
            //String column0 = msg_res.getString(0);
            String column1 = getProfileInfo(msg_res.getString(1),1);
            String column2 = getProfileInfo(msg_res.getString(2),1);
            String column3 = msg_res.getString(3);
            String column4 = msg_res.getString(4);
            String column5 = getProfileInfo(msg_res.getString(1), 2);
            String column6 = msg_res.getString(5);
             //String msg = column4.substring(0, column4.length() - 1);
             // String message = "Message from "+column1 +" to "+ column2 +" : "+ column3;
             // Private ImageView img;
             //  img  = (ImageView) findViewById(R.id.img);

            array_list.add(new Person("Message from "+column1 +" to "+ column2 , column3, column5, column4, column6  ));
            msg_res.moveToNext();
        }
        return array_list;
    }

    public String getProfileInfo(String id,Integer columnnumber) {
        String columndata;
        SQLiteDatabase db = this.getReadableDatabase();

        //String Query = "SELECT * FROM PROFILE_DATA WHERE PROFILE_USER = 'Fiji' ";
        Cursor prof_res = db.rawQuery("SELECT * FROM PROFILE_DATA WHERE " + PROFILE_ID_COLUMN_NAME + " = \"" + id + "\"", null);
        Log.d(TAG, "getProfileInfoCount: " + prof_res.getCount());
        Log.d(TAG, "getProfileInfoID: " + id);

        if (prof_res.getCount() != 0){
            prof_res.moveToFirst();
            columndata = prof_res.getString(columnnumber);
            Log.d(TAG, "getProfileInfo: " + columndata);}
        else{
            columndata="noimage";
        }

        return columndata;
    }

    public String getProfileId(String id) {
        String columndata;
        SQLiteDatabase db = this.getReadableDatabase();

        //String Query = "SELECT * FROM PROFILE_DATA WHERE PROFILE_USER = 'Fiji' ";
        Cursor prof_res = db.rawQuery("SELECT * FROM PROFILE_DATA WHERE " + PROFILE_USER_COLUMN_NAME + " = \"" + id + "\"", null);
        Log.d(TAG, "getProfileIdCount: " + prof_res.getCount());
        Log.d(TAG, "getProfileId ID: " + id);

        if (prof_res.getCount() != 0){
            prof_res.moveToFirst();
            columndata = prof_res.getString(0);
            Log.d(TAG, "getProfileIDInfo: " + columndata);}
        else{
            columndata="no id";
        }

        return columndata;
    }


}




