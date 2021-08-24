package com.mobdeve.s13.group12.tinappay.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {
    private TextView tvTitle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        bindComponents();
        initComponents();
        initCartButton();
        initRecyclerView();
    }

    private void bindComponents() {
        this.tvTitle = findViewById(R.id.tv_p_title);
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