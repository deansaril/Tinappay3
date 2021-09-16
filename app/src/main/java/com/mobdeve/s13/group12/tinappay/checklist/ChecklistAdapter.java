package com.mobdeve.s13.group12.tinappay.checklist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;
import com.mobdeve.s13.group12.tinappay.objects.Collections;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * This adapter connects checklist item data to the UI
 */
public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistViewHolder> {

    /* Class variables */
    // Item data
    private HashMap<String, Object> data;

    // Firebase connection
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

    /**
     * Instantiates a ChecklistAdapter
     * @param data HashMap - item data to be displayed
     */
    public ChecklistAdapter (HashMap<String, Object> data) {
        this.data = data;
        initFirebase();
    }



    /* Function overrides */
    @NonNull
    @NotNull
    @Override
    public ChecklistViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_checklist_item, parent, false);

        ChecklistViewHolder checklistViewHolder = new ChecklistViewHolder(itemView);

        return checklistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChecklistViewHolder holder, int position) {
        // Get current item
        Object key = data.keySet().toArray()[position];
        ChecklistItem item = (ChecklistItem) data.get(key);

        // Assigns values to UI elements
        holder.setName(item.getName());
        holder.setChecked(item.isChecked());

        // Sets check/uncheck interaction
        holder.setCheckListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = !item.isChecked();

                holder.setChecked(checked);
                updateChecked(key.toString(), checked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
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
     * Updates item in database
     * @param id String - checklist item to be updated
     * @param checked boolean - updated status of the checklist item
     */
    public void updateChecked (String id, boolean checked) {
        db.getReference(Collections.checklist.name())
                .child(this.userId)
                .child(id)
                .child(Collections.checked.name())
                .setValue(checked).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Checklist", "Marked " + id + " " + checked);
            }
        });
    }

    /**
     * Sets assigned item data
     * @param data HashMap - item data
     */
    public void setData (HashMap<String, Object> data) {
        this.data = data;
    }
}
