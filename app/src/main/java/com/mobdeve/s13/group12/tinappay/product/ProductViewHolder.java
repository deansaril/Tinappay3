package com.mobdeve.s13.group12.tinappay.product;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

/**
 * This viewholder manages product ingredients UI elements
 */
public class ProductViewHolder extends RecyclerView.ViewHolder {

    /* Class variables */
    private TextView tvName;
    private TextView tvQuantity;
    private TextView tvPrice;

    /**
     * Instantiates a ProductViewHolder
     * @param itemView View - container connected to data
     */
    public ProductViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvName = itemView.findViewById(R.id.tv_pp_name);
        this.tvQuantity = itemView.findViewById(R.id.tv_pp_quantity);
        this.tvPrice = itemView.findViewById(R.id.tv_pp_price);
    }



    /* Class functions */
    /**
     * Sets ingredient name
     * @param name String - ingredient name
     */
    public void setName (String name) {
        this.tvName.setText(name);
    }

    /**
     * Sets ingredient quantity
     * @param amount - ingredient quantity
     */
    public void setQuantity (int amount) { this.tvQuantity.setText(String.valueOf(amount)); }

    /**
     * Sets ingredient price
     * @param price - total ingredient price
     */
    public void setPrice (float price) {
        this.tvPrice.setText(String.valueOf(price));
    }
}
