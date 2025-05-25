package com.game;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends Character{
    // Player class repeats variables from Character class (redundant)
    // Rewrote a bit to make less redundant
    public int armor;
    public int attackDelay;

    // Just set those in the constructor, not redeclaring them
    public Player(){
        this.currentHealth = 10;
        this.maxHealth = 10;
        this.moveSpeed = 30;
        this.power = 2;
        this.armor = 0;
        this.isDead = false;
        this.attackDelay = 5; //not sure what to do with this yet
    }

    public void damaged(int power){
        this.currentHealth -= power;
        if (this.currentHealth <= 0){
            this.isDead = true;
        }
    }

    public void healthPotion(int healAmount){
        if ((this.currentHealth + healAmount) >= this.maxHealth){
            this.currentHealth = this.maxHealth;
        } else {
            this.currentHealth += healAmount;
        }
    }

    public void powerUpPotion(){
        //trying to figure out a timer to make the powerup only last for a limited time
    }

    private boolean facingRight = true; // Default facing direction
    
    public boolean isFacingRight() {
        return facingRight;
    }
    
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
    
}