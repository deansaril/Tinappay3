package com.mobdeve.s13.group12.tinappay.product;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.ProductIngredient;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private HashMap<String, Object> data;

    public ProductAdapter (HashMap<String, Object> data) {
        this.data = data;
    }

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

    public void setData (HashMap<String, Object> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}