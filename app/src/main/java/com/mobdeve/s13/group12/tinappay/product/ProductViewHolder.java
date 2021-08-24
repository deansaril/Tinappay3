package com.mobdeve.s13.group12.tinappay.product;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout clContainer;
    private TextView tvName;
    private TextView tvPrice;

    public ProductViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.clContainer = itemView.findViewById(R.id.cl_pp);
        this.tvName = itemView.findViewById(R.id.tv_pp_name);
        this.tvPrice = itemView.findViewById(R.id.tv_pp_price);
    }

    public ConstraintLayout getContainer() {
        return clContainer;
    }

    public void setName (String name) {
        this.tvName.setText(name);
    }

    public void setPrice (float price) {
        this.tvPrice.setText(Float.toString(price));
    }
}
