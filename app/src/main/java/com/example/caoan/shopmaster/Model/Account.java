package com.example.caoan.shopmaster.Model;

public class Account {
    private String userID;
    private String name;
    private String email;
    private String address;
    private String tinh;
    private String huyen;
    private String xa;
    private String phone;

    public Account() {
    }

    public Account(String userID, String name, String email, String address, String tinh, String huyen, String xa, String phone) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.address = address;
        this.tinh = tinh;
        this.huyen = huyen;
        this.xa = xa;
        this.phone = phone;
    }

    public Account(String name, String email, String address, String tinh, String huyen, String xa, String phone) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.tinh = tinh;
        this.huyen = huyen;
        this.xa = xa;
        this.phone = phone;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTinh() {
        return tinh;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public String getHuyen() {
        return huyen;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public String getXa() {
        return xa;
    }

    public void setXa(String xa) {
        this.xa = xa;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Account{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", tinh='" + tinh + '\'' +
                ", huyen='" + huyen + '\'' +
                ", xa='" + xa + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
