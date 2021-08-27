package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductEditActivity;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class IngredientEditActivity extends AppCompatActivity {

    // Activity Elements
    private ImageView ivImg;
    private EditText etName;
    private EditText etType;
    private EditText etPrice;
    private EditText etLocation;
    private Button btnSubmit;
    private ProgressBar pbLoad;

    // Back-end code
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private String ingredientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_modify);

        bindComponents();
        initComponents();
        initFirebase();
    }

    private void bindComponents() {

        this.ivImg = findViewById(R.id.iv_im_image);
        this.etName = findViewById(R.id.et_im_name);
        this.etType = findViewById(R.id.et_im_type);
        this.etPrice = findViewById(R.id.et_im_price);
        this.etLocation = findViewById(R.id.et_im_location);
        this.btnSubmit = findViewById(R.id.btn_im_modify);
        this.pbLoad = findViewById(R.id.pb_im);
    }

    private void initComponents() {
        // Changes layout template text
        TextView title = findViewById(R.id.tv_im_title);
        title.setText (R.string.im_edit);
        this.btnSubmit.setText("Apply");

        // Pre-places values into layout elements
        Intent i = getIntent();
        this.ingredientId = i.getStringExtra(Keys.KEY_INGREDIENT_ID);

        int img = i.getIntExtra(Keys.KEY_INGREDIENT_IMG, 0);
        String name = i.getStringExtra(Keys.KEY_INGREDIENT_NAME);
        String type = i.getStringExtra(Keys.KEY_INGREDIENT_TYPE);
        float price = i.getFloatExtra(Keys.KEY_INGREDIENT_PRICE, 0);
        String location = i.getStringExtra(Keys.KEY_INGREDIENT_LOCATION);

        this.ivImg.setImageResource(img);
        this.etName.setText(name);
        this.etType.setText(type);
        this.etPrice.setText(Float.toString(price));
        this.etLocation.setText(location);

        // Initialize submit button
        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String sPrice = etPrice.getText().toString().trim();
                String location = etLocation.getText().toString().trim();

                float price = 0;
                if (!sPrice.isEmpty())
                    price = Float.parseFloat(sPrice);

                // TODO: Get list of ingredients
                ArrayList<String> ingredients = new ArrayList<>();
                for (int i = 1; i <= 5; i++)
                    ingredients.add ("Placeholder ingredient " + i);

                // Sends update if values are valid
                if (!checkEmpty(name, type, location, price)) {
                    Ingredient ingredient = new Ingredient(R.drawable.ingredient,name, type, location, price);
                    ingredient.setId(ingredientId);

                    storeIngredient(ingredient);
                }
            }
        });
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userId = this.mAuth.getCurrentUser().getUid();
        //this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private boolean checkEmpty (String name, String type, String location, float price) {
        boolean hasEmpty = false;

        if (location.isEmpty()) {
            this.etLocation.setError("Please Enter Location");
            this.etLocation.requestFocus();
            hasEmpty = true;
        }

        if (price <= 0) {
            this.etPrice.setError("Invalid price");
            this.etPrice.requestFocus();
            hasEmpty = true;
        }

        if (type.isEmpty()) {
            this.etType.setError("Required field");
            this.etType.requestFocus();
            hasEmpty = true;
        }

        if (name.isEmpty()) {
            this.etName.setError("Required field");
            this.etName.requestFocus();
            hasEmpty = true;
        }

        return hasEmpty;
    }

    private void storeIngredient (Ingredient ingredient) {
        this.pbLoad.setVisibility(View.VISIBLE);

        HashMap update = new HashMap();
        update.put(this.ingredientId, ingredient);

        db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .updateChildren(update)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        updateSuccess(ingredient);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateFail();
                    }
                });
    }

    private void updateSuccess(Ingredient ingredient) {
        Intent i = new Intent();

        i.putExtra(Keys.KEY_INGREDIENT_ID, ingredient.getId());
        i.putExtra(Keys.KEY_INGREDIENT_IMG, ingredient.getImageId());
        i.putExtra(Keys.KEY_INGREDIENT_NAME, ingredient.getName());
        i.putExtra(Keys.KEY_INGREDIENT_TYPE, ingredient.getType());
        i.putExtra(Keys.KEY_INGREDIENT_PRICE, ingredient.getPrice());
        i.putExtra(Keys.KEY_INGREDIENT_LOCATION, ingredient.getLocation());

        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientEditActivity.this, "INGREDIENT UPDATED", Toast.LENGTH_SHORT).show();

        setResult(Activity.RESULT_OK, i);
        finish();
    }

    private void updateFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientEditActivity.this, "INGREDIENT CANNOT BE UPDATED", Toast.LENGTH_SHORT).show();
    }

}