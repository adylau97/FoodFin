package com.mhp.foodfinder.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Ady on 18/10/2018.
 */

public class RetrofitScalarsClient {
    private static Retrofit retrofit = null;
    public static Retrofit getScalarClient (String baseUrl){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
