package com.mobdeve.s13.group12.tinappay.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class Product extends ProductModel {
    private String id;
    private byte[] img;

    public Product() {}

    public Product(String name, String type, String description, HashMap<String, Integer> ingredients) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.ingredients = ingredients;
    }

    public Product(String imagePath, String name, String type, String description, HashMap<String, Integer> ingredients) {
        this.imagePath = imagePath;
        this.name = name;
        this.type = type;
        this.description = description;
        this.ingredients = ingredients;
    }

    public void setImg (Bitmap img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.img = stream.toByteArray();
    }

    public String getId() { return this.id; }

    public Bitmap getImg() {
        return BitmapFactory.decodeByteArray(this.img, 0, this.img.length);
    }
}