package com.example.app.ourapplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class Person implements Parcelable {
    private String mType;
    private String mPostId;
    private String mSenderName;
    private String mReceiverName;
    private String mMessage;
    private String mPhotoId;
    private String mPhotoMsg;
    private String mTimeMsg;

    public Person(String type,String postId, String senderName,String receiverName, String msg,
                  String photoId, String photoMsg, String timeMsg) {
        this.mType = type;
        this.mPostId = postId;
        this.mSenderName = senderName;
        this.mReceiverName = receiverName;
        this.mMessage = msg;
        this.mPhotoId = photoId;
        this.mPhotoMsg = photoMsg;
        this.mTimeMsg = timeMsg;
    }

    public Person(Parcel in) {
        mType = in.readString();
        mPostId = in.readString();
        mSenderName = in.readString();
        mReceiverName = in.readString();
        mMessage = in.readString();
        mPhotoId = in.readString();
        mPhotoMsg = in.readString();
        mTimeMsg = in.readString();
    }

    public String getType() {
        return mType;
    }

    public String getPostId() {
        return mPostId;
    }

    public String getSenderName() {
        return mSenderName;
    }

    public String getReceiverName() {
        return mReceiverName;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getPhotoId() {
        return mPhotoId;
    }

    public String getPhotoMsg() {
        return mPhotoMsg;
    }

    public String getTimeMsg() {
        return mTimeMsg;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeString(mPostId);
        dest.writeString(mSenderName);
        dest.writeString(mReceiverName);
        dest.writeString(mMessage);
        dest.writeString(mPhotoId);
        dest.writeString(mPhotoMsg);
        dest.writeString(mTimeMsg);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}