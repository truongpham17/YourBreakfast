package com.example.user.your_breakfast.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String name;
    private String phone;
    private String password;
    private String image;
    private Map<String, Address> address;

    public User(String name, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        image = "null";
    }


    public User(String name, String phone, String password, String image) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
    }

    public User(String name, String phone, String password, String image, Map<String, Address> address) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
    }

    public User() {
        image = "null";
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Address> getAddress() {
        return address;
    }

    public void setAddress(Map<String, Address> address) {
        this.address = address;
    }
}
