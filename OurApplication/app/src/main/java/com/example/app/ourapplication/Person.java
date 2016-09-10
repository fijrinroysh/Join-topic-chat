package com.example.app.ourapplication;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class Person  {
    String sendername;
    String receivername;
    String msg;
    String photoId;
    String photoMsg;
    String timeMsg;

    public Person(String sendername,String receivername, String msg, String photoId, String photoMsg, String timeMsg) {
        this.sendername = sendername;
        this.receivername = receivername;
        this.msg = msg;
        this.photoId = photoId;
        this.photoMsg = photoMsg;
        this.timeMsg = timeMsg;
    }


}

