package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify.IngredientAddActivity;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.IngredientModel;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *   Handles all the functionalities involved in the ingredients list screen
 */
public class IngredientsListActivity extends AppCompatActivity {

    /* Class variables */
    // Activity elements
    private ConstraintLayout clLoad;
    private ConstraintLayout clEmpty;
    private ConstraintLayout clFilter;
    private ImageButton btnAdd;
    private ImageButton btnFilter;
    private RecyclerView rvIngredientsList;

    // Loading screen
    private ProgressBar pbLoad;
    private TextView tvLoad;
    private long curProgress, totalProgress;

    // Filter
    private final String[] options = {"Name", "Type", "Location"};
    private boolean filterToggle;
    private Spinner spnFilter;
    private EditText etFilter;
    private Button btnCancelFilter;
    private Button btnApplyFilter;

    // RecyclerView
    private LinearLayoutManager llmManager;
    private ArrayList<Ingredient> data;
    private IngredientsListAdapter ingredientsListAdapter;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private String filterMode;
    private String filterQuery;

    //Firebase Cloud Storage Variables
    private FirebaseStorage fbStorage;
    private StorageReference storageReference;

    // Final variables
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    private final Handler handler = new Handler(Looper.getMainLooper()) {
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
                ingredientsListAdapter.setData(data);
                setEnabledButtons(true);
                rvIngredientsList.setVisibility(View.VISIBLE);
            }
        }
    };



    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_list);

        initFirebase();
        bindComponents();
        initComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();

        clLoad.setVisibility(View.VISIBLE);
        setEnabledButtons(false);
        rvIngredientsList.setVisibility(View.GONE);
        queryItems();
    }


    /* Class functions */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userId = this.mAuth.getCurrentUser().getUid();

        //Firebase Cloud Storage methods
        this.fbStorage = FirebaseStorage.getInstance();
        this.storageReference = fbStorage.getReference();
    }

    /**
     *   This function binds the objects in the layout to the activity's variables for editing
     */
    private void bindComponents() {
        // Loading screen
        clLoad = findViewById(R.id.cl_il_loading);
        pbLoad = findViewById(R.id.pb_loading);
        tvLoad = findViewById(R.id.tv_l_description);

        // Empty screen notice
        clEmpty = findViewById(R.id.cl_il_empty_notice);

        //Filter
        this.clFilter = findViewById(R.id.cl_il_filter);
        this.spnFilter = findViewById(R.id.spn_filter);
        this.etFilter = findViewById(R.id.et_filter);
        this.btnCancelFilter = findViewById(R.id.btn_cancel_filter);
        this.btnApplyFilter = findViewById(R.id.btn_apply_filter);

        // Activity elements
        this.btnFilter = findViewById(R.id.ib_il_filter);
        this.btnAdd = findViewById(R.id.ib_il_add);
        this.rvIngredientsList = findViewById(R.id.rv_il);
    }

    /**
     *  This function adds the needed functionalities of the layout objects
     */
    private void initComponents() {
        // Local data
        data = new ArrayList<>();
        curProgress = 0;

        filterToggle = false;
        filterMode = "name";
        filterQuery = "";

        // Activity elements
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spnFilter.setAdapter(adapter);

        initBtnAdd();
        initRecyclerView();
        initBtnFilter();
    }

    /**
     * initializes the click listener for filter button, which redirects the user to a filter layout allowing them to search for ingredients
     */
    private void initBtnFilter() {

        this.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //opens the filter layout
                if (!filterToggle) {
                    clFilter.setVisibility(View.VISIBLE);
                    rvIngredientsList.setVisibility(View.GONE);
                    clEmpty.setVisibility(View.GONE);
                    etFilter.setText("");
                    spnFilter.setSelection(0);
                }
                //closes the filter layout
                else {
                    clFilter.setVisibility(View.GONE);
                    if (totalProgress != 0)
                        rvIngredientsList.setVisibility(View.VISIBLE);
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

    /**
     * Sets the status and layout of the filter and add button
     * @param status is the status to set for the filter and add button
     */
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

    /**
     * initializes functionality of add ingredient button, which redirects user to add ingredient screen
     */
    private void initBtnAdd() {
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(IngredientsListActivity.this, IngredientAddActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * initializes the functionalities of the RecyclerView, such as the loading of the ingredients
     */
    private void initRecyclerView () {
        this.llmManager = new LinearLayoutManager(this);
        this.rvIngredientsList.setLayoutManager(this.llmManager);

        //Sets adapter to the recycler view
        this.ingredientsListAdapter = new IngredientsListAdapter(this.data);
        this.rvIngredientsList.setAdapter(this.ingredientsListAdapter);
    }

    /**
     *  Queries database for number of items to retrieve
     */
    private void queryItems() {
        tvLoad.setText(R.string.connecting);

        //sorts the ingredients in alphabetical order
        Query query = db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .orderByChild(filterMode);
        // targets matching items if filter is entered
        if (!filterQuery.isEmpty())
            query = query.equalTo(filterQuery);

        // Query count of items to retrieve
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                totalProgress = snapshot.getChildrenCount();

                //If there are no items, show no ingredients prompt
                if (totalProgress == 0) {
                    clEmpty.setVisibility(View.VISIBLE);
                    setEnabledButtons(true);
                    clLoad.setVisibility(View.GONE);
                }
                else
                    fetchItems();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("ILA Query failed", "Could not retrieve product count from database.");
            }
        });
    }

    /**
     * Fetch items from database
     */
    private void fetchItems() {
        curProgress = 0;
        clEmpty.setVisibility(View.GONE);

        pbLoad.setProgress(10);
        tvLoad.setText(R.string.fetch_items);

        Query query = db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .orderByChild(filterMode);
        if (!filterQuery.isEmpty())
            query = query.equalTo(filterQuery);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();
                try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        IngredientModel im = postSnapshot.getValue(IngredientModel.class);
                        String imagePath = im.getImagePath();
                        String name = im.getName();
                        String type = im.getType();
                        String location = im.getLocation();
                        float price = im.getPrice();
                        Ingredient i = new Ingredient(imagePath, name, type, location, price);
                        i.setId(postSnapshot.getKey());

                        data.add(i);
                        fetchImage(i, data.size() -1);
                    }
                } catch (Exception e) {
                    Log.e ("ILA fetchItems Error", e.toString());
                }
                ingredientsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("ILA onCancelled", "Failed to retrieve items.");
            }
        });
    }

    /**
     * Fetches the image of the current ingredient to be loaded
     * @param i is the current Ingredient being loaded in the list
     * @param pos is the int position of the said Ingredient
     */
    private void fetchImage(Ingredient i, int pos) {

        long MAXBYTES = 1024*1024;
        StorageReference imageReference = storageReference.child(i.getImagePath());

        //downloads the bytes of the image from the Cloud Storage
        imageReference.getBytes(MAXBYTES)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        // creates a bitmap of the bytes, which is used to set the bitmap of the ImageView to load the image
                        Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        data.get(pos).setImg(img);
                        curProgress++;

                        Log.d("Progress", "" + curProgress + "/" + totalProgress);

                        //increases the progress of ProgressBar for every image and ingredient loaded
                        int progress = 10 + (int)(90 * (float)curProgress / totalProgress);
                        ProgressBarRunnable runnable = new ProgressBarRunnable(handler, progress, 0);
                        scheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        String errorMessage = e.getMessage();
                        Log.e("ILA ERROR MESSAGE", "ERROR: " + i.getImagePath() + " " + errorMessage);
                    }
                });
    }

}