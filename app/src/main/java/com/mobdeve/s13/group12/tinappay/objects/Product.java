package com.mobdeve.s13.group12.tinappay.objects;

import java.util.ArrayList;

public class Product {
    private int imageId;
    private String name, type, description;
    private float price;
    private ArrayList<String> ingredients;

    public Product (int imageId, String name, String type, float price, String description, ArrayList<String> ingredients) {
        this.imageId = imageId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
        this.ingredients = ingredients;
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
