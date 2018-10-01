package com.example.user.your_breakfast.model;

public class Food {
    private String Name, Price, Image, Discount, Description;

    public Food() {
    }

    public Food(String name, String price, String image, String discount, String description) {
        Name = name;
        Price = price;
        Image = image;
        Discount = discount;
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}