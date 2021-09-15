package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.ProductIngredient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectIngredientsAdapter extends RecyclerView.Adapter<SelectIngredientsViewHolder> {
    private ArrayList<Ingredient> data;
    private HashMap<String, Integer> quantities;
    private ArrayList<String> selected;

    public SelectIngredientsAdapter (ArrayList<Ingredient> data, HashMap<String, Integer> quantities) {
        this.data = data;
        this.quantities = quantities;
    }

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
        View itemView = inflater.inflate(R.layout.data_select_ingredient, parent, false);

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

        if (quantities.containsKey(data.get(position).getId())) {
            holder.setSelected(true);
            int quantity = quantities.get(data.get(position).getId());
            holder.setQuantity(quantity);
        }
        else
            holder.setSelected(false);
        /*
        if (selected.contains(data.get(position).getId()))
            holder.setSelected(true);
        else
            holder.setSelected(false);
         */

        holder.setSelectedOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String curId = data.get(position).getId();
                boolean inList = quantities.containsKey(curId);

                // If item is not yet in the list of selected ingredients
                if (!inList) {
                    // Add and enable interaction
                    quantities.put(curId, 1);
                    holder.setSelected(true);
                }

                // If item is already in the list of selected ingredients
                else {
                    // Remove and disable interaction
                    quantities.remove(curId);
                    holder.setSelected(false);
                }
            }
        });

        holder.setArrowListeners(
            // Up arrow
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String curId = data.get(position).getId();
                    int quantity = quantities.get(curId);
                    quantity++;
                    quantities.put(curId, quantity);
                    holder.changeQuantity(1);
                }
            },
            // Down arrow
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String curId = data.get(position).getId();
                    int quantity = quantities.get(curId);
                    quantity--;
                    quantities.put(curId, quantity);
                    holder.changeQuantity(-1);
                }
            }
        );
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void setData (ArrayList<Ingredient> data) {
        this.data = data;
    }

    public ArrayList<String> getSelected() {
        return this.selected;
    }

    /*
    public HashMap<String, Object> getIngredients() {
        return this.ingredients;
    }
     */

    public HashMap<String, Integer> getQuantities() { return this.quantities; }
}
