package com.mobdeve.s13.group12.tinappay.product.product_add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

public class ProductAddActivity extends AppCompatActivity {
    private ImageButton ibEditIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        bindComponents();
        initComponents();
    }

    private void bindComponents() {
        this.ibEditIngredients = findViewById(R.id.ib_pa_edit_ingredient);
    }

    private void initComponents() {
        this.ibEditIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductAddActivity.this, SelectIngredientsActivity.class);
                startActivity(i);
            }
        });
    }
}