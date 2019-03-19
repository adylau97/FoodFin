package com.mhp.foodfinder.Model;

import java.util.List;

/**
 * Created by Ady on 27/10/2018.
 */

public class Firebase_Restaurant {

    private Double longitude;
    private Double latitude;
    private String name;
    private List<String> image;
    private String closetime;
    private String opentime;
    private String addByUser;
    private List<String> menu;
    private String averagePrice;
    private String contact;
    private String description;
    private String foodStyle;
    private String website;
    private String address;
    private int ratings;
    private List<CurrentReview_Firebase> reviews;

    public Firebase_Restaurant() {

    }



    public Firebase_Restaurant(Double longitude, Double latitude, String name, List<String> image, String closetime, String opentime, String addByUser, List<String> menu, String averagePrice, String contact, String description, String foodStyle, String website,String address, int ratings, List<CurrentReview_Firebase>reviews) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.image = image;
        this.closetime = closetime;
        this.opentime = opentime;
        this.addByUser = addByUser;
        this.menu = menu;
        this.averagePrice = averagePrice;
        this.contact = contact;
        this.description = description;
        this.foodStyle = foodStyle;
        this.website = website;
        this.address = address;
        this.ratings = ratings;
        this.reviews = reviews;
    }

    public List<CurrentReview_Firebase> getReviews() {
        return reviews;
    }

    public void setReviews(List<CurrentReview_Firebase> reviews) {
        this.reviews = reviews;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getClosetime() {
        return closetime;
    }

    public void setClosetime(String closetime) {
        this.closetime = closetime;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getAddByUser() {
        return addByUser;
    }

    public void setAddByUser(String addByUser) {
        this.addByUser = addByUser;
    }

    public List<String> getMenu() {
        return menu;
    }

    public void setMenu(List<String> menu) {
        this.menu = menu;
    }

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFoodStyle() {
        return foodStyle;
    }

    public void setFoodStyle(String foodStyle) {
        this.foodStyle = foodStyle;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
