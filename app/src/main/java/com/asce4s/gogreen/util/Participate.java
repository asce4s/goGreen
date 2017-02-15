package com.asce4s.gogreen.util;

/**
 * Created by Ace on 10/3/2016.
 */

public class Participate {
    String uid,name;

    public Participate(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
