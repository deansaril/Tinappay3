package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

public class SelectIngredientsViewHolder extends RecyclerView.ViewHolder {
    private CardView cvContainer;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvLocation;

    public SelectIngredientsViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.cvContainer = itemView.findViewById(R.id.cv_ic);
        this.ivImg = itemView.findViewById(R.id.iv_ic_img);
        this.tvName = itemView.findViewById(R.id.tv_ic_name);
        this.tvType = itemView.findViewById(R.id.tv_ic_type);
        this.tvPrice = itemView.findViewById(R.id.tv_ic_price);
        this.tvLocation = itemView.findViewById(R.id.tv_ic_location);
    }

    public CardView getContainer() {
        return this.cvContainer;
    }

    public void setImg (int img) {
        this.ivImg.setImageResource(img);
    }

    public void setName (String name) {
        this.tvName.setText(name);
    }

    public void setType (String type) {
        this.tvType.setText(type);
    }

    public void setPrice (float price) {
        this.tvPrice.setText(Float.toString(price));
    }

    public void setLocation (String location) {
        this.tvLocation.setText(location);
    }

    public void setSelectedOnClickListener (View.OnClickListener onClickListener) {
        this.cvContainer.setOnClickListener(onClickListener);
    }

    public void setSelected (boolean status) {
        if (status)
            cvContainer.setCardBackgroundColor(ContextCompat.getColor(this.cvContainer.getContext(), R.color.green));
        else
            cvContainer.setCardBackgroundColor(ContextCompat.getColor(this.cvContainer.getContext(), R.color.primary));
    }
}
