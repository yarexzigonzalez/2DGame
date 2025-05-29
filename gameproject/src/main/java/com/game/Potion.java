package com.game;

public abstract class Potion extends Item {
    protected int effectAmount;
    protected int durationSeconds;
    private double spawnX;
    private double spawnY;

    public Potion(String name, String imagePath, int effectAmount, int durationSeconds) {
        super(name, imagePath);
        this.effectAmount = effectAmount;
        this.durationSeconds = durationSeconds;
    }

    public int getEffectAmount() {
        return effectAmount;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void use(Player player, GameController controller) {
        use(player); // fallback logic
    }

    public abstract void use(Player player);

    public void setSpawnPosition(double x, double y) {
        this.spawnX = x;
        this.spawnY = y;
    }
    public double getSpawnX() {
        return spawnX;
    }
    public double getSpawnY() {
        return spawnY;
    }
}