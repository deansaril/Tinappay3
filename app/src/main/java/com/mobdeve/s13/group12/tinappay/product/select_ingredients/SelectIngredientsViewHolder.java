package com.mobdeve.s13.group12.tinappay.product.select_ingredients;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

public class SelectIngredientsViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout clContainer;

    private CardView cvContainer;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvLocation;

    private ImageButton ibUp;
    private EditText etQuantity;
    private ImageButton ibDown;

    public SelectIngredientsViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.clContainer = itemView.findViewById(R.id.cl_ic_item);
        this.cvContainer = itemView.findViewById(R.id.cv_ic);
        this.ivImg = itemView.findViewById(R.id.iv_ic_img);
        this.tvName = itemView.findViewById(R.id.tv_ic_name);
        this.tvType = itemView.findViewById(R.id.tv_ic_type);
        this.tvPrice = itemView.findViewById(R.id.tv_ic_price);
        this.tvLocation = itemView.findViewById(R.id.tv_ic_location);
        this.ibUp = itemView.findViewById(R.id.ib_ic_up);
        this.etQuantity = itemView.findViewById(R.id.et_ic_quantity);
        this.ibDown = itemView.findViewById(R.id.ib_ic_down);

        this.etQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etQuantity.setCursorVisible(true);
            }
        });

        this.etQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verifyQuantity();
                etQuantity.setCursorVisible(false);

                // Sauce: https://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-typing-in-edittext-in-android
                // TODO: Numpad enter not working
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    public CardView getContainer() {
        return this.cvContainer;
    }

    public void setImg (Bitmap img) {
        this.ivImg.setImageBitmap(img);
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
        if (status) {
            cvContainer.setCardBackgroundColor(ContextCompat.getColor(this.cvContainer.getContext(), R.color.tertiary));

            this.ibUp.setBackgroundResource(R.color.secondary);

            this.etQuantity.setBackgroundResource(R.color.primary);
            this.etQuantity.setCursorVisible(true);
            this.etQuantity.setText("1");

            this.ibDown.setBackgroundResource(R.color.secondary);
        }
        else {
            cvContainer.setCardBackgroundColor(ContextCompat.getColor(this.cvContainer.getContext(), R.color.primary));

            this.ibUp.setBackgroundResource(R.color.disabled_secondary);
            this.etQuantity.setBackgroundResource(R.color.disabled_primary);
            this.ibDown.setBackgroundResource(R.color.disabled_secondary);

            this.etQuantity.setText("0");
        }

        this.ibUp.setEnabled(status);
        this.etQuantity.setEnabled(status);
        this.ibDown.setEnabled(status);
    }

    public void setArrowListeners (View.OnClickListener upOnClickListener, View.OnClickListener downOnClickListener) {
        this.ibUp.setOnClickListener(upOnClickListener);
        this.ibDown.setOnClickListener(downOnClickListener);
    }

    public void setQuantityListeners (View.OnClickListener onClickListener, TextView.OnEditorActionListener onEditorActionListener) {
        this.etQuantity.setOnClickListener(onClickListener);
        this.etQuantity.setOnEditorActionListener(onEditorActionListener);
    }

    public void setQuantity (int amount) {
        this.etQuantity.setText(String.valueOf(amount));
    }

    public void changeQuantity (int amount) {
        int quantity = Integer.parseInt(etQuantity.getText().toString());

        if (amount == 1) {
            if (quantity + 1 <= 99)
                quantity++;
        }
        else
            if (quantity - 1 >= 1)
                quantity--;

        etQuantity.setText(String.valueOf(quantity));
    }

    public void verifyQuantity () {
        int quantity = Integer.parseInt(etQuantity.getText().toString());
        if (quantity > 99)
            etQuantity.setText("99");
        else if (quantity < 1)
            etQuantity.setText("1");
    }
}
