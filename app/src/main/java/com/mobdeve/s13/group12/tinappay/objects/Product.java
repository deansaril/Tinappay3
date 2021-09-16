package com.mobdeve.s13.group12.tinappay.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * This class is a model of product items for back-end manipulation
 */
public class Product extends ProductModel {
    private String id;
    private byte[] img;
    // ByteArray bitmap sauce:
    //      https://stackoverflow.com/questions/11010386/passing-android-bitmap-data-within-activity-using-intent-in-android

    /**
     * Default constructor of Product
     */
    public Product() {}

    /**
     * Instantiates a Product without an image
     * @param name String - product name
     * @param type String - product type
     * @param description String - product description
     * @param ingredients HashMap - product ingredients
     */
    public Product(String name, String type, String description, HashMap<String, Integer> ingredients) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.ingredients = ingredients;
    }

    /**
     * Instantiates a Product with an image
     * @param imagePath String - image file path
     * @param name String - product name
     * @param type String - product type
     * @param description String - product description
     * @param ingredients HashMap - product ingredients
     */
    public Product(String imagePath, String name, String type, String description, HashMap<String, Integer> ingredients) {
        this.imagePath = imagePath;
        this.name = name;
        this.type = type;
        this.description = description;
        this.ingredients = ingredients;
    }

    /**
     * Sets id of product item
     * @param id String - product id
     */
    public void setId (String id) {
        this.id = id;
    }

    /**
     * Sets image of product item
     * @param img Bitmap - product image
     */
    public void setImg (Bitmap img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.img = stream.toByteArray();
    }

    /**
     * Gets id of product item
     * @return String - product id
     */
    public String getId() { return this.id; }

    /**
     * Gets image of product item
     * @return String - product image
     */
    public Bitmap getImg() {
        return BitmapFactory.decodeByteArray(this.img, 0, this.img.length);
    }
}