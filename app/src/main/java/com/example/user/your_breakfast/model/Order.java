package com.example.user.your_breakfast.model;

public class Order {
    private String foodId, foodName, foodPrice;
    private int quantity;
    private String time;
    private String imgURL;

    public Order() {
    }

    public Order(String foodId, String foodName, String foodPrice, int quantity, String time, String imgURL) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.quantity = quantity;
        this.time = time;
        this.imgURL = imgURL;
    }
    public Order(String foodId, int quantity){
        this.quantity = quantity;
        this.foodId = foodId;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
