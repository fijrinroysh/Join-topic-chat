package com.example.app.ourapplication.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.example.app.ourapplication.AppUrl;
import com.example.app.ourapplication.DBHelper;
import com.example.app.ourapplication.HomeFeedActivity;
import com.example.app.ourapplication.Keys;
import com.example.app.ourapplication.Person;
import com.example.app.ourapplication.ProfileActivity;
import com.example.app.ourapplication.R;
import com.example.app.ourapplication.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sarumugam on 16/07/16.
 */
public class Helper extends AppCompatActivity{

    private final String TAG = Helper.class.getSimpleName();


    public static String getLoginRequestBody(String number, String password){
        JSONObject body = new JSONObject();
        try {
            body.put("userid",number);
            body.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

    public static String getSignUpRequestBody(String number, String name, String password){
        JSONObject body = new JSONObject();
        try {
            body.put("userid",number);
            body.put("name",name);
            body.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

    public static String getUpdateProfileBody(String userid, String strColumnname, String strColumndata){
        JSONObject body = new JSONObject();
        try {
            body.put("userid",userid);
            body.put("columnname",strColumnname);
            body.put("columndata",strColumndata);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public static String formFeedMessage(String message,String token,String receiver,Bitmap bitmap){

        String image_string;
        if (bitmap == null) {
            image_string="noimage";
        }
        else {
            image_string= getStringImage(bitmap);
        }
        JSONObject msgObject = new JSONObject();
        try {
            msgObject.put(Keys.KEY_MESSAGE,message);
            msgObject.put(Keys.KEY_TOKEN,token);
            msgObject.put(Keys.KEY_TO,receiver);
            msgObject.put(Keys.KEY_IMAGE,image_string);
            msgObject.put(Keys.KEY_TIME,getCurrentTimeStamp());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgObject.toString();
    }

    public static Bitmap decodeImageString(String rimgmessage) {
        String imgString;
        if (rimgmessage.length() != 0){
            imgString = rimgmessage.substring(0, rimgmessage.length() - 1);
        }else {
            imgString = "noimage";
        }

        byte[] decodedString = Base64.decode(imgString, Base64.NO_PADDING);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedImage;
    }

    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static String updateProfile(String reqBody) throws IOException {
        HttpURLConnection connection = Util.getHttpConnection(AppUrl.UPDATE_URL, "POST");
        Util.writeToStream(connection, reqBody);

        return Util.readInputStream(connection);
    }

    public static String login(String reqBody) throws IOException {
        HttpURLConnection connection = Util.getHttpConnection(AppUrl.LOGIN_URL,"POST");
        Util.writeToStream(connection, reqBody);

        return Util.readInputStream(connection);
    }

    public static String signUp(String reqBody) throws IOException {
        HttpURLConnection connection = Util.getHttpConnection(AppUrl.SIGN_UP_URL,"POST");
        Util.writeToStream(connection, reqBody);

        return Util.readInputStream(connection);
    }
}