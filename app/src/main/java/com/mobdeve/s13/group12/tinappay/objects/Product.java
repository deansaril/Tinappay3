package com.mobdeve.s13.group12.tinappay.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class Product implements Serializable {
    private int img; // TODO: Redesigned image assignment
    private String id, name, type, description, imagePath;
    private HashMap<String, Integer> ingredients;

    public Product() {}

    /*
    public Product(int imageId, String name, String type, String description, HashMap<String, Integer> ingredients) {
        this.id = UUID.randomUUID().toString().replace("-","").substring(0,8);
        this.img = imageId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.ingredients = ingredients;
    }
     */

    public Product(String userId, String name, String type, String description, HashMap<String, Integer> ingredients) {
        this.id = UUID.randomUUID().toString().replace("-","").substring(0,8);
        this.name = name;
        this.type = type;
        this.description = description;
        this.ingredients = ingredients;
        this.imagePath = userId + "/" + "products" + "/" + this.id;
    }

    public Product(String name, String type, String description, HashMap<String, Integer> ingredients) {
        this.id = UUID.randomUUID().toString().replace("-","").substring(0,8);
        this.name = name;
        this.type = type;
        this.description = description;
        this.ingredients = ingredients;
        this.imagePath = "product.png";
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

    public String getDescription() {
        return this.description;
    }

    public HashMap<String, Integer> getIngredients() {
        return this.ingredients;
    }

    public String getImagePath() {return this.imagePath;}
}
