package com.example.app.ourapplication.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.app.ourapplication.DiscussionActivity;
import com.example.app.ourapplication.Keys;
import com.example.app.ourapplication.OurApplication;
import com.example.app.ourapplication.R;
import com.example.app.ourapplication.Util;
import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.ApiUrls;
import com.example.app.ourapplication.rest.model.request.SignInReqModel;
import com.example.app.ourapplication.rest.model.request.SubscribeReqModel;
import com.example.app.ourapplication.rest.model.response.SuccessRespModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sarumugam on 16/07/16.
 */
public class Helper extends AppCompatActivity{



    private final String TAG = Helper.class.getSimpleName();

    public static String getCurrentTimeStamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return dateFormat.format(new Date());
    }

    public static String getRelativeTime(String posttime){

        Date currDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            String relativeTime =  DateUtils.getRelativeTimeSpanString(
                    dateFormat.parse(posttime).getTime(),// The time to display
                    currDate.getTime(), //Current time
                    DateUtils.SECOND_IN_MILLIS, // The minimum resolution. This will display seconds (eg: "3 seconds ago")
                    DateUtils.FORMAT_ABBREV_RELATIVE).toString();

            return relativeTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formCommentMessage(String type, String postid, String token,  String message){
        JSONObject msgObject = new JSONObject();
        try {
            msgObject.put(Keys.KEY_TYPE,type);
            msgObject.put(Keys.KEY_ID,postid);
            msgObject.put(Keys.KEY_TOKEN,token);
            //msgObject.put(Keys.KEY_TO,receiver);
            msgObject.put(Keys.KEY_MESSAGE,message);
            msgObject.put(Keys.KEY_IMAGE,"");
            msgObject.put(Keys.KEY_TIME,getCurrentTimeStamp());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgObject.toString();

    }

    public static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static Bitmap decodeImageString(String rimgmessage) {
        String imgString;
        Bitmap decodedImage;

        if (rimgmessage.length()==0) {
            decodedImage = null;
        }
        else {

            imgString = rimgmessage.substring(0, rimgmessage.length() - 1);
            byte[] decodedString = Base64.decode(imgString, Base64.NO_PADDING);
            decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return decodedImage;
    }

    public static String getStringImage(Bitmap bmp) {
        if(bmp == null){
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int maxWidth = 1024 ;
        int maxHeight = 512;


        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }
        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);
        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

    public Bitmap BITMAP_RESIZER(Bitmap bitmap,int newWidth,int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public static void PostSubscription(View view,String postid,String subscriptionflag){
        final View v=view;
        final String successmessage;
        final String failuremessage;
        final String userid =  PreferenceEditor.getInstance(v.getContext()).getLoggedInUserName();
        SubscribeReqModel model = new SubscribeReqModel(postid,userid,subscriptionflag);
        Call<SuccessRespModel> postsubscription;
        if(subscriptionflag.equals("Y")) {
            postsubscription = ((OurApplication) v.getContext().getApplicationContext()).getRestApi().
                    SubscribeFeed(model);
            successmessage="Post Subscription successful";
            failuremessage="Post Subscription failed";}
        else
        { postsubscription = ((OurApplication) v.getContext().getApplicationContext()).getRestApi().
                UnSubscribeFeed(model);
            successmessage="Post UnSubscription successful";
            failuremessage="Post UnSubscription failed";}
        postsubscription.enqueue(new Callback<SuccessRespModel>() {
            @Override
            public void onResponse(Call<SuccessRespModel> call, Response<SuccessRespModel> response) {
                if (response.body().isSuccess()) {
                    Toast.makeText(v.getContext(), successmessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), failuremessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SuccessRespModel> call, Throwable t) {
                t.printStackTrace();
                //Log.d(TAG, failuremessage);
                Toast.makeText(v.getContext(), failuremessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}