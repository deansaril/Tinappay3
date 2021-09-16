package com.mobdeve.s13.group12.tinappay.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mobdeve.s13.group12.tinappay.account.AccountActivity;
import com.mobdeve.s13.group12.tinappay.checklist.ChecklistActivity;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.ingredient.ingredient_list.IngredientsListActivity;
import com.mobdeve.s13.group12.tinappay.product.product_list.ProductsListActivity;

/**
 * This activity handles user navigation between different activities
 */
public class HomeActivity extends AppCompatActivity {

    /* Class variables */
    private Group grpProducts;
    private Group grpIngredients;
    private Group grpChecklist;
    private Group grpAccountSettings;



    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindComponents();
        initComponents();
    }



    /* Class functions */
    /**
     * Retrieves activity elements from layout and binds them to the activity
     */
    private void bindComponents() {
        this.grpProducts = findViewById(R.id.grp_h_products);
        this.grpIngredients = findViewById(R.id.grp_h_ingredients);
        this.grpChecklist = findViewById(R.id.grp_h_checklist);
        this.grpAccountSettings = findViewById(R.id.grp_h_account_settings);
    }

    /**
     * Initializes variables used in the activity
     * Initializes functionality of activity
     */
    private void initComponents() {
        int[] productsIds = grpProducts.getReferencedIds();
        int[] ingredientsIds = grpIngredients.getReferencedIds();
        int[] checklistIds = grpChecklist.getReferencedIds();
        int[] accountSettingsIds = grpAccountSettings.getReferencedIds();

        // Sets listener to move to "Products" activity
        for (int id : productsIds)
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, ProductsListActivity.class);
                    startActivity(intent);
                }
            });

        // Sets listener to move to "Ingredients" activity
        for (int id : ingredientsIds)
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, IngredientsListActivity.class);
                    startActivity(intent);
                }
            });

        // Sets listener to move to "Checklist" activity
        for (int id : checklistIds)
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, ChecklistActivity.class);
                    startActivity(intent);
                }
            });

        // Sets listener to move to "Account and Settings" activity
        for (int id : accountSettingsIds)
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                    startActivity(intent);
                }
            });
    }
}