package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s13.group12.tinappay.ProgressBarRunnable;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.IngredientModel;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This activity handles the selection of product ingredients
 */
public class SelectIngredientsActivity extends AppCompatActivity {

    /* Class variables */
    // Activity Elements
    private ConstraintLayout clLoad;
    private ConstraintLayout clEmpty;
    private Button btnConfirm;
    private RecyclerView rvIngredientList;

    // Loading screen
    private ProgressBar pbLoad;
    private TextView tvLoad;
    private long curProgress, totalProgress;

    // Recycler view
    private LinearLayoutManager llmManager;
    private ArrayList<Ingredient> data;
    private SelectIngredientsAdapter selectIngredientsAdapter;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private StorageReference storageReference;
    private String userId;
    private HashMap<String, Integer> quantities;

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
                selectIngredientsAdapter.setData(data);
                rvIngredientList.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.VISIBLE);
            }
        }
    };



    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ingredients);

        initFirebase();
        bindComponents();
        initComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Reload activity
        clLoad.setVisibility(View.VISIBLE);
        rvIngredientList.setVisibility(View.GONE);
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
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release
    }

    /**
     * Retrieves activity elements from layout and binds them to the activity
     */
    private void bindComponents() {
        // Loading screen
        this.clLoad = findViewById(R.id.cl_si_loading);
        this.pbLoad = findViewById(R.id.pb_loading);
        this.tvLoad = findViewById(R.id.tv_l_description);

        // Empty screen notice
        this.clEmpty = findViewById(R.id.cl_si_empty_notice);

        this.btnConfirm = findViewById(R.id.btn_si_confirm);
        this.rvIngredientList = findViewById(R.id.rv_si);
    }

    /**
     * Initializes variables used in the activity
     * Initializes functionality of activity
     */
    private void initComponents() {
        Intent i = getIntent();

        // Local data
        this.data = new ArrayList<>();
        this.curProgress = 0;
        this.quantities = (HashMap<String, Integer>)i.getSerializableExtra(Keys.KEY_SELECT_INGREDIENTS.name());

        initBtnConfirm();
        initRecyclerView();
    }

    /**
     * Initializes button to finish activity
     */
    private void initBtnConfirm() {
        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();

                // Retrieve list of selected ingredients and their quantity
                quantities = selectIngredientsAdapter.getQuantities();
                i.putExtra(Keys.KEY_SELECT_INGREDIENTS.name(), quantities);

                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
    }

    /**
     * Initializes functionality recycler view (Layout and adapter)
     */
    private void initRecyclerView() {
        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvIngredientList.setLayoutManager(this.llmManager);

        this.selectIngredientsAdapter = new SelectIngredientsAdapter(this.data, this.quantities);
        this.rvIngredientList.setAdapter(this.selectIngredientsAdapter);
    }

    /**
     * Queries database for number of items to retrieve
     */
    private void queryItems() {
        tvLoad.setText(R.string.connecting);

        // Query count of items to retrieve
        db.getReference(Collections.ingredients.name())
                .child(this.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                totalProgress = snapshot.getChildrenCount();

                // If there are no items, show prompt
                if (totalProgress == 0) {
                    clEmpty.setVisibility(View.VISIBLE);
                    clLoad.setVisibility(View.GONE);
                    btnConfirm.setVisibility(View.GONE);
                }
                // If there are items, fetch them
                else
                    fetchItems();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("SI", "Failed to retrieve item count.");
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

        // Fetch sorted items
        db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .orderByChild("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();

                try {
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        // Retrieve item from database
                        IngredientModel im = postSnapshot.getValue(IngredientModel.class);

                        // Create item to be transferred across activities
                        String imagePath = im.getImagePath();
                        String name = im.getName();
                        String type = im.getType();
                        String location = im.getLocation();
                        float price = im.getPrice();
                        Ingredient i = new Ingredient(imagePath, name, type, location, price);
                        i.setId(postSnapshot.getKey());

                        // Add item to list of items to display and fetch its matching image
                        data.add(i);
                        fetchImage(i, data.size() - 1);
                    }
                } catch (Exception e) {
                    Log.e ("SI", e.toString());
                }
                selectIngredientsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("SI", "Failed to retrieve items.");
            }
        });
    }

    /**
     * Fetches image of specified product from cloud storage
     * @param i Ingredient - item requiring image
     * @param pos int - index of item in the list
     */
    private void fetchImage(Ingredient i, int pos) {
        long MAXBYTES = 1024*1024;
        StorageReference imageReference = storageReference.child(i.getImagePath());

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
                    Log.e("SI", e.toString());
                }
            });
    }
}