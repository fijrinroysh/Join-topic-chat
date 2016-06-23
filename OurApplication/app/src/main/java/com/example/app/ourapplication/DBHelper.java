package com.example.app.ourapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by ROYSH on 6/8/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String MESSAGE_COLUMN_NAME = "MESSAGE";
    public static final String MESSAGE_FROM_COLUMN_NAME = "MESSAGE_FROM";
    public static final String MESSAGE_TO_COLUMN_NAME = "MESSAGE_TO";


    SQLiteDatabase mydatabase;

    public DBHelper(Context context)
    {
        super(context, "FEED" , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase mydatabase) {
        // TODO Auto-generated method stub
        mydatabase.execSQL(
                "create table DATA ("+MESSAGE_FROM_COLUMN_NAME+" VARCHAR,"+MESSAGE_TO_COLUMN_NAME+" VARCHAR,"+MESSAGE_TO_COLUMN_NAME+" VARCHAR)"
                //"CREATE TABLE IF NOT EXISTS DATA(FROM VARCHAR,TO VARCHAR,MESSAGE VARCHAR );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase mydatabase, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        mydatabase.execSQL("DROP TABLE IF EXISTS DATA");
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
        contentValues.put(MESSAGE_FROM_COLUMN_NAME, msgObject.optString(Keys.KEY_NAME));
        contentValues.put(MESSAGE_TO_COLUMN_NAME, msgObject.optString(Keys.KEY_TO));
        contentValues.put(MESSAGE_COLUMN_NAME, msgObject.optString(Keys.KEY_MESSAGE));

        mydatabase.insert("DATA", null, contentValues);
        return true;
    }


    public ArrayList<Person> getData(String id)
    {
        ArrayList<Person> array_list = new ArrayList<Person>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from DATA where " +MESSAGE_FROM_COLUMN_NAME+ " = \"" + id + "\" or " +MESSAGE_TO_COLUMN_NAME+ " = \""+id+"\"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String column1 = res.getString(0);
            String column2 = res.getString(1);
            String column3 = res.getString(2);
            String message = "Message from "+column1 +" to "+ column2 +" : "+ column3;

            array_list.add(new Person("Message from "+column1 +" to "+ column2 , column3, R.drawable.mickey));
            res.moveToNext();
        }
        return array_list;
    }
}



