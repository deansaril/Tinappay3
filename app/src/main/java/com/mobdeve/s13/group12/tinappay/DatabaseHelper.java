package com.mobdeve.s13.group12.tinappay;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.objects.ProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class DatabaseHelper { // TODO: Remove in final release
    private static FirebaseDatabase db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
    private static String[] ingredientIDs = new String[]
            {"16df3309", "91b387b4", "df2a2cd2", "fa614603", "fb0b8766"};

    public static void loadProducts (String userId) {
        for (int i = 1; i <= 5; i++) {
            String name = "Product " + i;
            String description = "This is the description for " + name;
            description += ".\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
            HashMap<String, Integer> ingredients = new HashMap<>();
            for (int j = 0; j < 5; j++)
                ingredients.put(getRandomIngredient(), (j + 1));

            ProductModel p = new Product(name, "Item", description, ingredients);
            p = new ProductModel((Product)p);
            storeProduct(userId, p);
        }
    }

    private static String getRandomIngredient() {
        int index = Math.abs(new Random().nextInt());
        index %= ingredientIDs.length;

        return ingredientIDs[index];
    }

    private static void storeProduct (String userId, ProductModel product) {
        String productId = UUID.randomUUID().toString().replace("-","").substring(0,8);

        db.getReference(Collections.products.name())
                .child(userId)
                .child(productId)
                .setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.i("Database Helper", "Product [" + product.getName() + "] added.");
                else
                    Log.e("Database Helper", "Products [" + product.getName() + "] could not be added.");
            }
        });
    }

    public static void loadIngredients (String userId) {
        for (int i = 1; i <= 5; i++) {
            String name = "Ingredient " + i;
            float price = 100 * i;
            String location = "Location " + i;

            Ingredient ingredient = new Ingredient(name, "Item", location, price);
            storeIngredient(userId, ingredient);
        }
    }

    private static void storeIngredient (String userId, Ingredient ingredient) {
        db.getReference(Collections.ingredients.name())
                .child(userId)
                .child(ingredient.getId())
                .setValue(ingredient).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.i("Database Helper", "Product [" + ingredient.getName() + "] added.");
                else
                    Log.e("Database Helper", "Products [" + ingredient.getName() + "] could not be added.");
            }
        });
    }
}
