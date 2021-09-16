package com.mobdeve.s13.group12.tinappay.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Extends the IngredientModel by adding the ingredient's ID and the bitmap of the image that corresponds to it.
 * Both of the attributes are used for retrieval
 */
public class Ingredient extends IngredientModel{
    private String id;
    private byte[] imageBytes;

    /**
     * Constructor for database queries
     */
    public Ingredient () {}

    /**
     *  This constructor is for when the user has not uploaded any image
     * @param name String of the ingredient's name
     * @param type String of the ingredient's type
     * @param location String of the ingredient's locaation
     * @param price float value of the ingredient's price
     */
    public Ingredient(String name, String type, String location, float price) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.price = price;
    }


    /**
     *  This constructor is for when the user has uploaded an image
     * @param name String of the ingredient's name
     * @param type String of the ingredient's type
     * @param location String of the ingredient's locaation
     * @param price float value of the ingredient's price
     */
    public Ingredient(String imagePath, String name, String type, String location, float price) {
        this.imagePath = imagePath;
        this.name = name;
        this.type = type;
        this.location = location;
        this.price = price;
    }

    /**
     * Sets the image bytes of the ingredient
     * @param img Bitmap of the image of ingredient to be converted to bytes
     */
    public void setImg (Bitmap img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.imageBytes = stream.toByteArray();
    }

    /**
     * Sets the ID of the ingredient
     * @param id the String ID to set the ingredient's id
     */
    public void setId(String id){ this.id = id;}

    /**
     * Returns the ID of the ingredient
     * @return id the String ID of the ingredient
     */
    public String getId() { return this.id; }

    /**
     * Converts the bytes of the ingredient's image and returns its Bitmap
     * @return Bitmap the bitmap of the ingredient's image
     */
    public Bitmap getImg() { return BitmapFactory.decodeByteArray(this.imageBytes, 0, this.imageBytes.length); }
}
