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

/**
 * Handles the loading of the ingredients and their layouts to be set to the recycler view
 */
public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListViewHolder>{
    private ArrayList<Ingredient> data;

    public IngredientsListAdapter (ArrayList<Ingredient> data) { this.data = data; }

    @NonNull
    @NotNull
    @Override
    public IngredientsListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_linear_item, parent, false);
        IngredientsListViewHolder ingredientsListViewHolder = new IngredientsListViewHolder(itemView);

        //sets the click listener of the ingredient card to allow user to go to the page of the clicked ingredient
        ingredientsListViewHolder.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent (v.getContext(), IngredientActivity.class);

                Ingredient item = data.get(ingredientsListViewHolder.getBindingAdapterPosition());
                i.putExtra(Keys.KEY_INGREDIENT.name(), item);

                v.getContext().startActivity(i);
            }
        });

        return ingredientsListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull IngredientsListViewHolder holder, int position) {
        holder.setItemImage(data.get(position).getImg());

        String name = data.get(position).getName();
        String type = data.get(position).getType();
        String location = data.get(position).getLocation();

        if (name.length() > 30)
            name = name.substring(0, 30) + "...";

        if (type.length() > 30)
            type = type.substring(0, 30) + "...";

        if (location.length() > 30)
            location = type.substring(0, 30) + "...";

        holder.setItemField1(name);
        holder.setItemField2(type);
        holder.setItemField4(location);

        holder.setItemField3(Float.toString(data.get(position).getPrice()));

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    /**
     * Sets the data of the adapter
     * @param data is the array of Ingredients to be loaded to the adapter
     */
    public void setData (ArrayList<Ingredient> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
