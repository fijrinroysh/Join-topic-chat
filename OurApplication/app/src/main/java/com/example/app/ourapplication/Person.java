package com.example.app.ourapplication;

import android.graphics.Bitmap;

/**
 * Created by ROYSH on 6/23/2016.
 */


    public class Person {
        String name;
        String age;
        int photoId;
        Bitmap photoMsg;

        Person(String name, String age, int photoId, Bitmap photoMsg) {
            this.name = name;
            this.age = age;
            this.photoId = photoId;
            this.photoMsg = photoMsg;
        }
    }

