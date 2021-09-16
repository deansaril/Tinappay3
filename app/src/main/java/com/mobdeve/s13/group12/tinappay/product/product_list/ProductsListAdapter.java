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

/**
 * This adapter connects product list item data to the UI
 */
public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListViewHolder> {

    /* Class variables */
    // Item data
    private ArrayList<Product> data;

    /**
     * Instantiates a ProductsListAdapter
     * @param data ArrayList - item data to be displayed
     */
    public ProductsListAdapter (ArrayList<Product> data) {
        this.data = data;
    }



    /* Function overrides */
    @NonNull
    @NotNull
    @Override
    public ProductsListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_grid_item, parent, false);

        ProductsListViewHolder productsListViewHolder = new ProductsListViewHolder(itemView);

        // Sets listener for moving to product details
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

        // Trims name for excess characters
        if (name.length() > 15)
            name = name.substring(0, 15) + "...";

        // Trims type for excess characters
        if (type.length() > 15)
            type = type.substring(0, 15) + "...";

        // Assigns values to UI elements
        holder.setItemImage(data.get(position).getImg());
        holder.setName(name);
        holder.setType(type);
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
    public void setData (ArrayList<Product> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
