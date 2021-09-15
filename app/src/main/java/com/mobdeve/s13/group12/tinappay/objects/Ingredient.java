package com.mobdeve.s13.group12.tinappay.objects;

import java.io.Serializable;
import java.util.UUID;

public class Ingredient implements Serializable {
    private int imageId;
    private String id, name, type, location;
    private float price;

    public Ingredient () {}

    public Ingredient(int imageId, String name, String type, String location, float price) {
        this.id = UUID.randomUUID().toString().replace("-","").substring(0,8);
        this.imageId = imageId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.price = price;
    }

    public String getId(){ return this.id;}

    public void setId (String id) {
        this.id = id;
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

    public String getLocation(){ return this.location;}

    public float getPrice() {
        return this.price;
    }
}
