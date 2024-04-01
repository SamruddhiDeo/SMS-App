package com.example.smsapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;
    private static final String BASEURL="https://www.ipqualityscore.com/api/json/url/Fg9Ggy7WBx5pxN2mYbP7uyjtbsJAH7x5/";

    public static Retrofit getRetrofit() {
        if (retrofit == null){
            retrofit = new Retrofit.Builder().
                    baseUrl(BASEURL).
                    addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
