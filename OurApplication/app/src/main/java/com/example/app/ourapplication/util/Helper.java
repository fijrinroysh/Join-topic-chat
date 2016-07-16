package com.example.app.ourapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.app.ourapplication.AppUrl;
import com.example.app.ourapplication.Keys;
import com.example.app.ourapplication.Person;
import com.example.app.ourapplication.R;
import com.example.app.ourapplication.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

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

    public static String formFeedMessage(String message,String token,String receiver){
        JSONObject msgObject = new JSONObject();
        try {
            msgObject.put(Keys.KEY_MESSAGE,message);
            msgObject.put(Keys.KEY_TOKEN,token);
            msgObject.put(Keys.KEY_TO,receiver);
            msgObject.put(Keys.KEY_IMAGE,"kllk");

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
            String msg = rimgmessage.substring(0, rimgmessage.length() - 1);
            byte[] decodedString = Base64.decode(msg, Base64.NO_PADDING);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            message_return = new Person("Message from "+msgObject.optString(Keys.KEY_NAME) +" to "
                    + msgObject.optString(Keys.KEY_TO) , msgObject.optString(Keys.KEY_MESSAGE), R.drawable.mickey, decodedByte );
            // message = "Message from "+msgObject.optString(Keys.KEY_NAME) +" to "+ msgObject.optString(Keys.KEY_TO) +" : "+ msgObject.optString(Keys.KEY_MESSAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message_return;
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