package com.mobdeve.s13.group12.tinappay.objects;

import java.util.ArrayList;
import java.util.UUID;

public class Product {
    private int img;
    private float price;
    private String id, name, type, description;
    private ArrayList<String> ingredients;

    public Product () {}

    public Product (int imageId, String name, String type, float price, String description, ArrayList<String> ingredients) {
        this.id = UUID.randomUUID().toString().replace("-","").substring(0,8);
        this.img = imageId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
        this.ingredients = ingredients;
    }

    public String getId() {
        return this.id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public int getImg() {
        return this.img;
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
