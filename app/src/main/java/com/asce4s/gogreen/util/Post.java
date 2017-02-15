package com.asce4s.gogreen.util;


import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseUser;

public class Post {
    private String title, imageURI, description,uid,display_name;
    private Location place;


    public Post(){}

    public Post(String title, String imageURI, String description, String uid, String display_name, Location place) {
        this.title = title;
        this.imageURI = imageURI;
        this.description = description;
        this.uid = uid;
        this.display_name = display_name;
        this.place = place;
    }

    public Post(String title, String description, String uid, String display_name) {
        this.title = title;
        this.description = description;
        this.uid = uid;
        this.display_name = display_name;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Location getPlace() {
        return place;
    }

    public void setPlace(Location place) {
        this.place = place;
    }
}
