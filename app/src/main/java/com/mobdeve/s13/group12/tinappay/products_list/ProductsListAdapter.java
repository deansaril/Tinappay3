package com.mobdeve.s13.group12.tinappay.products_list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;

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

        /*productsListViewHolder.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent (v.getContext(), ProductsListActivity.class);

                i.putExtra(Keys.KEY_ITEM_IMG, data.get(productsListViewHolder.getAdapterPosition()).getImageId());
                i.putExtra(Keys.KEY_ITEM_NAME, data.get(productsListViewHolder.getAdapterPosition()).getName());
                i.putExtra(Keys.KEY_ITEM_TYPE, data.get(productsListViewHolder.getAdapterPosition()).getType());
                i.putExtra(Keys.KEY_ITEM_PRICE, data.get(productsListViewHolder.getAdapterPosition()).getPrice());

                v.getContext().startActivity(i);
            }
        });
        */

        return productsListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductsListViewHolder holder, int position) {
        holder.setItemImage(data.get(position).getImageId());
        holder.setItemField1(data.get(position).getName());
        holder.setItemField2(data.get(position).getType());
        holder.setItemField3(Float.toString(data.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
