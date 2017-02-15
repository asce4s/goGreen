package com.asce4s.gogreen.util;

/**
 * Created by Ace on 10/6/2016.
 */

public class Votes {

    private Location l;
    private String uid;

    public Votes(Location l, String uid) {
        this.l = l;
        this.uid = uid;
    }

    public Location getL() {
        return l;
    }

    public void setL(Location l) {
        this.l = l;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
