package com.asce4s.gogreen.util;

/**
 * Created by Ace on 10/2/2016.
 */

public class Event {

    private String title, imageURI, description,uid,display_name,date,time;
    private int count=0;
    private Location place;



    public Event(){}

    public Event(String title, String imageURI, String description, String uid, String display_name, Location place,String date,String time) {
        this.title = title;
        this.imageURI = imageURI;
        this.description = description;
        this.uid = uid;
        this.display_name = display_name;
        this.place = place;
        this.date=date;
        this.time=time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
