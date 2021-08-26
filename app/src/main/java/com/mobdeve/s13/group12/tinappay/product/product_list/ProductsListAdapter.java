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

                i.putExtra(Keys.P_ID, data.get(productsListViewHolder.getBindingAdapterPosition()).getId());
                i.putExtra(Keys.P_IMG, data.get(productsListViewHolder.getBindingAdapterPosition()).getImg());
                i.putExtra(Keys.P_NAME, data.get(productsListViewHolder.getBindingAdapterPosition()).getName());
                i.putExtra(Keys.P_TYPE, data.get(productsListViewHolder.getBindingAdapterPosition()).getType());
                i.putExtra(Keys.P_PRICE, data.get(productsListViewHolder.getBindingAdapterPosition()).getPrice());
                i.putExtra(Keys.P_DESC, data.get(productsListViewHolder.getBindingAdapterPosition()).getDescription());

                ArrayList<String> ingredients = data.get(productsListViewHolder.getBindingAdapterPosition()).getIngredients();
                float[] prices = new float[ingredients.size()];
                for (int j = 0; j < prices.length; j++)
                    prices[j] = (j + 1) * 10;
                i.putExtra(Keys.PI_NAME, ingredients);
                i.putExtra(Keys.PI_PRICE, prices);


                v.getContext().startActivity(i);
            }
        });

        return productsListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductsListViewHolder holder, int position) {
        holder.setItemImage(data.get(position).getImg());
        holder.setItemField1(data.get(position).getName());
        holder.setItemField2(data.get(position).getType());
        holder.setItemField3(Float.toString(data.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
