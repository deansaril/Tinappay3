package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.SelectIngredient;

import java.util.ArrayList;

public class SelectIngredientsActivity extends AppCompatActivity {
    private RecyclerView rvIngredientList;

    private LinearLayoutManager llmManager;
    private ArrayList<SelectIngredient> data;
    private SelectIngredientsAdapter selectIngredientsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ingredients);

        initRecyclerView();
    }

    private void initRecyclerView() {
        this.rvIngredientList = findViewById(R.id.rv_si);

        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvIngredientList.setLayoutManager(this.llmManager);

        data = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            String name = "SelectIngredient " + i;
            String type = "Type " + (i % 2);
            float price = i * 10;
            data.add (new SelectIngredient(R.drawable.placeholder, name, type, price));
        }

        this.selectIngredientsAdapter = new SelectIngredientsAdapter(this.data);
        this.rvIngredientList.setAdapter(this.selectIngredientsAdapter);
    }
}