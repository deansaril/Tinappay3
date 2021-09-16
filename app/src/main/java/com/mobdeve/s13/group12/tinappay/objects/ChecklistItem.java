package com.mobdeve.s13.group12.tinappay.objects;

import java.io.Serializable;

/**
 * This class is a database model of checklist items
 */
public class ChecklistItem implements Serializable {
    private String name;
    private boolean isChecked;

    /**
     * Default constructor of ChecklistItem
     */
    public ChecklistItem () {}

    /**
     * Instantiates a ChecklistItem
     * @param name String - item name
     * @param isChecked boolean - status if item is checked
     */
    public ChecklistItem(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    /**
     * Gets name of checklist item
     * @return String - name of item
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets "checked" status of checklist item
     * @return boolean - status of item
     */
    public boolean isChecked() {
        return this.isChecked;
    }
}
