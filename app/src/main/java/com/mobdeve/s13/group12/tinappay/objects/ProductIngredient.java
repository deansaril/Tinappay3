package com.mobdeve.s13.group12.tinappay.objects;

import java.io.Serializable;

public class ProductIngredient implements Serializable {
    private String name;
    private int quantity;

    public ProductIngredient () {}

    public ProductIngredient (String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
    }

    public int getQuantity() { return this.quantity; }

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
