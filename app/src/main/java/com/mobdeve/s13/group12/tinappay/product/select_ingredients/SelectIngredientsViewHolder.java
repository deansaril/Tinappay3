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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

/**
 * This viewholder manages ingredient selection UI elements
 */
public class SelectIngredientsViewHolder extends RecyclerView.ViewHolder {

    /* Class variables */
    private CardView cvContainer;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvLocation;

    private ImageButton ibUp;
    private EditText etQuantity;
    private ImageButton ibDown;

    /**
     * Instantiates a SelectIngredientsViewHolder
     * @param itemView View - container connected to data
     */
    public SelectIngredientsViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.cvContainer = itemView.findViewById(R.id.cv_ic);
        this.ivImg = itemView.findViewById(R.id.iv_ic_img);
        this.tvName = itemView.findViewById(R.id.tv_ic_name);
        this.tvType = itemView.findViewById(R.id.tv_ic_type);
        this.tvPrice = itemView.findViewById(R.id.tv_ic_price);
        this.tvLocation = itemView.findViewById(R.id.tv_ic_location);
        this.ibUp = itemView.findViewById(R.id.ib_ic_up);
        this.etQuantity = itemView.findViewById(R.id.et_ic_quantity);
        this.ibDown = itemView.findViewById(R.id.ib_ic_down);

        // Sets listener to show cursor when editing field
        this.etQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etQuantity.setCursorVisible(true);
            }
        });

        // Sets listener to verify quantity entered and hide cursor after editing field
        this.etQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verifyQuantity();
                etQuantity.setCursorVisible(false);

                // Sauce: https://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-typing-in-edittext-in-android
                // TODO (Low prio): Numpad enter not working
                // If user presses "Submit" or "Enter" key
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Set displayed item image
     * @param img Bitmap - item image
     */
    public void setImg (Bitmap img) {
        this.ivImg.setImageBitmap(img);
    }

    /**
     * Set displayed item name
     * @param name String - item name
     */
    public void setName (String name) {
        this.tvName.setText(name);
    }

    /**
     * Sets displayed item type
     * @param type String - item type
     */
    public void setType (String type) {
        this.tvType.setText(type);
    }

    /**
     * Sets displayed item price
     * @param price float - item price
     */
    public void setPrice (float price) {
        this.tvPrice.setText(Float.toString(price));
    }

    /**
     * Sets displayed item location
     * @param location String - item location
     */
    public void setLocation (String location) {
        this.tvLocation.setText(location);
    }

    /**
     * Sets listener for clicks on item card
     * @param onClickListener OnClickListener - click listener
     */
    public void setSelectedOnClickListener (View.OnClickListener onClickListener) {
        this.cvContainer.setOnClickListener(onClickListener);
    }

    /**
     * Sets whether item quantity can be edited
     * @param status - "editable" status of items
     */
    public void setSelected (boolean status) {
        // If item can be edited
        if (status) {
            // Set background colors of elements to appear enabled
            this.cvContainer.setCardBackgroundColor(ContextCompat.getColor(this.cvContainer.getContext(), R.color.tertiary));
            this.ibUp.setBackgroundResource(R.color.secondary);
            this.etQuantity.setBackgroundResource(R.color.primary);
            this.ibDown.setBackgroundResource(R.color.secondary);

            // Pre-set value of quantity editors
            this.etQuantity.setCursorVisible(true);
            this.etQuantity.setText("1");
        }
        // If item cannot be edited
        else {
            // Set background colors of elements to appear disabled
            cvContainer.setCardBackgroundColor(ContextCompat.getColor(this.cvContainer.getContext(), R.color.primary));
            this.ibUp.setBackgroundResource(R.color.disabled_secondary);
            this.etQuantity.setBackgroundResource(R.color.disabled_primary);
            this.ibDown.setBackgroundResource(R.color.disabled_secondary);

            // Clear value of quantity editor
            this.etQuantity.setText("0");
        }

        // Set status of quantity editors to be enabled/disabled
        this.ibUp.setEnabled(status);
        this.etQuantity.setEnabled(status);
        this.ibDown.setEnabled(status);
    }

    /**
     * Sets listeners for quantity incrementing and decrementing
     * @param upOnClickListener OnClickListener - click listener for incrementing
     * @param downOnClickListener OnClickListener - click listener for decrementing
     */
    public void setArrowListeners (View.OnClickListener upOnClickListener, View.OnClickListener downOnClickListener) {
        this.ibUp.setOnClickListener(upOnClickListener);
        this.ibDown.setOnClickListener(downOnClickListener);
    }

    /**
     * Sets quantity of selected item
     * @param amount int - item quantity
     */
    public void setQuantity (int amount) {
        this.etQuantity.setText(String.valueOf(amount));
    }

    /**
     * Increments or decrements quantity of selected item
     * @param amount int - amount to change quantity
     */
    public void changeQuantity (int amount) {
        int quantity = Integer.parseInt(etQuantity.getText().toString());

        // Increment quantity
        if (amount == 1) {
            if (quantity + 1 <= 99)
                quantity++;
        }
        // Decrement quantity
        else
            if (quantity - 1 >= 1)
                quantity--;

        // Display new quantity
        etQuantity.setText(String.valueOf(quantity));
    }

    /**
     * Sets quantity to accepted amounts [1 - 99]
     */
    public void verifyQuantity () {
        int quantity = Integer.parseInt(etQuantity.getText().toString());
        if (quantity > 99)
            etQuantity.setText(R.string.max_quantity);
        else if (quantity < 1)
            etQuantity.setText(R.string.min_quantity);
    }
}
