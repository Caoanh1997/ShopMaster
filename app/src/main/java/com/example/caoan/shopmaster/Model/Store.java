package com.example.caoan.shopmaster.Model;

import java.io.Serializable;

public class Store implements Serializable{
    private String key;
    private String name;
    private String duong;
    private String xa;
    private String huyen;
    private String tinh;
    private String userkey;
    private String urlImage;
    private String phone;

    public Store(String key, String name, String duong, String xa, String huyen, String tinh, String userkey, String urlImage, String phone) {
        this.key = key;
        this.name = name;
        this.duong = duong;
        this.xa = xa;
        this.huyen = huyen;
        this.tinh = tinh;
        this.userkey = userkey;
        this.urlImage = urlImage;
        this.phone = phone;
    }

    public Store(String name, String duong, String xa, String huyen, String tinh, String userkey, String urlImage, String phone) {
        this.name = name;
        this.duong = duong;
        this.xa = xa;
        this.huyen = huyen;
        this.tinh = tinh;
        this.userkey = userkey;
        this.urlImage = urlImage;
        this.phone = phone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuong() {
        return duong;
    }

    public void setDuong(String duong) {
        this.duong = duong;
    }

    public String getXa() {
        return xa;
    }

    public void setXa(String xa) {
        this.xa = xa;
    }

    public String getHuyen() {
        return huyen;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public String getTinh() {
        return tinh;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Store{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", duong='" + duong + '\'' +
                ", xa='" + xa + '\'' +
                ", huyen='" + huyen + '\'' +
                ", tinh='" + tinh + '\'' +
                ", userkey='" + userkey + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public Store() {
    }
}
