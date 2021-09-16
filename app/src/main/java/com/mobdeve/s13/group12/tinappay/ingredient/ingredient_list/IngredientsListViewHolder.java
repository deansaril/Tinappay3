package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_list;

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
 * Sets up the layout of the ingredient to be loaded from the adapter data array
 */
public class IngredientsListViewHolder extends RecyclerView.ViewHolder{
    private CardView cvContainer;
    private ImageView ivItemImage;
    private TextView tvItemField1;
    private TextView tvItemField2;
    private TextView tvItemField3;
    private TextView tvItemField4;

    public IngredientsListViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.cvContainer = itemView.findViewById(R.id.cv_li);
        this.ivItemImage = itemView.findViewById(R.id.iv_li_image);
        this.tvItemField1 = itemView.findViewById(R.id.tv_li_field1);
        this.tvItemField2 = itemView.findViewById(R.id.tv_li_field2);
        this.tvItemField3 = itemView.findViewById(R.id.tv_li_field3);
        this.tvItemField4 = itemView.findViewById(R.id.tv_li_field4);

    }

    /**
     * Returns the card view of the ingredient being loaded
     * @return the whole card view layout of the ingredient
     */
    public CardView getContainer() {
        return this.cvContainer;
    }

    /**
     * Sets the image of the ingredient in the Card view
     * @param bitmap the bitmap of the image to be loaded to the Card's ImageView
     */
    public void setItemImage(Bitmap bitmap) {this.ivItemImage.setImageBitmap(bitmap);}

    /**
     * Sets the text of the first text field, whose text color is different and text size is bigger than other text fields
     */
    public void setItemField1(String text) {
        this.tvItemField1.setText(text);
    }

    /**
     * Sets the text of the second text field
     */
    public void setItemField2(String text) {
        this.tvItemField2.setText(text);
    }

    /**
     * Sets the text of the third text field
     */
    public void setItemField3(String text) {
        this.tvItemField3.setText(text);
    }

    /**
     * Sets the text of the fourth text field
     */
    public void setItemField4(String text) {
        this.tvItemField4.setText(text);
    }
}
