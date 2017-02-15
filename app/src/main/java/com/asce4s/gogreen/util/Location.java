package com.asce4s.gogreen.util;


import com.google.android.gms.maps.model.LatLng;

public class Location {


    private String name, addr;
    private Double latitude, longtitude;



    public Location() {
    }

    public Location(String name, String addr, Double latitude, Double longtitude) {
        this.name = name;
        this.addr = addr;
        this.latitude = latitude;
        this.longtitude = longtitude;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }


}
