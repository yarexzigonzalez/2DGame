package com.game;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends Character{
    public int currentHealth = 10;
    public int maxHealth = 10;
    public int moveSpeed = 30;
    public int power = 2;
    public int armor = 0;
    public boolean isDead = false;
    public int attackSpeed;

    Player(){
        this.currentHealth = 10;
        this.maxHealth = 10;
        this.moveSpeed = 30;
        this.power = 2;
        this.armor = 0;
        this.isDead = false;
        this.attackSpeed = 5; //not sure what to do with this yet
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
    }
}
