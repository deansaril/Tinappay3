package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.mobdeve.s13.group12.tinappay.DatabaseHelper;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify.IngredientAddActivity;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class IngredientsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_list);

        initFirebase();
        initBtnAdd();
        initRecyclerView();
    }

    private ImageButton btnAdd;
    private RecyclerView rvIngredientsList;
    private LinearLayoutManager llmManager;
    private ArrayList<Ingredient> data;
    private IngredientsListAdapter ingredientsListAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userId = this.mAuth.getCurrentUser().getUid();
        //this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private void initBtnAdd() {
        this.btnAdd = findViewById(R.id.ib_il_add);
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(IngredientsListActivity.this, IngredientAddActivity.class);
                startActivity(i);
            }
        });
    }

    private void initRecyclerView () {
        this.rvIngredientsList = findViewById(R.id.rv_il);

        this.llmManager = new LinearLayoutManager(this);
        this.rvIngredientsList.setLayoutManager(this.llmManager);

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
                    // TODO NOTE END

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.e("Products List", "Could not retrieve from database.");
                    }
                });

        fetchItems();

        // TODO: If data.size == 0, display notice, else create adapter

        this.ingredientsListAdapter = new IngredientsListAdapter(this.data);
        this.rvIngredientsList.setAdapter(this.ingredientsListAdapter);
    }


    private void fetchItems() {
        data = new ArrayList<>();

        db.getReference(Collections.ingredients.name())
                .child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();

                try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Ingredient ingredient = postSnapshot.getValue(Ingredient.class);
                        data.add(ingredient);
                    }
                } catch (Exception e) {
                    Log.e ("FetchItemsError", e.toString());
                }
                ingredientsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Products List", "Could not retrieve from database.");
            }
        });
    }

}