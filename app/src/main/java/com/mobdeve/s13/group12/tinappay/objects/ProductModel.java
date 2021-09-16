package com.mobdeve.s13.group12.tinappay.objects;


import java.io.Serializable;
import java.util.HashMap;

/**
 * This class is a database model of product items
 */
public class ProductModel implements Serializable {
    protected String imagePath, name, type, description;
    protected HashMap<String, Integer> ingredients;

    /**
     * Default constructor of ProductModel
     */
    public ProductModel() {}

    /**
     * Instantiates a ProductModel without an image saved
     * @param p Product - item to be stored
     */
    public ProductModel(Product p) {
        this.imagePath = "ingredient.png";
        this.name = p.getName();
        this.type = p.getType();
        this.description = p.getDescription();
        this.ingredients = p.getIngredients();
    }

    /**
     * Instantiates a ProductModel with an image saved
     * @param p Product - item to be stored
     * @param userId String - root directory of file path
     * @param productId String - name of image
     */
    public ProductModel(Product p, String userId, String productId) {
        this.imagePath = userId + "/" + "products" + "/" + productId;
        this.name = p.getName();
        this.type = p.getType();
        this.description = p.getDescription();
        this.ingredients = p.getIngredients();
    }

    /**
     * Gets file path of product image
     * @return String - file path
     */
    public String getImagePath() { return this.imagePath; }

    /**
     * Gets name of product item
     * @return String - product name
     */
    public String getName() {
        return this.name;
    }

    /** Gets type of product item
     * @return String - product type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets description of product item
     * @return String - product description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets ingredients of product item
     * @return HashMap - list of ingredients and their quantities
     */
    public HashMap<String, Integer> getIngredients() {
        return this.ingredients;
    }
}
