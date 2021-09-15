package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.ingredient.IngredientActivity;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListViewHolder>{
    private ArrayList<Ingredient> data;

    public IngredientsListAdapter (ArrayList<Ingredient> data) {
        this.data = data;
    }

    @NonNull
    @NotNull
    @Override
    public IngredientsListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_linear_item, parent, false);

        IngredientsListViewHolder ingredientsListViewHolder = new IngredientsListViewHolder(itemView);

        ingredientsListViewHolder.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent (v.getContext(), IngredientActivity.class);

                i.putExtra(Keys.KEY_INGREDIENT_ID, data.get(ingredientsListViewHolder.getBindingAdapterPosition()).getId());
                i.putExtra(Keys.KEY_INGREDIENT_IMG, data.get(ingredientsListViewHolder.getBindingAdapterPosition()).getImageId());
                i.putExtra(Keys.KEY_INGREDIENT_NAME, data.get(ingredientsListViewHolder.getBindingAdapterPosition()).getName());
                i.putExtra(Keys.KEY_INGREDIENT_TYPE, data.get(ingredientsListViewHolder.getBindingAdapterPosition()).getType());
                i.putExtra(Keys.KEY_INGREDIENT_PRICE, data.get(ingredientsListViewHolder.getBindingAdapterPosition()).getPrice());
                i.putExtra(Keys.KEY_INGREDIENT_LOCATION, data.get(ingredientsListViewHolder.getBindingAdapterPosition()).getLocation());

                v.getContext().startActivity(i);
            }
        });

        return ingredientsListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull IngredientsListViewHolder holder, int position) {
        holder.setItemImage(data.get(position).getImageId());
        holder.setItemField1(data.get(position).getName());
        holder.setItemField2(data.get(position).getType());
        holder.setItemField3(Float.toString(data.get(position).getPrice()));
        holder.setItemField4(data.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void setData (ArrayList<Ingredient> data) {
        this.data = data;
    }
}
