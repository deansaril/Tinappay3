package com.mobdeve.s13.group12.tinappay.product.product_modify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.Collections;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import java.util.ArrayList;

public class ProductAddActivity extends AppCompatActivity {
    // Activity Elements
    private EditText etName;
    private EditText etType;
    private EditText etPrice;
    private EditText etDescription;
    private TextView tvIngredients;
    private ImageButton ibEditIngredients;
    private Button btnSubmit;
    private ProgressBar pbLoad;

    // Back-end code
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
        this.etName = findViewById(R.id.et_pm_name);
        this.etType = findViewById(R.id.et_pm_type);
        this.etPrice = findViewById(R.id.et_pm_price);
        this.etDescription = findViewById(R.id.et_pm_description);
        this.tvIngredients = findViewById(R.id.tv_pm_ingredients);
        this.ibEditIngredients = findViewById(R.id.ib_pm_edit_ingredient);
        this.btnSubmit = findViewById(R.id.btn_pm_submit);
        this.pbLoad = findViewById(R.id.pb_pm);
    }

    private void initComponents() {
        // Changes layout template text
        TextView title = findViewById(R.id.tv_pm_title);
        title.setText (R.string.pm_add);
        this.btnSubmit.setText(R.string.pm_add);

        // TODO: Display ingredient list

        // Initialize ingredient selector button
        this.ibEditIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductAddActivity.this, SelectIngredientsActivity.class);
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
                for (int i = 1; i <= 5; i++)
                    ingredients.add ("Placeholder ingredient " + i);

                // Sends update if values are valid
                if (isValid(name, type, price, description, ingredients)) {
                    Product p = new Product(R.drawable.placeholder, name, type, price, description, ingredients);
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

        if (description.isEmpty()) {
            this.etDescription.setError("Required field");
            this.etDescription.requestFocus();
            valid = false;
        }

        if (price <= 0) {
            this.etPrice.setError("Invalid price");
            this.etPrice.requestFocus();
            valid = false;
        }

        if (type.isEmpty()) {
            this.etType.setError("Required field");
            this.etType.requestFocus();
            valid = false;
        }

        if (name.isEmpty()) {
            this.etName.setError("Required field");
            this.etName.requestFocus();
            valid = false;
        }

        return valid;
    }

    private void storeProduct (Product p) {
        this.pbLoad.setVisibility(View.VISIBLE);

        db.getReference(Collections.products.name())
                .child(this.userId)
                .child(p.getId())
                .setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    addSuccess();
                else
                    addFail();
            }
        });
    }

    private void addSuccess() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductAddActivity.this, "Add success.", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void addFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductAddActivity.this, "Add failed.", Toast.LENGTH_SHORT).show();
    }
}