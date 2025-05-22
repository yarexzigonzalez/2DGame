package com.game;

public abstract class Item extends Character {
    String item;
    int quantity;
    String description;
    String type;
}
    int hp;
    int atk;
    double moveSpeed;
    double jumpHeight;
    long atkDelay;
    int atkSetting;

    public Item(String item, String type, int quantity, String description) {
        this.item = item;
        this.type = type;
        this.quantity = quantity;
        this.description = description;
    }

    // ITEM NAME + TYPE + QUANTITY
    public String getItem() { return item; }
    public String getType() { return type; }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getQuantity() {
        return quantity;
    }
    public void addQuantity(int quantity) {
        this.quantity += 1;
    }
    public void subQuantity(int quantity) {
        this.quantity -= 1;
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }

    // ITEM DESCRIPTION
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}