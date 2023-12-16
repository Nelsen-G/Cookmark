package com.example.cookmark_app.model;

import java.io.Serializable;

public class Cookmark implements Serializable {
    private String userid;
    private String recipeid;

    public Cookmark(String userid, String recipeid) {
        this.userid = userid;
        this.recipeid = recipeid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRecipeid() {
        return recipeid;
    }

    public void setRecipeid(String recipeid) {
        this.recipeid = recipeid;
    }
}
