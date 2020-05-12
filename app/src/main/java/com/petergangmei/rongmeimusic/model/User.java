package com.petergangmei.rongmeimusic.model;

public class User {
    public User() {
    }

    private String userid, name, email, timenDate;
    private long views, totalViews, timestamp;

    public User(String userid, String name, String email, String timenDate, long views, long totalViews, long timestamp) {
        this.userid = userid;
        this.name = name;
        this.email = email;
        this.timenDate = timenDate;
        this.views = views;
        this.totalViews = totalViews;
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimenDate() {
        return timenDate;
    }

    public void setTimenDate(String timenDate) {
        this.timenDate = timenDate;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(long totalViews) {
        this.totalViews = totalViews;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
