package com.mhp.foodfinder.Model;

import java.io.Serializable;

/**
 * Created by Ady on 11/11/2018.
 */

public class User implements Serializable {

    private String name;
    private String birthday;
    private String phone;
    private String profilePicture;

    public User() {

    }

    public User(String birthday, String name, String phone, String profilePicture) {
        this.name = name;
        this.birthday=birthday;
        this.phone = phone;
        this.profilePicture = profilePicture;
    }

    public void setPhone (String phone) {
        this.phone = phone;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setBirthday (String birthday) {
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}