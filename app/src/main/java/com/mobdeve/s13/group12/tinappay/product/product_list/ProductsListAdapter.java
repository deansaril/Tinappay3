package com.mobdeve.s13.group12.tinappay.product.product_list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.product.ProductActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListViewHolder> {
    private ArrayList<Product> data;

    public ProductsListAdapter (ArrayList<Product> data) {
        this.data = data;
    }

    @NonNull
    @NotNull
    @Override
    public ProductsListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_grid_item, parent, false);

        ProductsListViewHolder productsListViewHolder = new ProductsListViewHolder(itemView);

        productsListViewHolder.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent (v.getContext(), ProductActivity.class);
                i.putExtra(Keys.KEY_PRODUCT.name(), data.get(productsListViewHolder.getBindingAdapterPosition()));

                v.getContext().startActivity(i);
            }
        });

        return productsListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductsListViewHolder holder, int position) {
        String name = data.get(position).getName();
        String type = data.get(position).getType();

        if (name.length() > 15)
            name = name.substring(0, 15) + "...";

        if (type.length() > 15)
            type = type.substring(0, 15) + "...";

        holder.setItemImage(data.get(position).getImg());
        holder.setItemField1(name);
        holder.setItemField2(type);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void setData (ArrayList<Product> data) {
        this.data = data;
    }
}
