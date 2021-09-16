package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group12.tinappay.DatabaseHelper;
import com.mobdeve.s13.group12.tinappay.ProgressBarRunnable;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify.IngredientAddActivity;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IngredientsListActivity extends AppCompatActivity {

    /* Class variables */
    // Activity elements
    private ConstraintLayout clLoad;
    private ConstraintLayout clEmpty;
    private ImageButton btnAdd;
    private RecyclerView rvIngredientsList;

    // Loading screen
    private ProgressBar pbLoad;
    private TextView tvLoad;
    private long curProgress, totalProgress;

    // RecyclerView
    private LinearLayoutManager llmManager;
    private ArrayList<Ingredient> data;
    private IngredientsListAdapter ingredientsListAdapter;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

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
        rvIngredientsList.setVisibility(View.GONE);
        queryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ingredientsListAdapter.notifyDataSetChanged();
    }



    /* Class functions */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userId = this.mAuth.getCurrentUser().getUid();
        //this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    /*
        This function binds the objects in the layout to the activity's variables for editing
     */
    private void bindComponents() {
        // Loading screen
        clLoad = findViewById(R.id.cl_il_loading);
        pbLoad = findViewById(R.id.pb_loading);
        tvLoad = findViewById(R.id.tv_l_description);

        // Empty screen notice
        clEmpty = findViewById(R.id.cl_il_empty_notice);

        // Activity elements
        this.btnAdd = findViewById(R.id.ib_il_add);
        this.rvIngredientsList = findViewById(R.id.rv_il);
    }

    private void initComponents() {
        // Local data
        data = new ArrayList<>();
        curProgress = 0;

        // Activity elements
        initBtnAdd();
        initRecyclerView();
    }

    private void initBtnAdd() {
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(IngredientsListActivity.this, IngredientAddActivity.class);
                startActivity(i);
            }
        });
    }

    private void initRecyclerView () {
        this.llmManager = new LinearLayoutManager(this);
        this.rvIngredientsList.setLayoutManager(this.llmManager);

        /*
        // Populates ingredients; TODO NOTE START: Remove in final release
        db.getReference(Collections.ingredients.name())
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) {
                            Log.i("UserIngredient", "No products. Pre-populating database...");
                            DatabaseHelper.loadIngredients(userId);
                        }
                        else
                            Log.i("UserIngredient", "User products found.");
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.e("Products List", "Could not retrieve from database.");
                    }
                });
        // NOTE END
         */

        this.ingredientsListAdapter = new IngredientsListAdapter(this.data);
        this.rvIngredientsList.setAdapter(this.ingredientsListAdapter);
    }

    private void queryItems() {
        tvLoad.setText(R.string.connecting);

        db.getReference(Collections.ingredients.name())
                .child(this.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                totalProgress = snapshot.getChildrenCount();

                // If there are no items
                if (totalProgress == 0) {
                    clEmpty.setVisibility(View.VISIBLE);
                    clLoad.setVisibility(View.GONE);
                }
                // If there are items
                else
                    fetchItems();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("IL", "Failed to retrieve count.");
            }
        });
    }


    private void fetchItems() {
        clEmpty.setVisibility(View.GONE);

        pbLoad.setProgress(10);
        tvLoad.setText(R.string.fetch_items);

        db.getReference(Collections.ingredients.name())
                .child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();

                try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        curProgress++;
                        Ingredient ingredient = postSnapshot.getValue(Ingredient.class);
                        data.add(ingredient);

                        int progress = 10 + (int)(90 * (float)curProgress / totalProgress);
                        ProgressBarRunnable runnable = new ProgressBarRunnable(handler, progress);
                        scheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    Log.e ("IL", e.toString());
                }
                ingredientsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("IL", "Failed to retrieve items.");
            }
        });
    }
}