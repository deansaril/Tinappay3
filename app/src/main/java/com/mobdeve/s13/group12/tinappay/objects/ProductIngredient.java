package com.mobdeve.s13.group12.tinappay.objects;

import java.io.Serializable;

public class ProductIngredient implements Serializable {
    private String name;
    private int quantity;
    private float price;

    public ProductIngredient () {}

    public ProductIngredient (String name, int quantity, float price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public int getQuantity() { return this.quantity; }

    public float getPrice() { return this.price; }

    public void changeQuantity (int amount) {
        if (amount == 1) {
            if (this.quantity + 1 <= 50)
                this.quantity++;
        }
        else
        if (this.quantity - 1 >= 1)
            this.quantity--;
    }
}
