package com.mobdeve.s13.group12.tinappay.checklist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

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

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistViewHolder> {
    private HashMap<String, Object> data;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;

    public ChecklistAdapter (HashMap<String, Object> data) {
        this.data = data;
        initFirebase();
    }

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
        Object key = data.keySet().toArray()[position];
        ChecklistItem item = (ChecklistItem) data.get(key);

        holder.setCliItem(item.getName());
        holder.setCbCliClicked(item.isChecked());

        /* Checklist tick button */
        /*
        holder.setTickedCheckboxOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = !item.isChecked();

                holder.setChecked(checked);
                updateChecked(key.toString(), checked);
                Log.d("Position", String.valueOf(position));
                Log.d("Key", key.toString());
                Log.d("Item", item.getName());
            }
        });
         */
        holder.setCheckListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = !item.isChecked();

                holder.setChecked(checked);
                updateChecked(key.toString(), checked);
                Log.d("Position", String.valueOf(position));
                Log.d("Key", key.toString());
                Log.d("Item", item.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release
    }

    public void updateChecked (String id, boolean checked) {
        db.getReference(Collections.checklist.name())
                .child(this.userId)
                .child(id)
                .child("checked")
                .setValue(checked).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Checklist", "Marked " + id + " " + checked);
            }
        });
    }

    public void setData (HashMap<String, Object> data) {
        this.data = data;
    }
}
