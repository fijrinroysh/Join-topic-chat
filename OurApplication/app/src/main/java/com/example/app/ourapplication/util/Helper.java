package com.example.app.ourapplication.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Base64;

import com.example.app.ourapplication.AppUrl;
import com.example.app.ourapplication.HomeFeedActivity;
import com.example.app.ourapplication.Keys;
import com.example.app.ourapplication.Person;
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
public class Helper {

    public static String getLoginRequestBody(String name, String password){
        JSONObject body = new JSONObject();
        try {
            body.put("name",name);
            body.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

    public static String getSignUpRequestBody(String name, String password){
        JSONObject body = new JSONObject();
        try {
            body.put("name",name);
            body.put("password",password);
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

    public static Person parseFeeds(String message){
        JSONObject msgObject = null;
        Person message_return = null;
        try {
            msgObject = new JSONObject(message);
            String rimgmessage = msgObject.optString(Keys.KEY_IMAGE);
            String imgmessage = rimgmessage.substring(0, rimgmessage.length() - 1);
            message_return = new Person("Message from "+msgObject.optString(Keys.KEY_NAME) +" to "
                    + msgObject.optString(Keys.KEY_TO) , msgObject.optString(Keys.KEY_MESSAGE), R.drawable.mickey, imgmessage );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message_return;
    }


    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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