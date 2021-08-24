package com.mobdeve.s13.group12.tinappay.product;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private ArrayList<String> dataNames;
    private float[] dataPrices;

    public ProductAdapter (ArrayList<String> names, float[] prices) {
        this.dataNames = names;
        this.dataPrices = prices;
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
        holder.setName(dataNames.get(position));
        holder.setPrice(dataPrices[position]);
    }

    @Override
    public int getItemCount() {
        return this.dataNames.size();
    }
}
