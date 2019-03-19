package com.mhp.foodfinder.Remote;

import com.mhp.foodfinder.Model.MyPlaces;
import com.mhp.foodfinder.Model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Ady on 2/9/2018.
 */

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlace(@Url String url);

    @GET("maps/api/directions/json")
    Call<String> getDirection(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);
}
