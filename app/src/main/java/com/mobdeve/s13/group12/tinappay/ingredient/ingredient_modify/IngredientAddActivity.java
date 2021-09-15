package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;

public class IngredientAddActivity extends AppCompatActivity {

    // TODO DEAN: CHANGE THIS BASED ON JAN'S NEW ADD
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_modify);

        bindComponents();
        initComponents();
        initFirebase();
    }
    private EditText etName;
    private EditText etType;
    private EditText etPrice;
    private EditText etLocation;
    private Button btnAdd;
    private ProgressBar pbLoad;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

    private void bindComponents() {
        this.etName = findViewById(R.id.et_im_name);
        this.etType = findViewById(R.id.et_im_type);
        this.etPrice = findViewById(R.id.et_im_price);
        this.etLocation = findViewById(R.id.et_im_location);
        this.btnAdd = findViewById(R.id.btn_im_modify);
        this.pbLoad = findViewById(R.id.pb_im);
    }

    private void initComponents() {
        TextView title = findViewById(R.id.tv_im_title);
        title.setText (R.string.im_add);
        this.btnAdd.setText(R.string.add);

        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String sPrice = etPrice.getText().toString().trim();
                float price = 0;
                if (!sPrice.isEmpty())
                    price = Float.parseFloat(sPrice);
                String location = etLocation.getText().toString().trim();

                if (!checkEmpty(name, type, location, price)){
                    Ingredient ingredient = new Ingredient(R.drawable.ingredient,name,type,location,price);
                    storeIngredient(ingredient);
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

    private boolean checkEmpty (String name, String type, String location, float price) {
        boolean hasEmpty = false;

        if (location.isEmpty()) {
            this.etLocation.setError("Required field");
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

        db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .child(ingredient.getId())
                .setValue(ingredient).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        Toast.makeText(IngredientAddActivity.this, "Add success.", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void addFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientAddActivity.this, "Add failed.", Toast.LENGTH_SHORT).show();
    }
}