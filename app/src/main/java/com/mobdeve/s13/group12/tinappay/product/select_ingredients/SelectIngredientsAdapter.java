package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SelectIngredientsAdapter extends RecyclerView.Adapter<SelectIngredientsViewHolder> {
    private ArrayList<Ingredient> data;
    private ArrayList<String> selected;

    public SelectIngredientsAdapter (ArrayList<Ingredient> data, ArrayList<String> selected) {
        this.data = data;
        this.selected = new ArrayList<>();
        if (selected != null)
            this.selected = selected;
    }

    @NonNull
    @NotNull
    @Override
    public SelectIngredientsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_ingredient_card, parent, false);

        SelectIngredientsViewHolder selectIngredientsViewHolder = new SelectIngredientsViewHolder(itemView);

        return selectIngredientsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectIngredientsViewHolder holder, int position) {
        holder.setImg(data.get(position).getImageId());
        holder.setName(data.get(position).getName());
        holder.setType(data.get(position).getType());
        holder.setPrice(data.get(position).getPrice());
        holder.setLocation(data.get(position).getLocation());
        if (selected.contains(data.get(position).getId()))
            holder.setSelected(true);
        else
            holder.setSelected(false);

        holder.setSelectedOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String current = data.get(position).getId();
                if (selected.contains(current)) {
                    int index = selected.indexOf(current);
                    selected.remove(index);
                    holder.setSelected(false);
                }
                else {
                    selected.add(current);
                    holder.setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public ArrayList<String> getSelected() {
        return this.selected;
    }
}
