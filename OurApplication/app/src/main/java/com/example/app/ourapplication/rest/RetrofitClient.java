package com.example.app.ourapplication.rest;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by sarumugam on 13/01/17.
 */
public class RetrofitClient {

    private static Retrofit mRetrofit;

    private RetrofitClient(){
        OkHttpClient.Builder clientBldr = new OkHttpClient.Builder();
        clientBldr.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBldr.addInterceptor(logging);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.HTTP_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(clientBldr.build())
                .build();
    }

    public static Retrofit getRetroClient() {
        if(mRetrofit == null){
            new RetrofitClient();
        }
        return mRetrofit;
    }
}