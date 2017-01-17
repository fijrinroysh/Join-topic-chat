package com.example.app.ourapplication.rest;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by sarumugam on 13/01/17.
 */
public class RetrofitClient {

    private static Retrofit mRetrofit;

    private RetrofitClient(){
        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(logging);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.HTTP_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }

    public static Retrofit getRetroClient() {
        if(mRetrofit == null){
            new RetrofitClient();
        }
        return mRetrofit;
    }
}