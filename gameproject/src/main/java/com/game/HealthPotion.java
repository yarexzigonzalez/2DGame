package com.game;

public class HealthPotion extends Item {
    private int healAmount;

    public HealthPotion(String name, String imagePath, int healAmount) {
        super(name, imagePath);
        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    @Override
    public void use() {
        System.out.println("Healed " + healAmount + " HP!");
    }
}
