package com.example.home.model;

public class Modeltest {
    private int id;
    private String avatar;

    public Modeltest(int id, String avatar) {
        this.id = id;
        this.avatar = avatar;
    }
    public Modeltest() {
        this.id = id;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
