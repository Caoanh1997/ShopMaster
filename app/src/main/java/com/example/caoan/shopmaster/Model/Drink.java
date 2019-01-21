package com.example.caoan.shopmaster.Model;

public class Drink {
    private String name;
    private String description;
    private String urlimage;
    private int price;

    public Drink(String name, String description, String urlimage, int price) {
        this.name = name;
        this.description = description;
        this.urlimage = urlimage;
        this.price = price;
    }

    public Drink() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlimage() {
        return urlimage;
    }

    public void setUrlimage(String urlimage) {
        this.urlimage = urlimage;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Drink{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", urlimage='" + urlimage + '\'' +
                ", price=" + price +
                '}';
    }
}
