package com.example.app.ourapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;

import com.example.app.ourapplication.Keys;
import com.example.app.ourapplication.Util;
import com.example.app.ourapplication.rest.ApiUrls;
import com.example.app.ourapplication.rest.model.request.SignInReqModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
}