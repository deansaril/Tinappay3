package com.mobdeve.s13.group12.tinappay.product.product_modify;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import java.util.HashMap;

public class ProductAddActivity extends AppCompatActivity {
    // Activity Elements
    private ImageView ivImg;
    private EditText etName;
    private EditText etType;
    private EditText etDescription;
    // TODO: Display for list of ingredients
    private TextView tvIngredients;
    private Button ibEditIngredients;
    private Button btnSubmit;
    private ProgressBar pbLoad;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private HashMap<String, String> names;
    private HashMap<String, Integer> quantities;

    private ActivityResultLauncher selectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = result.getData();

                        quantities = (HashMap<String, Integer>)i.getSerializableExtra(Keys.KEY_SELECT_INGREDIENTS.name());
                        // TODO: Pass list of ingredient names
                    }
                }
            }
    );

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
        this.etDescription = findViewById(R.id.et_pm_description);
        this.ibEditIngredients = findViewById(R.id.btn_pm_edit_ingredient);
        this.btnSubmit = findViewById(R.id.btn_pm_submit);
        this.pbLoad = findViewById(R.id.pb_pm);
    }

    private void initComponents() {
        // Changes layout template text
        TextView title = findViewById(R.id.tv_pm_title);
        title.setText (R.string.pm_add);
        this.btnSubmit.setText(R.string.pm_add);

        this.quantities = new HashMap<>();

        initSelectBtn();
        initAddBtn();
    }

    private void initSelectBtn() {
        // Initialize ingredient selector button
        this.ibEditIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductAddActivity.this, SelectIngredientsActivity.class);
                i.putExtra(Keys.KEY_SELECT_INGREDIENTS.name(), quantities);

                selectActivityResultLauncher.launch(i);
            }
        });
    }

    private void initAddBtn() {
        // Initialize submit button
        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                // Sends update if values are valid
                if (isValid(name, type, description, quantities)) {
                    Product p = new Product(R.drawable.placeholder, name, type, description, quantities);
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

    private boolean isValid (String name, String type, String description, HashMap<String, Integer> ingredients) {
        boolean valid = true;

        if (ingredients.isEmpty()) {
            this.tvIngredients.setError("No ingredients");
            this.tvIngredients.requestFocus();
            valid = false;
        }

        if (description.isEmpty()) {
            this.etDescription.setError("Required field");
            this.etDescription.requestFocus();
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