package com.game;

public class Player extends Character{
    public int attackDelay;

    public Player(){
        this.currentHealth = 15;
        this.maxHealth = 15;
        this.moveSpeed = 18; 
        this.power = 1;
        this.isDead = false;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }
    public int getPower() {
        return power;
    }
    public int getMoveSpeed() {
        return (int) moveSpeed;
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

    private boolean facingRight = true; // Default facing direction
    
    public boolean isFacingRight() {
        return facingRight;
    }
    
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
    
    public void heal(int amount) {
        if (amount <= 0) {
            System.out.println("Heal amount must be positive, IGNORED!");
            return;
        }
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }

    // Shorter using lambda expressions, saw this in a video
    public void applySpeedBoost(int boostAmount, int durationSeconds) {
        if (boostAmount <= 0 || durationSeconds <= 0) {
            System.out.println("Boost IGNORED! Invalid amount or duration.");
            return;
        }
        moveSpeed += boostAmount;

        // Threads do things in the background, 
        //aso we can use them to wait for a certain amount of time
        new Thread(() -> {
            try {
                Thread.sleep(durationSeconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveSpeed -= boostAmount;
        }).start();
    }
    
    public void applyDamageBoost(int boostAmount, int durationSeconds) {
        if (boostAmount <= 0 || durationSeconds <= 0) {
            System.out.println("Boost IGNORED! Invalid amount or duration.");
            return;
        }
        
        int originalPower = power; 
        power += boostAmount;
    
        new Thread(() -> {
            try {
                Thread.sleep(durationSeconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            power = originalPower;
            System.out.println("Damage boost ended. Power reverted to: " + power);
        }).start();
    }
}