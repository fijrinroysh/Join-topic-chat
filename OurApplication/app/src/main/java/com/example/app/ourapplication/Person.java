package com.example.app.ourapplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class Person  {
     String mType;
     String mPostId;
     String mSenderName;
     String mMessage;
     String mPhotoId;
     String mPhotoMsg;
     String mTimeMsg;

    public Person(String type,String postId, String senderName, String msg,
                  String photoId, String photoMsg, String timeMsg) {
        this.mType = type;
        this.mPostId = postId;
        this.mSenderName = senderName;
        this.mMessage = msg;
        this.mPhotoId = photoId;
        this.mPhotoMsg = photoMsg;
        this.mTimeMsg = timeMsg;
    }


}