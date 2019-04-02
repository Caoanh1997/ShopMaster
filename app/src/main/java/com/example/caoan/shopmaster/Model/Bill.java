package com.example.caoan.shopmaster.Model;

import java.util.List;

public class Bill {
    private String key_cart;
    private String name_user;
    private String address;
    private String phone;
    private String userID;
    private List<Cart> cartList;
    private String total_price;
    private String state;
    private String key_store;
    private String datetime;
    private String datetime_delivered;

    public Bill() {
    }

    public Bill(String key_cart, String name_user, String address, String phone, String userID, String total_price, String state, String key_store, String datetime, String datetime_delivered) {
        this.key_cart = key_cart;
        this.name_user = name_user;
        this.address = address;
        this.phone = phone;
        this.userID = userID;
        this.total_price = total_price;
        this.state = state;
        this.key_store = key_store;
        this.datetime = datetime;
        this.datetime_delivered = datetime_delivered;
    }

    public Bill(String key_cart, String name_user, String address, String phone, String userID, List<Cart> cartList, String total_price, String state, String key_store, String datetime, String datetime_delivered) {
        this.key_cart = key_cart;
        this.name_user = name_user;
        this.address = address;
        this.phone = phone;
        this.userID = userID;
        this.cartList = cartList;
        this.total_price = total_price;
        this.state = state;
        this.key_store = key_store;
        this.datetime = datetime;
        this.datetime_delivered = datetime_delivered;
    }

    public String getKey_cart() {
        return key_cart;
    }

    public void setKey_cart(String key_cart) {
        this.key_cart = key_cart;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getKey_store() {
        return key_store;
    }

    public void setKey_store(String key_store) {
        this.key_store = key_store;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDatetime_delivered() {
        return datetime_delivered;
    }

    public void setDatetime_delivered(String datetime_delivered) {
        this.datetime_delivered = datetime_delivered;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "key_cart='" + key_cart + '\'' +
                ", name_user='" + name_user + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", userID='" + userID + '\'' +
                ", cartList=" + cartList +
                ", total_price='" + total_price + '\'' +
                ", state='" + state + '\'' +
                ", key_store='" + key_store + '\'' +
                ", datetime='" + datetime + '\'' +
                ", datetime_delivered='" + datetime_delivered + '\'' +
                '}';
    }
}
