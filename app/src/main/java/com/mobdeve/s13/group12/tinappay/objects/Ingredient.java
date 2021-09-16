package com.mobdeve.s13.group12.tinappay.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class Ingredient extends IngredientModel{
    private String id;
    private byte[] imageBytes;


    public Ingredient () {}

    public Ingredient(String name, String type, String location, float price) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.price = price;
    }

    //This constructor is for when the user has not uploaded any image
    public Ingredient(String imagePath, String name, String type, String location, float price) {
        this.imagePath = imagePath;
        this.name = name;
        this.type = type;
        this.location = location;
        this.price = price;
    }

    public void setId (String id) {
        this.id = id;
    }

    public void setImg (Bitmap img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.imageBytes = stream.toByteArray();
    }

    public String getId() { return this.id; }

    public Bitmap getImg() { return BitmapFactory.decodeByteArray(this.imageBytes, 0, this.imageBytes.length); }
}
