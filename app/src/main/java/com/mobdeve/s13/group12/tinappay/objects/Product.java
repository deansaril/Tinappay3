package com.mobdeve.s13.group12.tinappay.objects;

public class Product {
    private int imageId;
    private String name, type;
    private float price;

    public Product (int imageId, String name, String type, float price) {
        this.imageId = imageId;
        this.name = name;
        this.type = type;
        this.price = price;
    }

    public int getImageId() {
        return this.imageId;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public float getPrice() {
        return this.price;
    }
}
