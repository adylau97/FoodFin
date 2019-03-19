package com.mhp.foodfinder.Model;

import java.io.Serializable;

/**
 * Created by Ady on 1/10/2018.
 */

public class CurrentReview implements Serializable {

    public String text;

    public CurrentReview(String text) {
        this.text = text;
    }
}
