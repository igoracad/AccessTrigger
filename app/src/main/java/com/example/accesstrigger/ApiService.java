package com.example.accesstrigger;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET(".")
    Call<Void>getRequest();
}
