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
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductEditActivity extends AppCompatActivity {
    // Activity Elements
    private ImageView ivImg;
    private EditText etName;
    private EditText etType;
    private EditText etPrice;
    private EditText etDescription;
    private TextView tvIngredients;
    private ImageButton ibSelectIngredients;
    private Button btnSubmit;
    private ProgressBar pbLoad;

    // Back-end code
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private String productId;

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
        this.ibSelectIngredients = findViewById(R.id.ib_pm_edit_ingredient);
        this.btnSubmit = findViewById(R.id.btn_pm_submit);
        this.pbLoad = findViewById(R.id.pb_pm);
    }

    private void initComponents() {
        // Changes layout template text
        TextView title = findViewById(R.id.tv_pm_title);
        title.setText (R.string.pm_edit);
        this.btnSubmit.setText(R.string.pm_apply);

        // Pre-places values into layout elements
        Intent i = getIntent();
        this.productId = i.getStringExtra(Keys.P_ID);

        int img = i.getIntExtra(Keys.P_IMG, 0);
        String name = i.getStringExtra(Keys.P_NAME);
        String type = i.getStringExtra(Keys.P_TYPE);
        float price = i.getFloatExtra(Keys.P_PRICE, 0);
        String description = i.getStringExtra(Keys.P_DESC);
        String ingredients = i.getStringArrayListExtra(Keys.PI_NAME).toString();

        this.ivImg.setImageResource(img);
        this.etName.setText(name);
        this.etType.setText(type);
        this.etPrice.setText(Float.toString(price));
        this.etDescription.setText(description);

        // TODO: Display ingredient list

        // Initialize ingredient selector button
        this.ibSelectIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductEditActivity.this, SelectIngredientsActivity.class);
                startActivity(i);
            }
        });

        // Initialize submit button
        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String sPrice = etPrice.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                float price = 0;
                if (!sPrice.isEmpty())
                    price = Float.parseFloat(sPrice);

                // TODO: Get list of ingredients
                ArrayList<String> ingredients = new ArrayList<>();
                ingredients.add("00aed0b0");
                ingredients.add("b14c4fe6");
                ingredients.add("dcb32aed");

                // Sends update if values are valid
                if (isValid(name, type, price, description, ingredients)) {
                    Product p = new Product(R.drawable.placeholder, name, type, price, description, ingredients);
                    p.setId(productId);

                    storeProduct(p);
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

        if (ingredients.size() == 0) {
            this.tvIngredients.setError("No ingredients");
            this.tvIngredients.requestFocus();
            valid = false;
        }

        else if (description.isEmpty()) {
            this.etDescription.setError("Required field");
            this.etDescription.requestFocus();
            valid = false;
        }

        else if (price <= 0) {
            this.etPrice.setError("Invalid price");
            this.etPrice.requestFocus();
            valid = false;
        }

        else if (type.isEmpty()) {
            this.etType.setError("Required field");
            this.etType.requestFocus();
            valid = false;
        }

        else if (name.isEmpty()) {
            this.etName.setError("Required field");
            this.etName.requestFocus();
            valid = false;
        }

        return valid;
    }

    private void storeProduct (Product p) {
        this.pbLoad.setVisibility(View.VISIBLE);

        HashMap update = new HashMap();
        update.put(this.productId, p);

        db.getReference(Collections.products.name())
            .child(this.userId)
            .updateChildren(update)
            .addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    updateSuccess(p);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    updateFail();
                }
            });
    }

    private void updateSuccess(Product p) {
        Intent i = new Intent();

        i.putExtra(Keys.P_ID, p.getId());
        i.putExtra(Keys.P_IMG, p.getImg());
        i.putExtra(Keys.P_NAME, p.getName());
        i.putExtra(Keys.P_TYPE, p.getType());
        i.putExtra(Keys.P_PRICE, p.getPrice());
        i.putExtra(Keys.P_DESC, p.getDescription());
        i.putExtra(Keys.PI_NAME, p.getIngredients());

        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductEditActivity.this, "Product updated.", Toast.LENGTH_SHORT).show();

        setResult(Activity.RESULT_OK, i);
        finish();
    }

    private void updateFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductEditActivity.this, "Product could not be updated.", Toast.LENGTH_SHORT).show();
    }
}