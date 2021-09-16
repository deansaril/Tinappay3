package com.mobdeve.s13.group12.tinappay.product;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    private TextView tvQuantity;
    private TextView tvPrice;

    public ProductViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvName = itemView.findViewById(R.id.tv_pp_name);
        this.tvQuantity = itemView.findViewById(R.id.tv_pp_quantity);
        this.tvPrice = itemView.findViewById(R.id.tv_pp_price);
    }

    public void setName (String name) {
        this.tvName.setText(name);
    }

    public void setQuantity (int amount) { this.tvQuantity.setText(String.valueOf(amount)); }

    public void setPrice (float price) {
        this.tvPrice.setText(String.valueOf(price));
    }
}
