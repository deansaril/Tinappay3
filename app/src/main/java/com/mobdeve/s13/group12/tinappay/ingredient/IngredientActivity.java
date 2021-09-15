package com.mobdeve.s13.group12.tinappay.ingredient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.R;

import com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify.IngredientEditActivity;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.product.ProductActivity;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductEditActivity;

import java.util.ArrayList;
import java.util.UUID;


public class IngredientActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvLocation;
    private ImageButton ibEdit;
    private ImageButton ibCart;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

    private ActivityResultLauncher editActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = result.getData();
                        setIntent(i);

                        int img = i.getIntExtra(Keys.KEY_INGREDIENT_IMG, 0);
                        String name = i.getStringExtra(Keys.KEY_INGREDIENT_NAME);
                        String type = i.getStringExtra(Keys.KEY_INGREDIENT_TYPE);
                        float price = i.getFloatExtra(Keys.KEY_INGREDIENT_PRICE, 0);
                        String location = i.getStringExtra(Keys.KEY_INGREDIENT_LOCATION);

                        tvTitle.setText(name);
                        ivImg.setImageResource(img);
                        tvName.setText(name);
                        tvType.setText(type);
                        tvPrice.setText(Float.toString(price));
                        tvLocation.setText(location);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        initFirebase();
        bindComponents();
        initComponents();
        initCartButton();
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private void bindComponents() {
        this.tvTitle = findViewById(R.id.tv_i_title);

        this.ivImg = findViewById(R.id.iv_i_img);
        this.tvName = findViewById(R.id.tv_i_name);
        this.tvType = findViewById(R.id.tv_i_type);
        this.tvPrice = findViewById(R.id.tv_i_price);
        this.tvLocation = findViewById(R.id.tv_i_location);
        this.ibEdit = findViewById(R.id.ib_i_edit);
        this.ibCart = findViewById(R.id.ib_i_cart);

    }

    private void initComponents() {
        Intent i = getIntent();

        int img = i.getIntExtra(Keys.KEY_INGREDIENT_IMG, 0);
        String name = i.getStringExtra(Keys.KEY_INGREDIENT_NAME);
        String type = i.getStringExtra(Keys.KEY_INGREDIENT_TYPE);
        float price = i.getFloatExtra(Keys.KEY_INGREDIENT_PRICE, 0);
        String location = i.getStringExtra(Keys.KEY_INGREDIENT_LOCATION);

        this.tvTitle.setText(name);
        this.ivImg.setImageResource(img);
        this.tvName.setText(name);
        this.tvType.setText(type);
        this.tvPrice.setText(Float.toString(price));
        this.tvLocation.setText(location);

        initEditButton();
    }

    private void initEditButton() {
        this.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldIntent = getIntent();
                Intent newIntent = new Intent(IngredientActivity.this, IngredientEditActivity.class);

                newIntent.putExtra(Keys.KEY_INGREDIENT_ID, oldIntent.getStringExtra(Keys.KEY_INGREDIENT_ID));
                newIntent.putExtra(Keys.KEY_INGREDIENT_IMG, oldIntent.getIntExtra(Keys.KEY_INGREDIENT_IMG, 0));
                newIntent.putExtra(Keys.KEY_INGREDIENT_NAME, oldIntent.getStringExtra(Keys.KEY_INGREDIENT_NAME));
                newIntent.putExtra(Keys.KEY_INGREDIENT_TYPE, oldIntent.getStringExtra(Keys.KEY_INGREDIENT_TYPE));
                newIntent.putExtra(Keys.KEY_INGREDIENT_PRICE, oldIntent.getFloatExtra(Keys.KEY_INGREDIENT_PRICE, 0));
                newIntent.putExtra(Keys.KEY_INGREDIENT_LOCATION, oldIntent.getStringExtra(Keys.KEY_INGREDIENT_LOCATION));

                editActivityResultLauncher.launch(newIntent);
            }
        });
    }

    private void initCartButton() {
        this.ibCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChecklist();
            }
        });
    }

    private void addChecklist() {
        Intent i = getIntent();

        String name = i.getStringExtra(Keys.KEY_INGREDIENT_NAME);
        String id = UUID.randomUUID().toString().replace("-","").substring(0,8);
        ChecklistItem item = new ChecklistItem(name, false);
        db.getReference(Collections.checklist.name())
                .child(this.userId)
                .child(id)
                .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    addSuccess();
                else
                    addFail();
            }
        });
    }

    private void addSuccess() {
        //this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientActivity.this, "Ingredient added to checklist.", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void addFail() {
        //this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientActivity.this, "Could not add to checklist.", Toast.LENGTH_SHORT).show();
    }
}