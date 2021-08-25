package com.mobdeve.s13.group12.tinappay.objects;

import java.util.ArrayList;

public class Product {
    private int imageId;
    private float price;
    private String id, name, type, description;
    private ArrayList<String> ingredients;

    public Product () {}

    public Product (int imageId, String name, String type, float price, String description, ArrayList<String> ingredients) {
        this.id = name.toLowerCase().replaceAll(" ", "_");
        this.imageId = imageId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
        this.ingredients = ingredients;
    }

    public String getId() {
        return this.id;
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

    public String getDescription() {
        return this.description;
    }
    public ArrayList<String> getIngredients() {
        return this.ingredients;
    }
}
