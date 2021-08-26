package com.mobdeve.s13.group12.tinappay.product;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.checklist.ChecklistActivity;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.product_list.ProductsListAdapter;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductEditActivity;

import java.util.ArrayList;

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

    private ActivityResultLauncher editActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = result.getData();
                        setIntent(i);

                        int img = i.getIntExtra(Keys.KEY_ITEM_IMG, 0);
                        String name = i.getStringExtra(Keys.KEY_ITEM_NAME);
                        String type = i.getStringExtra(Keys.KEY_ITEM_TYPE);
                        float price = i.getFloatExtra(Keys.KEY_ITEM_PRICE, 0);
                        String description = i.getStringExtra(Keys.KEY_ITEM_DESCRIPTION);
                        String ingredients = i.getStringArrayListExtra(Keys.KEY_PI_NAME).toString();

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

        int img = i.getIntExtra(Keys.KEY_ITEM_IMG, 0);
        String name = i.getStringExtra(Keys.KEY_ITEM_NAME);
        String type = i.getStringExtra(Keys.KEY_ITEM_TYPE);
        float price = i.getFloatExtra(Keys.KEY_ITEM_PRICE, 0);
        String description = i.getStringExtra(Keys.KEY_ITEM_DESCRIPTION);
        String ingredients = i.getStringArrayListExtra(Keys.KEY_PI_NAME).toString();

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

                newIntent.putExtra(Keys.KEY_ITEM_IMG, oldIntent.getIntExtra(Keys.KEY_ITEM_IMG, 0));
                newIntent.putExtra(Keys.KEY_ITEM_NAME, oldIntent.getStringExtra(Keys.KEY_ITEM_NAME));
                newIntent.putExtra(Keys.KEY_ITEM_TYPE, oldIntent.getStringExtra(Keys.KEY_ITEM_TYPE));
                newIntent.putExtra(Keys.KEY_ITEM_PRICE, oldIntent.getFloatExtra(Keys.KEY_ITEM_PRICE, 0));
                newIntent.putExtra(Keys.KEY_ITEM_DESCRIPTION, oldIntent.getStringExtra(Keys.KEY_ITEM_DESCRIPTION));
                newIntent.putExtra(Keys.KEY_PI_NAME, oldIntent.getStringArrayListExtra(Keys.KEY_PI_NAME));

                editActivityResultLauncher.launch(newIntent);
            }
        });
    }

    private void initCartButton() {
        this.ibCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductActivity.this, ChecklistActivity.class);
                startActivity(i);
            }
        });
    }

    private void initRecyclerView() {
        this.rvIngredientsList = findViewById(R.id.rv_p_ingredients);

        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvIngredientsList.setLayoutManager(this.llmManager);

        Intent i = getIntent();
        dataNames = i.getStringArrayListExtra(Keys.KEY_PI_NAME);
        dataPrices = i.getFloatArrayExtra(Keys.KEY_PI_PRICE);

        this.productAdapter = new ProductAdapter(this.dataNames, this.dataPrices);
        this.rvIngredientsList.setAdapter(this.productAdapter);
    }
}