package com.mobdeve.s13.group12.tinappay.product.product_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.product_add.ProductAddActivity;

import java.util.ArrayList;

public class ProductsListActivity extends AppCompatActivity {
    private Button btnAdd;
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
        this.btnAdd = findViewById(R.id.btn_pl_add);
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
            float price = 100 * i;
            data.add(new Product(R.drawable.placeholder, name, "Item", price));
        }

        this.productsListAdapter = new ProductsListAdapter(this.data);
        this.rvProductsList.setAdapter(this.productsListAdapter);
    }
}