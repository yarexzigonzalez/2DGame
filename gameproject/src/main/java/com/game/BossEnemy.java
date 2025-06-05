package com.game;

public class BossEnemy extends Enemy {
    public BossEnemy() {
        this.maxHealth = 50;
        this.currentHealth = maxHealth;
        this.power = 2;
        this.moveSpeed = 1; // Slower than basic
        this.isDead = false;
    }
}
