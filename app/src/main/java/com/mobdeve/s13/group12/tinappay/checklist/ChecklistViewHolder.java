package com.mobdeve.s13.group12.tinappay.checklist;

import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group12.tinappay.R;

import org.jetbrains.annotations.NotNull;

public class ChecklistViewHolder extends RecyclerView.ViewHolder {
    private TextView tvCliItem;
    private CheckBox cbCliClicked;

    public ChecklistViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvCliItem = itemView.findViewById(R.id.tv_cli_item);
        this.cbCliClicked = itemView.findViewById(R.id.cb_cli_ticked);
    }

    public void setCliItem (String name) {
        this.tvCliItem.setText(name);
    }

    public void setCbCliClicked (boolean status) {
        if (status) {
            cbCliClicked.setChecked(true);
            tvCliItem.setPaintFlags(tvCliItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvCliItem.setTextColor(ContextCompat.getColor(this.cbCliClicked.getContext(), R.color.gray_2));
        }
        else {
            cbCliClicked.setChecked(false);
            tvCliItem.setPaintFlags(tvCliItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tvCliItem.setTextColor(ContextCompat.getColor(this.cbCliClicked.getContext(), R.color.black));
        }
    }

    public void setTickedCheckboxOnClickListener (CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.cbCliClicked.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void setChecked (boolean status) {
        if (status) {
            tvCliItem.setPaintFlags(tvCliItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvCliItem.setTextColor(ContextCompat.getColor(this.cbCliClicked.getContext(), R.color.gray_2));
        }
        else {
            tvCliItem.setPaintFlags(tvCliItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tvCliItem.setTextColor(ContextCompat.getColor(this.cbCliClicked.getContext(), R.color.black));
        }
    }
}
