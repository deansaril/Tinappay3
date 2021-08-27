package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group12.tinappay.DatabaseHelper;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectIngredientsActivity extends AppCompatActivity {
    private Button btnConfirm;
    private RecyclerView rvIngredientList;

    private LinearLayoutManager llmManager;
    private ArrayList<Ingredient> data;
    private ArrayList<String> selected;
    private SelectIngredientsAdapter selectIngredientsAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ingredients);

        initComponents();
        initFirebase();
        initRecyclerView();
    }

    private void initComponents() {
        this.btnConfirm = findViewById(R.id.btn_si_confirm);
        this.selected = new ArrayList<>();

        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();

                ArrayList<String> list = (selectIngredientsAdapter.getSelected());
                i.putExtra(Keys.SI_LIST, list);

                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
    }

    private void initRecyclerView() {
        this.rvIngredientList = findViewById(R.id.rv_si);

        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvIngredientList.setLayoutManager(this.llmManager);

        fetchItems();
        Intent i = getIntent();
        selected = i.getStringArrayListExtra(Keys.SI_LIST);

        this.selectIngredientsAdapter = new SelectIngredientsAdapter(this.data, selected);
        this.rvIngredientList.setAdapter(this.selectIngredientsAdapter);
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userId = this.mAuth.getCurrentUser().getUid();
        //this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private void fetchItems() {
        data = new ArrayList<>();

        db.getReference(Collections.ingredients.name())
                .child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();

                try {
                    for (DataSnapshot postSnapshot: snapshot.getChildren())
                        data.add(postSnapshot.getValue(Ingredient.class));
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