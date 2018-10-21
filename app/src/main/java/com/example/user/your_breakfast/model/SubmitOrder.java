package com.example.user.your_breakfast.model;

import java.util.List;

public class SubmitOrder {
    private String phoneNumber, address, status, total, name;
    private List<Order> orderDetail;
    private String time;
    private String moreInfo;

    public SubmitOrder(String phoneNumber, String address, String status, String total, String name, List<Order> orderDetail, String time) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
        this.total = total;
        this.name = name;
        this.orderDetail = orderDetail;
        this.time = time;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public SubmitOrder() {
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<Order> orderDetail) {
        this.orderDetail = orderDetail;
    }
}
