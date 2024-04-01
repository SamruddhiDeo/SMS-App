package com.example.smsapp;

import com.example.smsapp.ModelClasses.ApiPojoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IpQualityScoreApi {
    @GET("check")
    Call<ApiPojoModel> checkUrl(@Query("url") String url);
}
