package com.game;

public class Enemy extends Character {
    private long lastDamageTime = 0; // Las time it hurt player
    private long damageCooldown = 1000; // Cooldown time in milliseconds (1 sec)
    private double speed;

    public Enemy() {
        this.maxHealth = 10;
        this.currentHealth = maxHealth;
        this.power = 1;
        this.moveSpeed = 1;
        this.isDead = false;
    }

    public boolean canAttack() {
        return (System.currentTimeMillis() - lastDamageTime) >= damageCooldown;
    }
    public void attackedPlayer() {
        lastDamageTime = System.currentTimeMillis();
    }
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            isDead = true;
            currentHealth = 0; // Ensure health doesn't go negative
        }
    }
}