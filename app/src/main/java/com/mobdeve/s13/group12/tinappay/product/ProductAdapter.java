package com.mobdeve.s13.group12.tinappay.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.ProductIngredient;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * This adapter connects product ingredients item data to the UI
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    /* Class variables */
    // Item data
    private HashMap<String, Object> data;

    /**
     * Instantiates a ProductAdapter
     * @param data HashMap - item data to be displayed
     */
    public ProductAdapter (HashMap<String, Object> data) {
        this.data = data;
    }



    /* Function overrides */
    @NonNull
    @NotNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_product_pricing, parent, false);

        ProductViewHolder productViewHolder = new ProductViewHolder(itemView);

        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int position) {
        Object key = data.keySet().toArray()[position];
        ProductIngredient pi = (ProductIngredient)data.get(key);
        holder.setName(pi.getName());
        holder.setQuantity(pi.getQuantity());
        holder.setPrice(pi.getPrice());
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
    public void setData (HashMap<String, Object> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}