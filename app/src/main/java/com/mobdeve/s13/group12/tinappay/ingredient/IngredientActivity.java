package com.mobdeve.s13.group12.tinappay.ingredient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s13.group12.tinappay.R;

import com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify.IngredientEditActivity;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.product.ProductActivity;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductEditActivity;


public class IngredientActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvLocation;
    private ImageButton ibEdit;

    private ActivityResultLauncher editActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = result.getData();
                        setIntent(i);

                        int img = i.getIntExtra(Keys.KEY_INGREDIENT_IMG, 0);
                        String name = i.getStringExtra(Keys.KEY_INGREDIENT_NAME);
                        String type = i.getStringExtra(Keys.KEY_INGREDIENT_TYPE);
                        float price = i.getFloatExtra(Keys.KEY_INGREDIENT_PRICE, 0);
                        String location = i.getStringExtra(Keys.KEY_INGREDIENT_LOCATION);

                        tvTitle.setText(name);
                        ivImg.setImageResource(img);
                        tvName.setText(name);
                        tvType.setText(type);
                        tvPrice.setText(Float.toString(price));
                        tvLocation.setText(location);
                    }
                }
            }
    );

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

        initEditButton();
    }

    private void initEditButton() {
        this.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldIntent = getIntent();
                Intent newIntent = new Intent(IngredientActivity.this, IngredientEditActivity.class);

                newIntent.putExtra(Keys.KEY_INGREDIENT_ID, oldIntent.getStringExtra(Keys.KEY_INGREDIENT_ID));
                newIntent.putExtra(Keys.KEY_INGREDIENT_IMG, oldIntent.getIntExtra(Keys.KEY_INGREDIENT_IMG, 0));
                newIntent.putExtra(Keys.KEY_INGREDIENT_NAME, oldIntent.getStringExtra(Keys.KEY_INGREDIENT_NAME));
                newIntent.putExtra(Keys.KEY_INGREDIENT_TYPE, oldIntent.getStringExtra(Keys.KEY_INGREDIENT_TYPE));
                newIntent.putExtra(Keys.KEY_INGREDIENT_PRICE, oldIntent.getFloatExtra(Keys.KEY_INGREDIENT_PRICE, 0));
                newIntent.putExtra(Keys.KEY_INGREDIENT_LOCATION, oldIntent.getStringExtra(Keys.KEY_INGREDIENT_LOCATION));

                editActivityResultLauncher.launch(newIntent);
            }
        });
    }
}