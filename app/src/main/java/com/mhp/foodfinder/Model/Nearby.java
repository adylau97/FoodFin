package com.mhp.foodfinder.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Ady on 11/9/2018.
 */

public class Nearby implements Serializable{

    public String name,review,photo_reference,isOpening;
    public transient LatLng latLng;

    public Nearby(String name, String review, String photo_reference,String isOpening,LatLng latLng) {
        this.name = name;
        this.review = review;
        this.photo_reference = photo_reference;
        this.isOpening = isOpening;
        this.latLng = latLng;
    }
}
