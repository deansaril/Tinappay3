package com.mobdeve.s13.group12.tinappay.objects;

import java.io.Serializable;

public class IngredientModel implements Serializable {
    protected String name, type, location, imagePath;
    protected float price;


    public IngredientModel () {}

    //This constructor is for when the user has not uploaded any image
    public IngredientModel(Ingredient i) {
        this.imagePath = "ingredient.png";
        this.name = i.getName();
        this.type = i.getType();
        this.location = i.getLocation();
        this.price = i.getPrice();
    }


    public IngredientModel(Ingredient i, String userId, String ingredientId) {
        this.imagePath = userId + "/" + "ingredients" + "/" + ingredientId;
        this.name = i.getName();
        this.type = i.getType();
        this.location = i.getLocation();
        this.price = i.getPrice();
    }

    public String getImagePath() {return this.imagePath;}

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
