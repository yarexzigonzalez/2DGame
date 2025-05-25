package com.game;

public abstract class Item {
    String item = null;
    int quantity = 0;
    String description = null;
    String type = null;
    private String name;
    private String imagePath;

    public Item(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public abstract void use();
}