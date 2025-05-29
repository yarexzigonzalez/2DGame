package com.game;

public class DamagePotion extends Potion {
    public DamagePotion(String name, String imagePath, int boostAmount, int durationSeconds) {
        // Call the superclass constructor with the potion's name, image path, boost amount, and duration
        super(name, imagePath, boostAmount, durationSeconds);
    }

    @Override
    public void use(Player player) {
        player.applyDamageBoost(effectAmount, durationSeconds);
        System.out.println("Used Damage Potion! +" + effectAmount + " power for " + durationSeconds + "s.");
    }

    @Override
    public void use(Player player, GameController controller) {
        use(player); // apply logic
        controller.showBoostMessage("damage", "+ " + effectAmount + " ATK", durationSeconds);
        System.out.println("message shown on GUI");
    }
}
