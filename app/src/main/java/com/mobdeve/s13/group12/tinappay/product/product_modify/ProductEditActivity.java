package com.mobdeve.s13.group12.tinappay.product.product_modify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.mobdeve.s13.group12.tinappay.Collections;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductEditActivity extends AppCompatActivity {
    private ImageView ivImg;
    private EditText etName;
    private EditText etType;
    private EditText etPrice;
    private EditText etDescription;
    private TextView tvIngredients;
    private ImageButton ibEditIngredients;
    private Button btnAdd;
    private ProgressBar pbLoad;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_modify);

        bindComponents();
        initComponents();
        initFirebase();
    }

    private void bindComponents() {
        this.ivImg = findViewById(R.id.iv_pm_img);
        this.etName = findViewById(R.id.et_pm_name);
        this.etType = findViewById(R.id.et_pm_type);
        this.etPrice = findViewById(R.id.et_pm_price);
        this.etDescription = findViewById(R.id.et_pm_description);
        this.tvIngredients = findViewById(R.id.tv_pm_ingredients);
        this.ibEditIngredients = findViewById(R.id.ib_pm_edit_ingredient);
        this.btnAdd = findViewById(R.id.btn_pm_add);
        this.pbLoad = findViewById(R.id.pb_pm);
    }

    private void initComponents() {
        TextView title = findViewById(R.id.tv_pm_title);
        title.setText ("Edit Product");
        this.btnAdd.setText("Apply");

        Intent i = getIntent();

        int img = i.getIntExtra(Keys.KEY_ITEM_IMG, 0);
        String name = i.getStringExtra(Keys.KEY_ITEM_NAME);
        String type = i.getStringExtra(Keys.KEY_ITEM_TYPE);
        float price = i.getFloatExtra(Keys.KEY_ITEM_PRICE, 0);
        String description = i.getStringExtra(Keys.KEY_ITEM_DESCRIPTION);
        String ingredients = i.getStringArrayListExtra(Keys.KEY_PI_NAME).toString();

        this.ivImg.setImageResource(img);
        this.etName.setText(name);
        this.etType.setText(type);
        this.etPrice.setText(Float.toString(price));
        this.etDescription.setText(description);

        this.ibEditIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductEditActivity.this, SelectIngredientsActivity.class);
                startActivity(i);
            }
        });

        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String sPrice = etPrice.getText().toString().trim();
                float price = 0;
                if (!sPrice.isEmpty())
                    price = Float.parseFloat(sPrice);
                String description = etDescription.getText().toString().trim();
                ArrayList<String> ingredients = new ArrayList<>();
                for (int i = 1; i <= 5; i++)
                    ingredients.add ("Placeholder ingredient " + i);

                if (isValid(name, type, price, description, ingredients)) {
                    Product product = new Product(R.drawable.placeholder, name, type, price, description, ingredients);
                    storeProduct(product);
                }
            }
        });
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private boolean isValid (String name, String type, float price, String description, ArrayList<String> ingredients) {
        boolean valid = true;

        if (name.isEmpty()) {
            this.etName.setError("Required field");
            this.etName.requestFocus();
            valid = false;
        }

        else if (type.isEmpty()) {
            this.etType.setError("Required field");
            this.etType.requestFocus();
            valid = false;
        }

        else if (price <= 0) {
            this.etPrice.setError("Invalid price");
            this.etPrice.requestFocus();
            valid = false;
        }

        else if (description.isEmpty()) {
            this.etDescription.setError("Required field");
            this.etDescription.requestFocus();
            valid = false;
        }

        else if (ingredients.size() == 0) {
            this.tvIngredients.setError("No ingredients");
            this.tvIngredients.requestFocus();
            valid = false;
        }

        return valid;
    }

    private void storeProduct (Product product) {
        this.pbLoad.setVisibility(View.VISIBLE);

        HashMap update = new HashMap();
        update.put(product.getId(), product);

        db.getReference(Collections.products.name())
            .child(this.userId)
            .updateChildren(update)
            .addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Log.i("Update", "Product update success.");
                    updateSuccess(product);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Update", "Product update failed.");
                    updateFail();
                }
            });
    }

    private void updateSuccess(Product product) {
        Intent i = new Intent();

        i.putExtra(Keys.KEY_ITEM_IMG, product.getImageId());
        i.putExtra(Keys.KEY_ITEM_NAME, product.getName());
        i.putExtra(Keys.KEY_ITEM_TYPE, product.getType());
        i.putExtra(Keys.KEY_ITEM_PRICE, product.getPrice());
        i.putExtra(Keys.KEY_ITEM_DESCRIPTION, product.getDescription());
        i.putExtra(Keys.KEY_PI_NAME, product.getIngredients());

        setResult(Activity.RESULT_OK, i);

        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductEditActivity.this, "Update success.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductEditActivity.this, "Update failed.", Toast.LENGTH_SHORT).show();
    }
}