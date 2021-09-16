package com.mobdeve.s13.group12.tinappay.product.product_list;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

/**
 * This viewholder manages product list UI elements
 */
public class ProductsListViewHolder extends RecyclerView.ViewHolder {

    /* Class variables */
    private CardView cvContainer;
    private ImageView ivItemImage;
    private TextView tvName;
    private TextView tvType;

    /**
     * Instantiates a ProductsListViewHolder
     * @param itemView View - container connected to data
     */
    public ProductsListViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.cvContainer = itemView.findViewById(R.id.cv_gi);
        this.ivItemImage = itemView.findViewById(R.id.iv_gi_image);
        this.tvName = itemView.findViewById(R.id.tv_gi_field1);
        this.tvType = itemView.findViewById(R.id.tv_gi_field2);
    }



    /* Class functions */
    /**
     * Gets view containing all UI elements
     * @return CardView - parent view of all variables
     */
    public CardView getContainer() {
        return this.cvContainer;
    }

    /**
     * Sets displayed image of item
     * @param img Bitmap - image to be displayed
     */
    public void setItemImage(Bitmap img) {
        this.ivItemImage.setImageBitmap(img);
    }

    /**
     * Sets displayed name of item
     * @param text String - item name
     */
    public void setName(String text) {
        this.tvName.setText(text);
    }

    /**
     * Sets displayed type of item
     * @param text String - item type
     */
    public void setType(String text) {
        this.tvType.setText(text);
    }
}
