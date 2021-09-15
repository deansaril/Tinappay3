package com.mobdeve.s13.group12.tinappay.checklist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group12.tinappay.ProgressBarRunnable;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChecklistActivity extends AppCompatActivity {

    // Activity elements
    private ConstraintLayout clLoad;
    private ConstraintLayout clEmpty;
    private RecyclerView rvChecklist;

    // Loading screen
    private ProgressBar pbLoad;
    private TextView tvLoad;
    private long curProgress, totalProgress;

    // RecyclerView
    private LinearLayoutManager llmManager;
    private ArrayList<ChecklistItem> data;
    public ArrayList<String> keys;
    private ChecklistAdapter checklistAdapter;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

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
                checklistAdapter.setData(data);
                rvChecklist.setVisibility(View.VISIBLE);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        initFirebase();
        initComponents();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        clLoad.setVisibility(View.VISIBLE);
        rvChecklist.setVisibility(View.GONE);
        queryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checklistAdapter.notifyDataSetChanged();
    }



    private void initComponents() {
        Log.i("Products List", "Initializing activity");

        // Loading screen
        clLoad = findViewById(R.id.cl_cl_loading);
        pbLoad = findViewById(R.id.pb_loading);
        tvLoad = findViewById(R.id.tv_l_description);

        // Empty screen notice
        clEmpty = findViewById(R.id.cl_cl_empty_notice);

        // Local data
        data = new ArrayList<>();
        curProgress = 0;
    }

    private void initRecyclerView () {
        this.rvChecklist = findViewById(R.id.rv_cl);

        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvChecklist.setLayoutManager(this.llmManager);

        this.checklistAdapter = new ChecklistAdapter(this.data, this.keys);
        this.rvChecklist.setAdapter(checklistAdapter);
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release
    }

    private void queryItems() {
        tvLoad.setText(R.string.connecting);
        try {
            Thread.sleep(250);
        } catch (Exception e) {
            Log.e("Checklist", e.toString());
        }

        db.getReference(Collections.checklist.name())
                .child(this.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                totalProgress = snapshot.getChildrenCount();

                if (totalProgress == 0) {
                    clEmpty.setVisibility(View.VISIBLE);
                    clLoad.setVisibility(View.GONE);
                }
                else
                    fetchItems();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Checklist", "Could not retrieve checklist count from database.");
            }
        });
    }

    private void fetchItems() {
        data = new ArrayList<>();
        keys = new ArrayList<>();
        clEmpty.setVisibility(View.GONE);

        pbLoad.setProgress(10);
        tvLoad.setText(R.string.fetch_items);
        try {
            Thread.sleep(250);
        } catch (Exception e) {
            Log.e("Checklist", e.toString());
        }

        db.getReference(Collections.checklist.name())
                .child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();
                keys.clear();

                try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        curProgress++;
                        data.add(postSnapshot.getValue(ChecklistItem.class));
                        keys.add(postSnapshot.getKey());

                        int progress = 10 + (int)(90 * (float)curProgress / totalProgress);
                        ProgressBarRunnable runnable = new ProgressBarRunnable(handler, progress);
                        scheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
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