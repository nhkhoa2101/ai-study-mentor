package com.example.aistudymentor.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private int level;
    private int xp;

    public User(String username, int level, int xp) {
        this.username = username;
        this.level = level;
        this.xp = xp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
}
