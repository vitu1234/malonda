package com.example.malonda.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://192.168.8.102/malonda/android/public/";
    public static final String BASE_URL2 = "http://192.168.8.102/malonda/";//for getting images
    public static RetrofitClient minstance;
    public Retrofit retrofit;

    public RetrofitClient(){
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(minstance == null){
            //create the instance
            minstance = new RetrofitClient();
        }
        return minstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }

}