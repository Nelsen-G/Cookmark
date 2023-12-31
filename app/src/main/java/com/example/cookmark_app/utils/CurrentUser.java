package com.example.cookmark_app.utils;

public class CurrentUser {
    // singleton to store userId from login
    private static CurrentUser instance;
    private String userId;

    private CurrentUser() {

    }

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
