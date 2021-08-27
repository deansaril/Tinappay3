package com.mobdeve.s13.group12.tinappay.checklist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Product;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChecklistActivity extends AppCompatActivity {
    private RecyclerView rvChecklist;
    private LinearLayoutManager llmManager;
    private ArrayList<ChecklistItem> data;
    public ArrayList<String> keys;
    private ChecklistAdapter checklistAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        initFirebase();
        initRecyclerView();
    }

    private void initRecyclerView () {
        this.rvChecklist = findViewById(R.id.rv_cl);

        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvChecklist.setLayoutManager(this.llmManager);

        fetchItems();

        this.checklistAdapter = new ChecklistAdapter(this.data, this.keys);
        this.rvChecklist.setAdapter(checklistAdapter);
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userId = this.mAuth.getCurrentUser().getUid();
        //this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private void fetchItems() {
        data = new ArrayList<>();
        keys = new ArrayList<>();

        db.getReference(Collections.checklist.name())
                .child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();
                keys.clear();

                try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        data.add(postSnapshot.getValue(ChecklistItem.class));
                        keys.add(postSnapshot.getKey());
                    }
                } catch (Exception e) {
                    Log.e ("Checklist", e.toString());
                }
                checklistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Checklist", "Could not retrieve from database.");
            }
        });
    }
}