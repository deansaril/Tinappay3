package com.mobdeve.s13.group12.tinappay.objects;

public class SelectIngredient {
    private int imageId;
    private String name, type;
    private float price;
    private boolean isSelected;

    public SelectIngredient(int imageId, String name, String type, float price) {
        this.imageId = imageId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.isSelected = false;
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

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected (boolean status) {
        this.isSelected = status;
    }
}
