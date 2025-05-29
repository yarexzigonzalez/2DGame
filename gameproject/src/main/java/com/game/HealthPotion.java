package com.game;

public class HealthPotion extends Potion {
    public HealthPotion(String name, String imagePath, int healAmount) {
        super(name, imagePath, healAmount, 0);
    }

    @Override
    public void use(Player player) {
        player.heal(effectAmount);
        System.out.println("Used Health Potion! Healed " + effectAmount + " HP.");
        
    }
    @Override
    public void use(Player player, GameController controller) {
        use(player); // apply logic
        controller.showBoostMessage("heal", "+ " + effectAmount + " HP", 2);
        System.out.println("message shown on GUI");
    }
}
