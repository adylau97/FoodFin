package com.mhp.foodfinder.Model;

import java.io.Serializable;

/**
 * Created by Ady on 7/11/2018.
 */

public class CurrentReview_Firebase implements Serializable {

    public String rating,review,user;


    public CurrentReview_Firebase() {
    }

    public CurrentReview_Firebase(String rating, String review, String user) {
        this.rating = rating;
        this.review = review;
        this.user = user;
    }



}
