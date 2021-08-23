package com.mobdeve.s13.group12.tinappay.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mobdeve.s13.group12.tinappay.checklist.ChecklistActivity;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.product.product_list.ProductsListActivity;

public class    HomeActivity extends AppCompatActivity {
    private Group grpProducts;
    private Group grpIngredients;
    private Group grpChecklist;
    private Group grpAccountSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        assignComponents();
        initComponents();
    }

    private void assignComponents() {
        this.grpProducts = findViewById(R.id.grp_h_products);
        this.grpIngredients = findViewById(R.id.grp_h_ingredients);
        this.grpChecklist = findViewById(R.id.grp_h_checklist);
        this.grpAccountSettings = findViewById(R.id.grp_h_account_settings);
    }

    private void initComponents() {
        int[] productsIds = grpProducts.getReferencedIds();
        int[] ingredientsIds = grpIngredients.getReferencedIds();
        int[] checklistIds = grpChecklist.getReferencedIds();
        int[] accountSettingsIds = grpAccountSettings.getReferencedIds();

        // For "Products"
        for (int id : productsIds)
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, ProductsListActivity.class);
                    startActivity(intent);
                }
            });

        // For "Ingredients"
        for (int id : ingredientsIds)
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(HomeActivity.this, <>Activity.class);
                    //startActivity(intent);
                }
            });

        // For "Checklist"
        for (int id : checklistIds)
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, ChecklistActivity.class);
                    startActivity(intent);
                }
            });

        // For "Account and Settings"
        for (int id : accountSettingsIds)
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(HomeActivity.this, <>Activity.class);
                    //startActivity(intent);
                }
            });
    }
}