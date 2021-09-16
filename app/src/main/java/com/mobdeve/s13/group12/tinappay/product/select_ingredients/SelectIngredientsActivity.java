package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group12.tinappay.ProgressBarRunnable;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelectIngredientsActivity extends AppCompatActivity {
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
    private String userId;
    private HashMap<String, Integer> quantities;

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
                selectIngredientsAdapter.setData(data);
                rvIngredientList.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.VISIBLE);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ingredients);

        bindComponents();
        initComponents();
        initFirebase();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        clLoad.setVisibility(View.VISIBLE);
        rvIngredientList.setVisibility(View.GONE);
        queryItems();
    }

    private void bindComponents() {
        // Loading screen
        clLoad = findViewById(R.id.cl_si_loading);
        pbLoad = findViewById(R.id.pb_loading);
        tvLoad = findViewById(R.id.tv_l_description);

        // Empty screen notice
        clEmpty = findViewById(R.id.cl_si_empty_notice);

        this.btnConfirm = findViewById(R.id.btn_si_confirm);
    }

    private void initComponents() {
        // Local data
        data = new ArrayList<>();
        curProgress = 0;

        Intent i = getIntent();
        this.quantities = (HashMap<String, Integer>)i.getSerializableExtra(Keys.KEY_SELECT_INGREDIENTS.name());
        //this.ingredients = (HashMap)i.getSerializableExtra(KeysOld.SI_LIST);

        initBtnConfirm();
    }

    private void initBtnConfirm() {
        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();

                quantities = selectIngredientsAdapter.getQuantities();
                i.putExtra(Keys.KEY_SELECT_INGREDIENTS.name(), quantities);

                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
    }

    private void initRecyclerView() {
        this.rvIngredientList = findViewById(R.id.rv_si);

        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvIngredientList.setLayoutManager(this.llmManager);

        this.selectIngredientsAdapter = new SelectIngredientsAdapter(this.data, this.quantities);
        this.rvIngredientList.setAdapter(this.selectIngredientsAdapter);
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release
    }

    private void queryItems() {
        tvLoad.setText(R.string.connecting);
        try {
            Thread.sleep(250);
        } catch (Exception e) {
            Log.e("Select Ingredients", e.toString());
        }

        db.getReference(Collections.ingredients.name())
                .child(this.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                totalProgress = snapshot.getChildrenCount();

                if (totalProgress == 0) {
                    clEmpty.setVisibility(View.VISIBLE);
                    clLoad.setVisibility(View.GONE);
                    btnConfirm.setVisibility(View.GONE);
                }
                else
                    fetchItems();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Ingredients List", "Could not retrieve product count from database.");
            }
        });
    }

    private void fetchItems() {
        data = new ArrayList<>();
        clEmpty.setVisibility(View.GONE);

        pbLoad.setProgress(10);
        tvLoad.setText(R.string.fetch_items);
        try {
            Thread.sleep(250);
        } catch (Exception e) {
            Log.e("Select Ingredients", e.toString());
        }

        db.getReference(Collections.ingredients.name())
                .child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();

                try {
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        curProgress++;
                        data.add(postSnapshot.getValue(Ingredient.class));

                        int progress = 10 + (int)(90 * (float)curProgress / totalProgress);
                        ProgressBarRunnable runnable = new ProgressBarRunnable(handler, progress);
                        scheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    Log.e ("Checklist", e.toString());
                }
                selectIngredientsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Products List", "Could not retrieve from database.");
            }
        });
    }
}