package com.mobdeve.s13.group12.tinappay.product;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.mobdeve.s13.group12.tinappay.checklist.ChecklistActivity;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductAddActivity;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductEditActivity;

import java.util.ArrayList;
import java.util.UUID;

public class ProductActivity extends AppCompatActivity {
    private TextView tvTitle;
    private ImageButton ibSettings;
    private ImageButton ibCart;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvDescription;
    private TextView tvIngredients;

    private RecyclerView rvIngredientsList;
    private LinearLayoutManager llmManager;
    private ArrayList<String> dataNames;
    private float[] dataPrices;
    private ProductAdapter productAdapter;

    // Back-end code
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

                        int img = i.getIntExtra(Keys.P_IMG, 0);
                        String name = i.getStringExtra(Keys.P_NAME);
                        String type = i.getStringExtra(Keys.P_TYPE);
                        float price = i.getFloatExtra(Keys.P_PRICE, 0);
                        String description = i.getStringExtra(Keys.P_DESC);
                        String ingredients = i.getStringArrayListExtra(Keys.PI_NAME).toString();

                        tvTitle.setText(name);
                        ivImg.setImageResource(img);
                        tvName.setText(name);
                        tvType.setText(type);
                        tvPrice.setText(Float.toString(price));
                        tvDescription.setText(description);
                        tvIngredients.setText(ingredients);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        bindComponents();
        initComponents();
        initFirebase();
        initRecyclerView();
    }

    private void bindComponents() {
        this.tvTitle = findViewById(R.id.tv_p_title);
        this.ibSettings = findViewById(R.id.ib_p_settings);
        this.ibCart = findViewById(R.id.ib_p_cart);
        this.ivImg = findViewById(R.id.iv_p_img);
        this.tvName = findViewById(R.id.tv_p_name);
        this.tvType = findViewById(R.id.tv_p_type);
        this.tvPrice = findViewById(R.id.tv_p_price);
        this.tvDescription = findViewById(R.id.tv_p_description);
        this.tvIngredients = findViewById(R.id.tv_p_ingredients);
    }

    private void initComponents() {
        Intent i = getIntent();

        int img = i.getIntExtra(Keys.P_IMG, 0);
        String name = i.getStringExtra(Keys.P_NAME);
        String type = i.getStringExtra(Keys.P_TYPE);
        float price = i.getFloatExtra(Keys.P_PRICE, 0);
        String description = i.getStringExtra(Keys.P_DESC);
        String ingredients = i.getStringArrayListExtra(Keys.PI_NAME).toString();

        this.tvTitle.setText(name);
        this.ivImg.setImageResource(img);
        this.tvName.setText(name);
        this.tvType.setText(type);
        this.tvPrice.setText(Float.toString(price));
        this.tvDescription.setText(description);
        this.tvIngredients.setText(ingredients);

        initEditButton();
        initCartButton();
    }

    private void initEditButton() {
        this.ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldIntent = getIntent();
                Intent newIntent = new Intent(ProductActivity.this, ProductEditActivity.class);

                newIntent.putExtra(Keys.P_ID, oldIntent.getStringExtra(Keys.P_ID));
                newIntent.putExtra(Keys.P_IMG, oldIntent.getIntExtra(Keys.P_IMG, 0));
                newIntent.putExtra(Keys.P_NAME, oldIntent.getStringExtra(Keys.P_NAME));
                newIntent.putExtra(Keys.P_TYPE, oldIntent.getStringExtra(Keys.P_TYPE));
                newIntent.putExtra(Keys.P_PRICE, oldIntent.getFloatExtra(Keys.P_PRICE, 0));
                newIntent.putExtra(Keys.P_DESC, oldIntent.getStringExtra(Keys.P_DESC));
                newIntent.putExtra(Keys.PI_NAME, oldIntent.getStringArrayListExtra(Keys.PI_NAME));

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

    private void initRecyclerView() {
        this.rvIngredientsList = findViewById(R.id.rv_p_ingredients);

        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvIngredientsList.setLayoutManager(this.llmManager);

        Intent i = getIntent();
        dataNames = i.getStringArrayListExtra(Keys.PI_NAME);
        dataPrices = i.getFloatArrayExtra(Keys.PI_PRICE);

        this.productAdapter = new ProductAdapter(this.dataNames, this.dataPrices);
        this.rvIngredientsList.setAdapter(this.productAdapter);
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userId = this.mAuth.getCurrentUser().getUid();
        //this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private void addChecklist() {
        //this.pbLoad.setVisibility(View.VISIBLE);
        Intent i = getIntent();
        ArrayList<String> ingredients = i.getStringArrayListExtra(Keys.PI_NAME);

        for (String name : ingredients) {
            String id = UUID.randomUUID().toString().replace("-","").substring(0,8);
            ChecklistItem item = new ChecklistItem(name, false);

            db.getReference(Collections.checklist.name())
                    .child(this.userId)
                    .child(id)
                    .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Checklist Add", "Added " + name);
                }
            });
        }
    }

    private void addSuccess() {
        //this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductActivity.this, "Add to checklist.", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void addFail() {
        //this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductActivity.this, "Could not add to checklist.", Toast.LENGTH_SHORT).show();
    }
}