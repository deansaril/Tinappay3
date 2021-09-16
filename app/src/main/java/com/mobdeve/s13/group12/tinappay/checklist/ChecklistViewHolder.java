package com.mobdeve.s13.group12.tinappay.checklist;

import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

/**
 * This viewholder manages checklist UI elements
 */
public class ChecklistViewHolder extends RecyclerView.ViewHolder {

    /* Class variables */
    private TextView tvName;
    private CheckBox cbChecked;

    /**
     * Instantiates a ChecklistViewHolder
     * @param itemView View - container connected to data
     */
    public ChecklistViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvName = itemView.findViewById(R.id.tv_cli_item);
        this.cbChecked = itemView.findViewById(R.id.cb_cli_ticked);
    }



    /* Class functions */
    /**
     * Sets name of displayed item
     * @param name String - item name
     */
    public void setName (String name) {
        this.tvName.setText(name);
    }

    /**
     * Sets listener for checkbox button
     * @param onClickListener OnClickListener - click listener
     */
    public void setCheckListener (View.OnClickListener onClickListener) {
        this.cbChecked.setOnClickListener(onClickListener);
    }

    /**
     * Sets "checked" status of displayed item
     * @param status boolean - if an item is checked
     */
    public void setChecked (boolean status) {
        if (status) {
            tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvName.setTextColor(ContextCompat.getColor(this.cbChecked.getContext(), R.color.gray_2));
        }
        else {
            tvName.setPaintFlags(tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tvName.setTextColor(ContextCompat.getColor(this.cbChecked.getContext(), R.color.black));
        }
    }
}
