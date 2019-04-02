package com.example.caoan.shopmaster.Model;

import java.io.Serializable;

public class Cart implements Serializable {

    private int id;
    private String name;
    private float price;
    private int number;
    private String urlImage;
    private String userKey;
    private String keyStore;

    public Cart() {
    }

    public Cart(String name, float price, int number, String urlImage, String userKey, String keyStore) {
        this.name = name;
        this.price = price;
        this.number = number;
        this.urlImage = urlImage;
        this.userKey = userKey;
        this.keyStore = keyStore;
    }

    public Cart(int id, String name, float price, int number, String urlImage, String userKey, String keyStore) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.number = number;
        this.urlImage = urlImage;
        this.userKey = userKey;
        this.keyStore = keyStore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", number=" + number +
                ", urlImage='" + urlImage + '\'' +
                ", userKey='" + userKey + '\'' +
                ", keyStore='" + keyStore + '\'' +
                '}';
    }
}
