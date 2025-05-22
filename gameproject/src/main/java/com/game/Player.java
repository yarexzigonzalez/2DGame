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

    public void healthPotion(){
        if ((this.currentHealth + 2) >= this.maxHealth){
            this.currentHealth = this.maxHealth;
        } else {
            this.currentHealth += 2;
        }
    }

    public void powerUpPotion(){
        //trying to figure out a timer to make the powerup only last for a limited time

        // Ashley - Added my own code to test with
        Potion powerUp = new Potion("Health Max Up", "Health", 1, "Increases Maximum health by 2", 2, 0, 0, 0);
        powerUp.maxHealth += 2;
        powerUp.currentHealth += 2;
    }

}