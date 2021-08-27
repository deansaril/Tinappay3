package com.mobdeve.s13.group12.tinappay.ingredient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s13.group12.tinappay.R;

import com.mobdeve.s13.group12.tinappay.objects.Keys;




public class IngredientActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvLocation;
    private ImageButton ibEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        bindComponents();
        initComponents();
    }

    private void bindComponents() {
        this.tvTitle = findViewById(R.id.tv_i_title);

        this.ivImg = findViewById(R.id.iv_i_img);
        this.tvName = findViewById(R.id.tv_i_name);
        this.tvType = findViewById(R.id.tv_i_type);
        this.tvPrice = findViewById(R.id.tv_i_price);
        this.tvLocation = findViewById(R.id.tv_i_location);
        this.ibEdit = findViewById(R.id.ib_i_edit);

    }

    private void initComponents() {
        Intent i = getIntent();

        int img = i.getIntExtra(Keys.KEY_INGREDIENT_IMG, 0);
        String name = i.getStringExtra(Keys.KEY_INGREDIENT_NAME);
        String type = i.getStringExtra(Keys.KEY_INGREDIENT_TYPE);
        float price = i.getFloatExtra(Keys.KEY_INGREDIENT_PRICE, 0);
        String location = i.getStringExtra(Keys.KEY_INGREDIENT_LOCATION);

        this.tvTitle.setText(name);
        this.ivImg.setImageResource(img);
        this.tvName.setText(name);
        this.tvType.setText(type);
        this.tvPrice.setText(Float.toString(price));
        this.tvLocation.setText(location);
    }
}