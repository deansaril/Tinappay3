package com.mobdeve.s13.group12.tinappay.product.product_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.DatabaseHelper;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductAddActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProductsListActivity extends AppCompatActivity {
    private ImageButton btnAdd;
    private RecyclerView rvProductsList;
    private GridLayoutManager glmManager;
    private ArrayList<Product> data;
    private ProductsListAdapter productsListAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

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

        initFirebase();
        initBtnAdd();
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        productsListAdapter.notifyDataSetChanged();
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

        // Populates products; TODO NOTE START: Remove in final release
        db.getReference(Collections.products.name())
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    Log.i("UserProduct", "No products. Pre-populating database...");
                    DatabaseHelper.loadProducts(userId);
                }
                else
                    Log.i("UserProduct", "User products found.");
            }
        // TODO NOTE END

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Products List", "Could not retrieve from database.");
            }
        });

        fetchItems();

        // TODO: If data.size == 0, display notice, else create adapter

        this.productsListAdapter = new ProductsListAdapter(this.data);
        this.rvProductsList.setAdapter(this.productsListAdapter);
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private void fetchItems() {
        data = new ArrayList<>();

        db.getReference(Collections.products.name())
                .child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren())
                    data.add(postSnapshot.getValue(Product.class));
                productsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Products List", "Could not retrieve from database.");
            }
        });
    }
}