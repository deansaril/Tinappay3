package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_list;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

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

    public CardView getContainer() {
        return this.cvContainer;
    }

    public void setItemImage(Bitmap bitmap) {this.ivItemImage.setImageBitmap(bitmap);}

    public void setItemField1(String text) {
        this.tvItemField1.setText(text);
    }

    public void setItemField2(String text) {
        this.tvItemField2.setText(text);
    }

    public void setItemField3(String text) {
        this.tvItemField3.setText(text);
    }

    public void setItemField4(String text) {
        this.tvItemField4.setText(text);
    }
}
