package com.mobdeve.s13.group12.tinappay.product.product_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.product_add.ProductAddActivity;

import java.util.ArrayList;

public class ProductsListActivity extends AppCompatActivity {
    private ImageButton btnAdd;
    private RecyclerView rvProductsList;
    private GridLayoutManager glmManager;
    private ArrayList<Product> data;
    private ProductsListAdapter productsListAdapter;

    /*
    private ActivityResultLauncher addActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent i = result.getData();

                    String caption = i.getStringExtra(AddTweetActivity.KEY_CAPTION);

                    data.add(0, new Tweet("Janicon", "Jan Canicon", caption));
                    tweetAdapter.notifyItemChanged(0);
                    tweetAdapter.notifyItemRangeChanged(0, tweetAdapter.getItemCount());
                }
            }
        }
    );
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initBtnAdd();
        initRecyclerView();
    }

    private void initBtnAdd() {
        this.btnAdd = findViewById(R.id.ib_pl_add);
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(ProductsListActivity.this, ProductAddActivity.class);

                //addActivityResultLauncher.launch(i);
                startActivity(i);
            }
        });
    }

    private void initRecyclerView () {
        this.rvProductsList = findViewById(R.id.rv_pl);

        this.glmManager = new GridLayoutManager(this, 2);
        this.rvProductsList.setLayoutManager(this.glmManager);

        data = new ArrayList<Product>();
        for (int i = 1; i <= 15; i++) {
            String name = "Item " + i;
            String description = "This is the description for " + name;
            description += ".\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
            float price = 100 * i;
            ArrayList<String> ingredients = new ArrayList<>();
            for (int j = 0; j < i; j++)
                ingredients.add("Ingredient " + (j + 1));
            data.add(new Product(R.drawable.placeholder, name, "Item", price, description, ingredients));
        }

        this.productsListAdapter = new ProductsListAdapter(this.data);
        this.rvProductsList.setAdapter(this.productsListAdapter);
    }
}