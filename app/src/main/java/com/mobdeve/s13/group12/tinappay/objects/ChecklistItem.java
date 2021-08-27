package com.mobdeve.s13.group12.tinappay.objects;

public class ChecklistItem {
    private String name;
    private boolean isChecked;

    public ChecklistItem () {}

    public ChecklistItem(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    public String getName() {
        return this.name;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked (boolean status) {
        this.isChecked = status;
    }
}
