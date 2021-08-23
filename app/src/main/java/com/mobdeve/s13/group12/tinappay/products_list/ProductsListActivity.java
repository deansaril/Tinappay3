package com.mobdeve.s13.group12.tinappay.products_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Product;

import java.util.ArrayList;

public class ProductsListActivity extends AppCompatActivity {
    private RecyclerView rvProductsList;
    private GridLayoutManager glmManager;
    private ArrayList<Product> data;
    private ProductsListAdapter productsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initRecyclerView();
    }

    private void initRecyclerView () {
        this.rvProductsList = findViewById(R.id.rv_pl);

        this.glmManager = new GridLayoutManager(this, 2);
        this.rvProductsList.setLayoutManager(this.glmManager);

        data = new ArrayList<Product>();
        for (int i = 1; i <= 15; i++) {
            String name = "Item" + i;
            float price = 100 * i;
            data.add(new Product(R.drawable.placeholder, name, "Item", price));
        }

        this.productsListAdapter = new ProductsListAdapter(this.data);
        this.rvProductsList.setAdapter(productsListAdapter);
    }
}