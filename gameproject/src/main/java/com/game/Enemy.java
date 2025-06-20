package com.game;

public class Enemy extends Character {
    private long lastDamageTime = 0; // Las time it hurt player
    private long damageCooldown = 2000; // Cooldown time in milliseconds (2 sec)

    public Enemy() {
        this.maxHealth = 10;
        this.currentHealth = maxHealth;
        this.power = 1;
        this.moveSpeed = .7;
        this.isDead = false;
    }

    public boolean canAttack() {
        return (System.currentTimeMillis() - lastDamageTime) >= damageCooldown;
    }
    public void attackedPlayer() {
        lastDamageTime = System.currentTimeMillis();
    }
    public void takeDamage(int damage) {
        if (damage <= 0) {
            System.out.println("Damage must be positive! Enemy not hurt.");
            return;
        }
        currentHealth -= damage;
        if (currentHealth <= 0) {
            isDead = true;
            currentHealth = 0; // Ensure health doesn't go negative
        }
    }
}