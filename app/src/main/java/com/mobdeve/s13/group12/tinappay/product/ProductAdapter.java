package com.mobdeve.s13.group12.tinappay.product;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.ProductIngredient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private HashMap data;

    public ProductAdapter (HashMap data) {
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
        // TODO: Get price of ingredients
        //holder.setPrice(pi.getPrice());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void setData (HashMap data) {
        this.data = data;
    }
}
