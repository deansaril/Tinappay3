package com.mobdeve.s13.group12.tinappay.product.product_add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.Collections;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.account.RegisterActivity;
import com.mobdeve.s13.group12.tinappay.account.User;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.product_list.ProductsListActivity;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import java.util.ArrayList;

public class ProductAddActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_product_add);

        bindComponents();
        initComponents();
        initFirebase();
    }

    private void bindComponents() {
        this.etName = findViewById(R.id.et_pa_name);
        this.etType = findViewById(R.id.et_pa_type);
        this.etPrice = findViewById(R.id.et_pa_price);
        this.etDescription = findViewById(R.id.et_pa_description);
        this.tvIngredients = findViewById(R.id.tv_pa_ingredients);
        this.ibEditIngredients = findViewById(R.id.ib_pa_edit_ingredient);
        this.btnAdd = findViewById(R.id.et_btn_add);
        this.pbLoad = findViewById(R.id.pb_pa);
    }

    private void initComponents() {
        this.ibEditIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductAddActivity.this, SelectIngredientsActivity.class);
                startActivity(i);
            }
        });

        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                float price = Float.parseFloat(etPrice.getText().toString().trim());
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
        this.userId = this.mAuth.getCurrentUser().getUid();
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

        db.getReference(Collections.users.name())
                //.child(this.userId)
                .child("lY0dNpmmU5cfGWtjUmuTPGseZsY2")
                .child(Collections.products.name())
                .child(product.getId())
                .setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
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