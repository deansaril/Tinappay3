package com.mobdeve.s13.group12.tinappay.objects;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class ProductModel implements Serializable {
    protected String imagePath, name, type, description;
    protected HashMap<String, Integer> ingredients;

    public ProductModel() {}

    public ProductModel(Product p) {
        this.imagePath = "ingredient.png";
        this.name = p.getName();
        this.type = p.getType();
        this.description = p.getDescription();
        this.ingredients = p.getIngredients();
    }

    public ProductModel(Product p, String userId, String productId) {
        this.imagePath = userId + "/" + "products" + "/" + productId;
        this.name = p.getName();
        this.type = p.getType();
        this.description = p.getDescription();
        this.ingredients = p.getIngredients();
    }

    public String getImagePath() { return this.imagePath; }

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
}
