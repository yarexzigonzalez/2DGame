package com.game;

public class SpeedPotion extends Potion {
    public SpeedPotion(String name, String imagePath, int boostAmount, int durationSeconds) {
        super(name, imagePath, boostAmount, durationSeconds);
    }

    @Override
    public void use(Player player) {
        player.applySpeedBoost(effectAmount, durationSeconds);
        System.out.println("Used Speed Potion! +" + effectAmount + " speed for " + durationSeconds + "s.");
    }

    @Override
    public void use(Player player, GameController controller) {
        use(player); // apply logic
        controller.showBoostMessage("speed", "+ " + effectAmount + " SPD", durationSeconds);
        System.out.println("message shown on GUI");
    }
}
