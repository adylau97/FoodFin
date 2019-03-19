package com.mhp.foodfinder.Remote;

import com.mhp.foodfinder.Model.MyPlaces;
import com.mhp.foodfinder.Model.Results;

/**
 * Created by Ady on 2/9/2018.
 */

public class Common {

    public static Results currentResult;

    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

    public static IGoogleAPIService getGoogleAPIServiceScalars(){
        return RetrofitScalarsClient.getScalarClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
