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
    public static final String MESSAGE_ID_COLUMN = "POSTID";
    public static final String MESSAGE_COLUMN = "MESSAGE";
    public static final String MESSAGE_USER_NAME_COLUMN = "USER_NAME";
    public static final String MESSAGE_USER_ID_COLUMN = "USER_ID";
    public static final String MESSAGE_IMAGE_COLUMN = "MESSAGE_IMAGE";
    public static final String MESSAGE_TIME_COLUMN = "MESSAGETIME";
    public static final String MESSAGE_LIKES_COLUMN = "LIKES";
    public static final String PROFILE_IMAGE_COLUMN = "PROFILEIMAGE";
    public static final String PROFILE_USER_COLUMN = "PROFILEUSER";
    public static final String PROFILE_ID_COLUMN = "PROFILEID";
    public static final String MESSAGE_PROTOCOL_COLUMN = "PROTOCOL";



    SQLiteDatabase mydatabase;


    public DBHelper(Context context)
    {
        super(context, "FEED" , null, 9); //9 is the database version
    }

    @Override
    public void onCreate(SQLiteDatabase mydatabase) {
        // TODO Auto-generated method stub
        mydatabase.execSQL(
                "create table MESSAGE_DATA ("+MESSAGE_ID_COLUMN+" VARCHAR,"+
                        MESSAGE_USER_NAME_COLUMN+" VARCHAR,"+
                        MESSAGE_USER_ID_COLUMN +" VARCHAR,"+
                        MESSAGE_COLUMN+" VARCHAR,"+
                        PROFILE_IMAGE_COLUMN +" VARCHAR,"+
                        MESSAGE_IMAGE_COLUMN+" VARCHAR,"+
                        MESSAGE_TIME_COLUMN+" VARCHAR,"+
                        MESSAGE_PROTOCOL_COLUMN+" VARCHAR)"

        );

        mydatabase.execSQL(
                "create table COMMENT_DATA ("+MESSAGE_ID_COLUMN+" VARCHAR,"+
                        MESSAGE_USER_NAME_COLUMN+" VARCHAR,"+
                        MESSAGE_USER_ID_COLUMN+" VARCHAR,"+
                        MESSAGE_COLUMN+" VARCHAR,"+
                        PROFILE_IMAGE_COLUMN +" VARCHAR,"+
                        MESSAGE_LIKES_COLUMN+" VARCHAR,"+
                        MESSAGE_TIME_COLUMN+" VARCHAR)"

        );

        mydatabase.execSQL(
                "create table PROFILE_DATA (" + PROFILE_ID_COLUMN + " VARCHAR PRIMARY KEY," +
                        PROFILE_USER_COLUMN + " VARCHAR ," +
                        PROFILE_IMAGE_COLUMN + " VARCHAR )"
                //"CREATE TABLE IF NOT EXISTS DATA(FROM VARCHAR,TO VARCHAR,MESSAGE VARCHAR );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase mydatabase, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        mydatabase.execSQL("DROP TABLE IF EXISTS MESSAGE_DATA");
        mydatabase.execSQL("DROP TABLE IF EXISTS PROFILE_DATA");
        mydatabase.execSQL("DROP TABLE IF EXISTS COMMENT_DATA");
        onCreate(mydatabase);
    }

    public boolean insertFeedData (String message , String protocol) {
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(message);
            Log.d(TAG, "Inserted");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_ID_COLUMN, msgObject.optString(Keys.KEY_ID));
        contentValues.put(MESSAGE_USER_NAME_COLUMN, msgObject.optString(Keys.KEY_NAME));
        contentValues.put(MESSAGE_USER_ID_COLUMN, msgObject.optString(Keys.KEY_USERID));
        contentValues.put(MESSAGE_COLUMN, msgObject.optString(Keys.KEY_MESSAGE));
        contentValues.put(PROFILE_IMAGE_COLUMN, msgObject.optString(Keys.KEY_PROFIMG));
        contentValues.put(MESSAGE_IMAGE_COLUMN, msgObject.optString(Keys.KEY_IMAGE));
        contentValues.put(MESSAGE_TIME_COLUMN, msgObject.optString(Keys.KEY_TIME));
        contentValues.put(MESSAGE_PROTOCOL_COLUMN, protocol);
        mydatabase.insert("MESSAGE_DATA", null, contentValues);
        return true;
    }


    public boolean insertCommentData (String message) {
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_ID_COLUMN, msgObject.optString(Keys.KEY_ID));
        contentValues.put(MESSAGE_USER_NAME_COLUMN, msgObject.optString(Keys.KEY_NAME));
        contentValues.put(MESSAGE_USER_ID_COLUMN, msgObject.optString(Keys.KEY_USERID));
        contentValues.put(MESSAGE_COLUMN, msgObject.optString(Keys.KEY_MESSAGE));
        contentValues.put(PROFILE_IMAGE_COLUMN, msgObject.optString(Keys.KEY_PROFIMG));
        //contentValues.put(MESSAGE_IMAGE_COLUMN_NAME, msgObject.optString(Keys.KEY_IMAGE));
        //contentValues.put(MESSAGE_LIKES_COLUMN_NAME, msgObject.optString(Keys.KEY_LIKES));
        contentValues.put(MESSAGE_TIME_COLUMN, msgObject.optString(Keys.KEY_TIME));
        mydatabase.insert("COMMENT_DATA", null, contentValues);
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
        contentValues.put(PROFILE_ID_COLUMN, msgObject.optString("phonenumber"));
        contentValues.put(PROFILE_USER_COLUMN, msgObject.optString("username"));
        contentValues.put(PROFILE_IMAGE_COLUMN, msgObject.optString(Keys.KEY_PROFIMG));
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
        mydatabase.update("PROFILE_DATA", contentValues, PROFILE_ID_COLUMN + "= \"" + msgObject.optString(Keys.KEY_USERID) + "\" ", null);

        return true;
    }


    public ArrayList<Person> getFeedData()
    {
        ArrayList<Person> array_list = new ArrayList<Person>();

        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor msg_res =  db.rawQuery( "select * from MESSAGE_DATA where " +MESSAGE_FROM_COLUMN+ " = \"" + id + "\" or " +MESSAGE_TO_COLUMN_NAME+ " = \""+id+"\" ORDER BY "+ MESSAGE_TIME_COLUMN_NAME+" DESC", null );
        Cursor msg_res =  db.rawQuery( "select * from MESSAGE_DATA where " + MESSAGE_PROTOCOL_COLUMN + " = \"HTTP\" ORDER BY "+ MESSAGE_TIME_COLUMN+" DESC", null );
        msg_res.moveToFirst();

        while(msg_res.isAfterLast() == false){
            String column0 = msg_res.getString(0);
            String column1 = msg_res.getString(1);
            String column2 = msg_res.getString(3);
            String column3 = msg_res.getString(4);
            String column4 = msg_res.getString(5);
            String column5 = msg_res.getString(6);

            array_list.add(new Person("F",column0, column1 , column2 , column3, column4,column5 ));
            msg_res.moveToNext();
        }
        return array_list;
    }


    public String getFeedDataColumn(String id, Integer columnnumber )
    {

        String  columndata;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor msg_res =  db.rawQuery( "select * from MESSAGE_DATA where " +MESSAGE_ID_COLUMN+ " = \"" + id +"\"" , null );

        msg_res.moveToFirst();

        if (msg_res.getCount() != 0){
            msg_res.moveToFirst();
            columndata = msg_res.getString(columnnumber);
            Log.d(TAG, "getFeedDataColumn: " + columndata);}
        else{
            columndata="nodata";
        }

        return columndata;

    }

    public String getFeedDataLatestTime()
    {

        String  columndata;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor msg_res =  db.rawQuery( "select * from MESSAGE_DATA where " + MESSAGE_PROTOCOL_COLUMN + " = \"HTTP\"  ORDER BY "+ MESSAGE_TIME_COLUMN+" DESC" , null );

        msg_res.moveToFirst();

        if (msg_res.getCount() != 0){
            //msg_res.moveToFirst();
            columndata = msg_res.getString(6);
            Log.d(TAG, "getFeedDataColumn: " + columndata);}
        else{
            columndata="2000-12-31 12:00:00";
        }

        return columndata;

    }



    public ArrayList<Person> getCommentData(String id)
    {
        ArrayList<Person> array_list = new ArrayList<Person>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor msg_res =  db.rawQuery( "select * from COMMENT_DATA where " +MESSAGE_ID_COLUMN+ " = \"" + id +"\" ORDER BY "+ MESSAGE_TIME_COLUMN+" DESC", null );
        //Cursor res =  db.rawQuery( "select * from MESSAGE_DATA" , null );
        msg_res.moveToFirst();

        while(msg_res.isAfterLast() == false){
            String column0 = msg_res.getString(0);
            String column1 = msg_res.getString(1);
            String column2 = msg_res.getString(3);
            String column3 = msg_res.getString(4);
            String column4 = msg_res.getString(4);


            array_list.add(new Person("C",column0, column1 , column2 ,column3, "", column4  ));
            msg_res.moveToNext();
        }
        return array_list;
    }



    public String getProfileInfo(String id,Integer columnnumber) {
        String columndata;
        SQLiteDatabase db = this.getReadableDatabase();

        //String Query = "SELECT * FROM PROFILE_DATA WHERE PROFILE_USER = 'Fiji' ";
        Cursor prof_res = db.rawQuery("SELECT * FROM PROFILE_DATA WHERE " + PROFILE_ID_COLUMN + " = \"" + id + "\"", null);
        Log.d(TAG, "getProfileInfoCount: " + prof_res.getCount());
        Log.d(TAG, "getProfileInfoID: " + id);

        if (prof_res.getCount() != 0){
            prof_res.moveToFirst();
            columndata = prof_res.getString(columnnumber);
        //    Log.d(TAG, "getProfileInfo: " + columndata);
        }
        else{
            columndata="noimage";
        }

        return columndata;
    }

    public String getProfileId(String id) {
        String columndata;
        SQLiteDatabase db = this.getReadableDatabase();

        //String Query = "SELECT * FROM PROFILE_DATA WHERE PROFILE_USER = 'Fiji' ";
        Cursor prof_res = db.rawQuery("SELECT * FROM PROFILE_DATA WHERE " + PROFILE_USER_COLUMN + " = \"" + id + "\"", null);
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




