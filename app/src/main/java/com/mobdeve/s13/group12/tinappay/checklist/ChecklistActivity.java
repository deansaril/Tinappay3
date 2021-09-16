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

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This activity handles user interaction with the item checklist
 */
public class ChecklistActivity extends AppCompatActivity {

    /* Class variables */
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
    private HashMap<String, Object> data;
    private ChecklistAdapter checklistAdapter;

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
            pbLoad.setProgress(progress);

            // If all items have been queried, proceed to display
            if (pbLoad.getProgress() == 100) {
                clLoad.setVisibility(View.GONE);
                checklistAdapter.setData(data);
                rvChecklist.setVisibility(View.VISIBLE);
            }
        }
    };



    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        initFirebase();
        bindComponents();
        initComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();

        clLoad.setVisibility(View.VISIBLE);
        rvChecklist.setVisibility(View.GONE);
        queryItems();
    }



    /* Class functions */
    /**
     * Initializes connection to firebase database
     */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release
    }

    /**
     * Retrieves activity elements from layout and binds them to the activity
     */
    private void bindComponents() {
        // Loading screen
        clLoad = findViewById(R.id.cl_cl_loading);
        pbLoad = findViewById(R.id.pb_loading);
        tvLoad = findViewById(R.id.tv_l_description);

        // Empty screen notice
        clEmpty = findViewById(R.id.cl_cl_empty_notice);

        // Activity elements
        this.rvChecklist = findViewById(R.id.rv_cl);
    }

    /**
     * Initializes variables used in the activity
     * Initializes functionality of activity
     */
    private void initComponents() {
        // Local data
        data = new HashMap<>();
        curProgress = 0;

        initRecyclerView();
    }

    /**
     * Initializes functionality recycler view (Layout and adapter)
     */
    private void initRecyclerView () {
        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvChecklist.setLayoutManager(this.llmManager);

        this.checklistAdapter = new ChecklistAdapter(this.data);
        this.rvChecklist.setAdapter(checklistAdapter);
    }

    /**
     * Queries database for number of items to retrieve
     */
    private void queryItems() {
        tvLoad.setText(R.string.connecting);

        // Query count of items to retrieve
        db.getReference(Collections.checklist.name())
                .child(this.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                totalProgress = snapshot.getChildrenCount();

                // If there are no items, show prompt
                if (totalProgress == 0) {
                    clEmpty.setVisibility(View.VISIBLE);
                    clLoad.setVisibility(View.GONE);
                }
                // If there are items, fetch them
                else
                    fetchItems();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("CL", "Failed to retrieve item count.");
            }
        });
    }

    /**
     * Fetches items from database
     */
    private void fetchItems() {
        clEmpty.setVisibility(View.GONE);

        pbLoad.setProgress(10);
        tvLoad.setText(R.string.fetch_items);

        // Fetch items
        db.getReference(Collections.checklist.name())
                .child(this.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();

                try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        ChecklistItem item = postSnapshot.getValue(ChecklistItem.class);
                        data.put(key, item);

                        // Sends progressbar value
                        curProgress++;
                        int progress = 10 + (int)(90 * (float)curProgress / totalProgress);
                        ProgressBarRunnable runnable = new ProgressBarRunnable(handler, progress);
                        scheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    Log.e ("CL", e.toString());
                }
                checklistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("CL", "Failed to retrieve items.");
            }
        });
    }
}