package com.mhp.foodfinder.Model;

import java.io.Serializable;

/**
 * Created by Ady on 5/11/2018.
 */

public class FoodList implements Serializable {

    public String name,photo,key;

    public FoodList(String name, String photo, String key) {
        this.name = name;
        this.photo = photo;
        this.key = key;
    }
}
