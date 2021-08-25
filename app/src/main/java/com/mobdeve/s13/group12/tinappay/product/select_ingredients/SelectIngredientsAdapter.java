package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.SelectIngredient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SelectIngredientsAdapter extends RecyclerView.Adapter<SelectIngredientsViewHolder> {
    private ArrayList<SelectIngredient> data;

    public SelectIngredientsAdapter (ArrayList<SelectIngredient> data) {
        this.data = data;
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

        holder.setSelectedOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                boolean selected = !data.get(position).isSelected();

                holder.setSelected(selected);
                data.get(position).setSelected(selected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
