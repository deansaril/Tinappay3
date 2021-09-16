package com.mobdeve.s13.group12.tinappay.product.product_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s13.group12.tinappay.DatabaseHelper;
import com.mobdeve.s13.group12.tinappay.ProgressBarRunnable;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.objects.ProductModel;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductAddActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProductsListActivity extends AppCompatActivity {

    // Activity elements
    private ConstraintLayout clLoad;
    private ConstraintLayout clEmpty;
    private ConstraintLayout clFilter;
    private ImageButton btnFilter;
    private ImageButton btnAdd;
    private RecyclerView rvProductsList;

    // Loading screen
    private ProgressBar pbLoad;
    private TextView tvLoad;
    private long curProgress, totalProgress;

    // Filter
    private final String[] options = {"Name", "Type"};
    private boolean filterToggle;
    private Spinner spnFilter;
    private EditText etFilter;
    private Button btnCancelFilter;
    private Button btnApplyFilter;

    // RecyclerView
    private GridLayoutManager glmManager;
    private ArrayList<Product> data;
    private ProductsListAdapter productsListAdapter;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private StorageReference storageReference;
    private String userId;
    private String filterMode;
    private String filterQuery;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message message) {
            super.handleMessage(message);
            
            Bundle bundle = message.getData();

            // Set current progress
            int progress = bundle.getInt(Keys.KEY_LOAD.name());
            //int progress = bundle.getInt(KeysOld.KEY_PROGRESS);
            pbLoad.setProgress(progress);

            // If all items have been queried, proceed to display
            if (pbLoad.getProgress() == 100) {
                clLoad.setVisibility(View.GONE);
                productsListAdapter.setData(data);
                setEnabledButtons(true);
                rvProductsList.setVisibility(View.VISIBLE);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initFirebase();
        bindComponents();
        initComponents();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        clLoad.setVisibility(View.VISIBLE);
        setEnabledButtons(false);
        rvProductsList.setVisibility(View.GONE);
        queryItems();
    }



    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void bindComponents() {
        // Loading screen
        this.clLoad = findViewById(R.id.cl_pl_loading);
        this.pbLoad = findViewById(R.id.pb_loading);
        this.tvLoad = findViewById(R.id.tv_l_description);

        // Empty screen notice
        this.clEmpty = findViewById(R.id.cl_pl_empty_notice);

        // Filter
        this.clFilter = findViewById(R.id.cl_pl_filter);
        this.spnFilter = findViewById(R.id.spn_filter);
        this.etFilter = findViewById(R.id.et_filter);
        this.btnCancelFilter = findViewById(R.id.btn_cancel_filter);
        this.btnApplyFilter = findViewById(R.id.btn_apply_filter);

        // Activity elements
        this.btnFilter = findViewById(R.id.ib_pl_filter);
        this.btnAdd = findViewById(R.id.ib_pl_add);
        this.rvProductsList = findViewById(R.id.rv_pl);
    }

    private void initComponents() {
        // Local data
        this.data = new ArrayList<>();
        this.curProgress = 0;
        filterToggle = false;
        filterMode = "name";
        filterQuery = "";

        // TODO: Hide add/filter buttons until loaded

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spnFilter.setAdapter(adapter);

        initBtnFilter();
        initBtnAdd();
    }

    private void initBtnFilter() {
        this.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filterToggle) {
                    clFilter.setVisibility(View.VISIBLE);
                    rvProductsList.setVisibility(View.GONE);
                    clEmpty.setVisibility(View.GONE);
                    etFilter.setText("");
                    spnFilter.setSelection(0);
                }
                else {
                    clFilter.setVisibility(View.GONE);
                    if (totalProgress != 0)
                        rvProductsList.setVisibility(View.VISIBLE);
                    else
                        clEmpty.setVisibility(View.VISIBLE);
                }
                filterToggle = !filterToggle;
            }
        });

        this.btnCancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterToggle = false;
                filterMode = "name";
                filterQuery = "";
                clFilter.setVisibility(View.GONE);
                setEnabledButtons(false);
                clLoad.setVisibility(View.VISIBLE);
                queryItems();
            }
        });

        this.btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterToggle = false;
                filterMode = spnFilter.getSelectedItem().toString().toLowerCase();
                filterQuery = etFilter.getText().toString().trim();
                clFilter.setVisibility(View.GONE);
                setEnabledButtons(false);
                clLoad.setVisibility(View.VISIBLE);
                queryItems();
            }
        });
    }

    private void initBtnAdd() {
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(ProductsListActivity.this, ProductAddActivity.class);
                startActivity(i);
            }
        });
    }

    private void initRecyclerView () {
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

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Products List", "Could not retrieve from database.");
            }
        });
        // TODO NOTE END

        this.productsListAdapter = new ProductsListAdapter(this.data);
        this.rvProductsList.setAdapter(this.productsListAdapter);
    }

    private void queryItems() {
        tvLoad.setText(R.string.connecting);

        Query query = db.getReference(Collections.products.name())
                .child(this.userId)
                .orderByChild(filterMode);
        if (!filterQuery.isEmpty())
            query = query.equalTo(filterQuery);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                totalProgress = snapshot.getChildrenCount();

                if (totalProgress == 0) {
                    clEmpty.setVisibility(View.VISIBLE);
                    setEnabledButtons(false);
                    clLoad.setVisibility(View.GONE);
                }
                else
                    fetchItems();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Products List", "Could not retrieve product count from database.");
            }
        });
    }

    private void fetchItems() {
        curProgress = 0;
        clEmpty.setVisibility(View.GONE);

        pbLoad.setProgress(10);
        tvLoad.setText(R.string.fetch_items);

        Query query = db.getReference(Collections.products.name())
                .child(this.userId)
                .orderByChild(filterMode);
        if (!filterQuery.isEmpty())
            query = query.equalTo(filterQuery);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();

                try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        ProductModel pm = postSnapshot.getValue(ProductModel.class);
                        String imagePath = pm.getImagePath();
                        String name = pm.getName();
                        String type = pm.getType();
                        String description = pm.getDescription();
                        HashMap<String, Integer> ingredients = pm.getIngredients();
                        Product p = new Product(imagePath, name, type, description, ingredients);

                        fetchImage(p);
                    }
                } catch (Exception e) {
                    Log.e ("Products List", e.toString());
                }
                productsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Products List", "Could not retrieve from database.");
            }
        });
    }

    private void fetchImage(Product p) {
        long MAXBYTES = 1024*1024;
        StorageReference imageReference = storageReference.child(p.getImagePath());

        imageReference.getBytes(MAXBYTES)
        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                p.setImg(img);

                data.add(p);
                curProgress++;

                int progress = 10 + (int)(90 * (float)curProgress / totalProgress);
                ProgressBarRunnable runnable = new ProgressBarRunnable(handler, progress, 0);
                scheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                String errorMessage = e.getMessage();
                Log.v("ERROR MESSAGE", "ERROR: " + p.getImagePath() + " " + errorMessage);
            }
        });
    }

    private void setEnabledButtons(boolean status) {
        btnFilter.setEnabled(status);
        btnAdd.setEnabled(status);

        if (status) {
            this.btnFilter.setBackgroundResource(R.color.secondary);
            this.btnAdd.setBackgroundResource(R.color.secondary);
        }
        else {
            this.btnFilter.setBackgroundResource(R.color.disabled_secondary);
            this.btnAdd.setBackgroundResource(R.color.disabled_secondary);
        }
    }
}