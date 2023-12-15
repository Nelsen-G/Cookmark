package com.example.cookmark_app.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MealPlan implements Serializable {
    String userid;
    String recipeid;
    Date prepareDate;
    LocalTime prepareTime;

    public MealPlan(String userid, Date prepareDate, LocalTime prepareTime, String recipeid) {
        this.userid = userid;
        this.prepareDate = prepareDate;
        this.recipeid = recipeid;
        this.prepareTime = prepareTime;
    }

    public String getPrepareDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String dateString = dateFormat.format(prepareDate);
        return dateString;
    }

    public void setPrepareDate(String prepareDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            this.prepareDate = dateFormat.parse(prepareDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getPrepareTime(){
        DateTimeFormatter timeFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timeFormatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
            return  prepareTime.format(timeFormatter);
        }
        return null;
    }

    public void setPrepareTime(String prepareTime) {
        DateTimeFormatter inputFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputFormatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
            this.prepareTime = (prepareTime != null) ? LocalTime.parse(prepareTime, inputFormatter) : null;
        }
    }

    public String getRecipeid() {
        return recipeid;
    }

    public void setRecipeid(String recipeid) {
        this.recipeid = recipeid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
