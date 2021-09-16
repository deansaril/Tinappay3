package com.mobdeve.s13.group12.tinappay.objects;

import java.io.Serializable;

/**
 * This class contains the essential information of the ingredient.
 * It is mainly used for storing the ingredient's data to the database.
 */
public class IngredientModel implements Serializable {
    protected String name, type, location, imagePath;
    protected float price;


    /**
     * Constructor for database queries
     */
    public IngredientModel () {}



    /**
     * This constructor is for when the user has not uploaded any image
     * @param i the Ingredient that contains the essential data for the Constructor
     */
    public IngredientModel(Ingredient i) {
        this.imagePath = "ingredient.png";
        this.name = i.getName();
        this.type = i.getType();
        this.location = i.getLocation();
        this.price = i.getPrice();
    }

    /**
     * This constructor is for when the user has uploaded an image to set the ingredient's image path
     * @param i the Ingredient that contains the essential data for the Constructor
     * @param userId the String ID of the user who is currently signed in
     * @param ingredientId the String ID of the ingredient
     */
    public IngredientModel(Ingredient i, String userId, String ingredientId) {
        this.imagePath = userId + "/" + "ingredients" + "/" + ingredientId;
        this.name = i.getName();
        this.type = i.getType();
        this.location = i.getLocation();
        this.price = i.getPrice();
    }

    /**
     * Returns the path of the ingredient's image in the Cloud Storage
     * @return imagePath the String path leading to the image stored in the Cloud Storage
     */
    public String getImagePath() {return this.imagePath;}

    /**
     * Returns name of ingredient
     * @return name String name of ingredient
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns type of ingredient
     * @return type String type of ingredient
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns location of ingredient
     * @return location String location of ingredient
     */
    public String getLocation(){ return this.location;}

    /**
     * Returns price of ingredient
     * @return price float price of ingredient
     */
    public float getPrice() {
        return this.price;
    }
}
