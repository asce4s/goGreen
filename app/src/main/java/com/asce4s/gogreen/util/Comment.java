package com.asce4s.gogreen.util;

public class Comment {
    String uid,name,cmnt;

    public Comment(){};

    public Comment(String uid, String name, String cmnt) {
        this.uid = uid;
        this.name = name;
        this.cmnt = cmnt;
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

    public String getCmnt() {
        return cmnt;
    }

    public void setCmnt(String cmnt) {
        this.cmnt = cmnt;
    }
}
