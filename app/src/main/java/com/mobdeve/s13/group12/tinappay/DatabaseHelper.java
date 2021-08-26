package com.mobdeve.s13.group12.tinappay;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.objects.Product;

import java.util.ArrayList;

public class DatabaseHelper { // TODO: Remove in final release
    private static FirebaseDatabase db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");

    public static void loadProducts (String userId) {
        for (int i = 1; i <= 15; i++) {
            String name = "Product " + i;
            String description = "This is the description for " + name;
            description += ".\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
            float price = 100 * i;
            ArrayList<String> ingredients = new ArrayList<>();
            int start = 0;
            if (i > 5)
                start = i - 5;

            for (int j = start; j < i; j++)
                ingredients.add("Ingredient " + (j + 1));

            Product p = new Product(R.drawable.placeholder, name, "Item", price, description, ingredients);
            storeProduct(userId, p);
        }
    }

    private static void storeProduct (String userId, Product product) {
        db.getReference(Collections.products.name())
                .child(userId)
                .child(product.getId())
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
}
