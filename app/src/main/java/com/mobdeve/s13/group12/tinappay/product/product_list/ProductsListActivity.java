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

/**
 * This activity handles the displaying of all products
 */
public class ProductsListActivity extends AppCompatActivity {

    /* Class variables */
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
    private Button btnFilterClear;
    private Button btnFilterApply;

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

    // Final variables
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message message) {
            super.handleMessage(message);
            
            Bundle bundle = message.getData();

            // Set current progress
            int progress = bundle.getInt(Keys.KEY_LOAD.name());
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



    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initFirebase();
        bindComponents();
        initComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Reload activity
        clLoad.setVisibility(View.VISIBLE);
        setEnabledButtons(false);
        rvProductsList.setVisibility(View.GONE);
        queryItems();
    }



    /* Class functions */
    /**
     * Initializes connection to firebase database and cloud storage
     */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.storageReference = FirebaseStorage.getInstance().getReference();
        this.userId = this.mAuth.getCurrentUser().getUid();
    }

    /**
     * Retrieves activity elements from layout and binds them to the activity
     */
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
        this.btnFilterClear = findViewById(R.id.btn_cancel_filter);
        this.btnFilterApply = findViewById(R.id.btn_apply_filter);

        // Activity elements
        this.btnFilter = findViewById(R.id.ib_pl_filter);
        this.btnAdd = findViewById(R.id.ib_pl_add);
        this.rvProductsList = findViewById(R.id.rv_pl);
    }

    /**
     * Initializes variables used in the activity
     * Initializes functionality of activity
     */
    private void initComponents() {
        // Local data
        this.data = new ArrayList<>();
        this.curProgress = 0;
        this.filterToggle = false;
        this.filterMode = "name";
        this.filterQuery = "";

        // Adapter for filter dropdown menu
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spnFilter.setAdapter(adapter);

        initBtnFilter();
        initBtnAdd();
        initRecyclerView();
    }

    /**
     * Initializes all buttons related to filtering
     */
    private void initBtnFilter() {
        // Sets listener for filter fields visibility
        this.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If filter menu is not displayed, set it visible
                if (!filterToggle) {
                    // Sets element visibility
                    clFilter.setVisibility(View.VISIBLE);
                    rvProductsList.setVisibility(View.GONE);
                    clEmpty.setVisibility(View.GONE);

                    // Pre-sets field values
                    etFilter.setText("");
                    spnFilter.setSelection(0);
                }
                // If filter menu is displayed, set it hidden
                else {
                    clFilter.setVisibility(View.GONE);
                    // If there are items, display them
                    if (totalProgress != 0)
                        rvProductsList.setVisibility(View.VISIBLE);
                    // If there are no items, display prompt
                    else
                        clEmpty.setVisibility(View.VISIBLE);
                }
                filterToggle = !filterToggle;
            }
        });

        // Sets listener for clearing filter
        this.btnFilterClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clears filter
                filterToggle = false;
                filterMode = "name";
                filterQuery = "";

                // Starts product query
                clFilter.setVisibility(View.GONE);
                setEnabledButtons(false);
                clLoad.setVisibility(View.VISIBLE);
                queryItems();
            }
        });

        // Sets listener for applying filter
        this.btnFilterApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Readies filter
                filterToggle = false;
                filterMode = spnFilter.getSelectedItem().toString().toLowerCase();
                filterQuery = etFilter.getText().toString().trim();

                // Starts product query
                clFilter.setVisibility(View.GONE);
                setEnabledButtons(false);
                clLoad.setVisibility(View.VISIBLE);
                queryItems();
            }
        });
    }

    /**
     * Initializes button for moving to product add actiivty
     */
    private void initBtnAdd() {
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(ProductsListActivity.this, ProductAddActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Initializes functionality recycler view (Layout and adapter)
     */
    private void initRecyclerView () {
        this.glmManager = new GridLayoutManager(this, 2);
        this.rvProductsList.setLayoutManager(this.glmManager);

        this.productsListAdapter = new ProductsListAdapter(this.data);
        this.rvProductsList.setAdapter(this.productsListAdapter);
    }

    /**
     * Queries database for number of items to retrieve
     */
    private void queryItems() {
        tvLoad.setText(R.string.connecting);

        // Sorts entries for filtering
        Query query = db.getReference(Collections.products.name())
                .child(this.userId)
                .orderByChild(filterMode);
        // If a filter is entered, target matching items
        if (!filterQuery.isEmpty())
            query = query.equalTo(filterQuery);

        // Query count of items to retrieve
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                totalProgress = snapshot.getChildrenCount();

                // If there are no items, show prompt
                if (totalProgress == 0) {
                    clEmpty.setVisibility(View.VISIBLE);
                    setEnabledButtons(true);
                    clLoad.setVisibility(View.GONE);
                }
                // If there are items, fetch them
                else
                    fetchItems();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("PL", "Failed to retrieve item count.");
            }
        });
    }

    /**
     * Fetches items from database
     */
    private void fetchItems() {
        curProgress = 0;
        clEmpty.setVisibility(View.GONE);

        pbLoad.setProgress(10);
        tvLoad.setText(R.string.fetch_items);

        // Sorts entries for filtering
        Query query = db.getReference(Collections.products.name())
                .child(this.userId)
                .orderByChild(filterMode);
        // If a filter is entered, target matching items
        if (!filterQuery.isEmpty())
            query = query.equalTo(filterQuery);

        // Fetch items
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();

                try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        // Retrieve item from database
                        ProductModel pm = postSnapshot.getValue(ProductModel.class);

                        // Create item to be transferred across activities
                        String imagePath = pm.getImagePath();
                        String name = pm.getName();
                        String type = pm.getType();
                        String description = pm.getDescription();
                        HashMap<String, Integer> ingredients = pm.getIngredients();
                        Product p = new Product(imagePath, name, type, description, ingredients);
                        p.setId(postSnapshot.getKey());

                        // Add item to list of items to display and fetch its matching image
                        data.add(p);
                        fetchImage(p, data.size() - 1);
                    }
                } catch (Exception e) {
                    Log.e ("PL", e.toString());
                }
                productsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("PL", "Failed to retrieve items.");
            }
        });
    }

    /**
     * Fetches image of specified product from cloud storage
     * @param p Product - item requiring image
     * @param pos int - index of item in the list
     */
    private void fetchImage(Product p, int pos) {
        long MAXBYTES = 1024*1024;
        StorageReference imageReference = storageReference.child(p.getImagePath());

        // Fetches image from cloud storage
        imageReference.getBytes(MAXBYTES)
            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Create bitmap of image
                    Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    // Assign image to item
                    data.get(pos).setImg(img);

                    // Sends progressbar value
                    curProgress++;
                    int progress = 10 + (int)(90 * (float)curProgress / totalProgress);
                    ProgressBarRunnable runnable = new ProgressBarRunnable(handler, progress, 0);
                    scheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Log.e("PLA", e.toString());
                }
            });
    }

    /**
     * Sets enabled/disabled appearance and interactivity of buttons
     * @param status boolean - "enabled" status of buttons
     */
    private void setEnabledButtons(boolean status) {
        // Set buttons to be clickable or not
        btnFilter.setEnabled(status);
        btnAdd.setEnabled(status);

        // Set buttons to appear enabled
        if (status) {
            this.btnFilter.setBackgroundResource(R.color.secondary);
            this.btnAdd.setBackgroundResource(R.color.secondary);
        }
        // Set buttons to appear enabled
        else {
            this.btnFilter.setBackgroundResource(R.color.disabled_secondary);
            this.btnAdd.setBackgroundResource(R.color.disabled_secondary);
        }
    }
}