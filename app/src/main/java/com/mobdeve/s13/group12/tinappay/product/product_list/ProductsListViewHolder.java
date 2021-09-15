package com.mobdeve.s13.group12.tinappay.product.product_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

public class ProductsListViewHolder extends RecyclerView.ViewHolder {
    private CardView cvContainer;
    private ImageView ivItemImage;
    private TextView tvItemField1;
    private TextView tvItemField2;
    private TextView tvItemField3;
    private TextView tvItemField4;

    public ProductsListViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.cvContainer = itemView.findViewById(R.id.cv_gi);
        this.ivItemImage = itemView.findViewById(R.id.iv_gi_image);
        this.tvItemField1 = itemView.findViewById(R.id.tv_gi_field1);
        this.tvItemField2 = itemView.findViewById(R.id.tv_gi_field2);
        this.tvItemField3 = itemView.findViewById(R.id.tv_gi_field3);
        this.tvItemField4 = itemView.findViewById(R.id.tv_gi_field4);

        tvItemField3.setVisibility(View.GONE);
        tvItemField4.setVisibility(View.GONE);
    }

    public CardView getContainer() {
        return this.cvContainer;
    }

    public void setItemImage(int img) {
        this.ivItemImage.setImageResource(img);
    }

    public void setItemField1(String text) {
        this.tvItemField1.setText(text);
    }

    public void setItemField2(String text) {
        this.tvItemField2.setText(text);
    }
}
