package com.example.aistudymentor.data.models;

public class RecentActivity {
    private int id;
    private String userEmail;
    private String title;
    private String description;
    private String timestamp;

    public RecentActivity() {
    }

    public RecentActivity(String userEmail, String title, String description, String timestamp) {
        this.userEmail = userEmail;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
