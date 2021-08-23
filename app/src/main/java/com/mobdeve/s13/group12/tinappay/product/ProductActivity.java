package com.mobdeve.s13.group12.tinappay.product;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

public class ProductActivity extends AppCompatActivity {
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ivImg = findViewById(R.id.iv_p_img);
        tvName = findViewById(R.id.tv_p_name);
        tvType = findViewById(R.id.tv_p_type);
        tvPrice = findViewById(R.id.tv_p_price);

        Intent i = getIntent();

        int img = i.getIntExtra(Keys.KEY_ITEM_IMG, 0);
        String name = i.getStringExtra(Keys.KEY_ITEM_NAME);
        String type = i.getStringExtra(Keys.KEY_ITEM_TYPE);
        float price = i.getFloatExtra(Keys.KEY_ITEM_PRICE, 0);

        this.ivImg.setImageResource(img);
        this.tvName.setText(name);
        this.tvType.setText(type);
        this.tvPrice.setText(Float.toString(price));
    }
}