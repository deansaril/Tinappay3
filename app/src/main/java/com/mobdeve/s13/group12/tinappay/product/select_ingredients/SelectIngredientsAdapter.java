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
import java.util.HashMap;

/**
 * This adapter connects ingredient selection item data to the UI
 */
public class SelectIngredientsAdapter extends RecyclerView.Adapter<SelectIngredientsViewHolder> {

    /* Class variables */
    // Item data
    private ArrayList<Ingredient> data;
    private HashMap<String, Integer> quantities;

    /**
     * Instantiates a SelectIngredientsAdapter
     * @param data ArrayList - data to be displayed
     * @param quantities HashMap - list of selected items and their quantity
     */
    public SelectIngredientsAdapter (ArrayList<Ingredient> data, HashMap<String, Integer> quantities) {
        this.data = data;
        this.quantities = quantities;
    }



    /* Function overrides */
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
        // Assigns values to UI elements
        holder.setImg(data.get(position).getImg());
        holder.setName(data.get(position).getName());
        holder.setType(data.get(position).getType());
        holder.setPrice(data.get(position).getPrice());
        holder.setLocation(data.get(position).getLocation());

        // If current item is in selected list, enable fields
        if (quantities.containsKey(data.get(position).getId())) {
            holder.setSelected(true);
            int quantity = quantities.get(data.get(position).getId());
            holder.setQuantity(quantity);
        }
        // If current item is not in selected list, disable fields
        else
            holder.setSelected(false);

        // Sets listener to enable quantity selector
        holder.setSelectedOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String curId = data.get(position).getId();
                boolean inList = quantities.containsKey(curId);

                // If item is not yet in the list of selected ingredients, add and enable interaction
                if (!inList) {
                    quantities.put(curId, 1);
                    holder.setSelected(true);
                }

                // If item is already in the list of selected ingredients, remove and disable interaction
                else {
                    quantities.remove(curId);
                    holder.setSelected(false);
                }
            }
        });

        // Sets listener for incrementing/decrementing quantity
        holder.setArrowListeners(
            // Listener for incrementing
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String curId = data.get(position).getId();
                    int quantity = quantities.get(curId);
                    quantity++;

                    // Save new quantity
                    quantities.put(curId, quantity);
                    holder.changeQuantity(1);
                }
            },
            // Listener for decrementing
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String curId = data.get(position).getId();
                    int quantity = quantities.get(curId);
                    quantity--;

                    // Save new quantity
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



    /* Class functions */
    /**
     * Sets assigned item data
     * @param data ArrayList - item data
     */
    public void setData (ArrayList<Ingredient> data) {
        this.data = data;
    }

    /**
     * Gets selected items and their quantities
     * @return HashMap - quantities mapped to correct items
     */
    public HashMap<String, Integer> getQuantities() { return this.quantities; }
}
