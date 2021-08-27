package com.mobdeve.s13.group12.tinappay.objects;

public class Ingredient {
    private int imageId;
    private String id, name, type, location;
    private float price;

    public Ingredient () {}

    public Ingredient(int imageId, String name, String type, String location, float price) {
        this.id = name.toLowerCase().replaceAll(" ", "_");
        this.imageId = imageId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.price = price;
    }

    public String getId(){ return this.id;}

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
