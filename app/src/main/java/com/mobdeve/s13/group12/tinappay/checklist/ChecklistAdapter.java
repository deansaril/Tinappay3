package com.mobdeve.s13.group12.tinappay.checklist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistViewHolder> {
    private ArrayList<ChecklistItem> data;

    public ChecklistAdapter (ArrayList<ChecklistItem> data) {
        this.data = data;
    }

    @NonNull
    @NotNull
    @Override
    public ChecklistViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_checklist_item, parent, false);

        ChecklistViewHolder checklistViewHolder = new ChecklistViewHolder(itemView);

        /* Checklist tick button */
        checklistViewHolder.setCheckboxOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.get(checklistViewHolder.getBindingAdapterPosition()).setChecked(isChecked);
                checklistViewHolder.setCbCliClicked(isChecked);
            }
        });

        return checklistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChecklistViewHolder holder, int position) {
        holder.setCliItem(data.get(position).getName());
        holder.setCbCliClicked(data.get(position).isChecked());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
