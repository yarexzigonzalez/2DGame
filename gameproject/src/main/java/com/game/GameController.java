package com.game; 

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

public class GameController {
    private Player playerStats = new Player(); // Create a new player object     
    @FXML
    private Rectangle player; // Matches element type in FXML
    @FXML
    private Button inventoryButton;
    @FXML
    private Label healthLabel;
    @FXML
    private Rectangle healthBar;

    private boolean jumping = false;
    private double velocityY = 0;
    private final double gravity = 0.5; // Gravity strength can adjust
    private final double jumpStrength = -20; // Jump strength can adjust
    private final double groundLevel = 510; // Y position of the ground can adjust
    private final double maxFallSpeed = 5; // Maximum falling speed can adjust

    @FXML 
    private Rectangle floatingPlatform; // Matches element type in FXML
    @FXML
    private Rectangle enemy;
    // Using long for time-based cooldown (milliseconds)
    private long lastDamageTime = 0; // When enemy last damaged player
    private final long damageCooldown = 1000; // 1 second cooldown between hits
    
    private double enemySpeed = 1;
    private boolean movingRight = true; 
    
    @FXML
    public void initialize() {
        updateHealthLabel();

        // Loop animation (allows for smooth movement)
        AnimationTimer timer = new AnimationTimer() {
           @Override
           public void handle(long now) {
               applyGravity();
               /*  Every frame, runs applyGravity which check for 
               collisions and update player position */

                moveEnemy(); // Call the enemy movement function
                checkPlayerEnemyCollision(); // Check for player-enemy collision

           }
        };
        timer.start();
    }

    private void moveEnemy() {
        double playerX = player.getX();
        double enemyX = enemy.getX();
        double distance = Math.abs(playerX - enemyX);

        if (distance > enemySpeed) {
            // Move enemy toward player
            if (playerX < enemyX) {
                enemy.setX(enemyX - enemySpeed);
            } else if (playerX > enemyX) {
                enemy.setX(enemyX + enemySpeed);
            } // Let enemy move off screen for now since screen will follow later
        } else {
            // Snap enemy nect to playeer so collision works
            enemy.setX(playerX);
        }
    }


    private void checkPlayerEnemyCollision() {
        if (playerStats.isDead) {
            return; // No collision check if player is dead
        }
        // Get bounds for player and enemy
        Bounds playerBounds = player.getBoundsInParent();
        Bounds enemyBounds = enemy.getBoundsInParent();

        // Check for collision
        if (playerBounds.intersects(enemyBounds)) {
            long currentTime = System.currentTimeMillis();
            
            if (currentTime - lastDamageTime >= damageCooldown) {
                // Collision detected! Player takes damage
                playerStats.damaged(1); // Assuming enemy deals 1 damage
                // Will add something to show damage on screen later:
                // red falsh on player, health bar decrease, sound effect, etc....
                lastDamageTime = currentTime; // Update last damage time
                updateHealthLabel(); // Update health label after taking damage
                
                if (playerStats.isDead) {
                    System.out.println("Player is dead!");
                    // Handle player death later -> restart game, show game over screen
                }
                System.out.println("Enemy hit player! Player health is now: " + playerStats.currentHealth);
            
            }
        }
    }

    /* applyGravity() called every frame to add gravity by increasing vertical speed, 
    predicts where player will move next, 
    detects if player will land on platfrom, 
    places player to platform or ground if collision occurs
    (used Bounds (geometry rectangles) to make more accurate collision checks)
    */
    private void applyGravity() {
        // Calculate where player will move next vertically (Y position)
        double nextY = player.getY() + velocityY;
    
        // Get bounds for next frame
        Bounds playerBounds = player.getBoundsInParent();
        Bounds platformBounds = floatingPlatform.getBoundsInParent();
    
        // Check if player is horizontally within platform's width
        boolean horizontal = playerBounds.getMaxX() > platformBounds.getMinX() &&
                             playerBounds.getMinX() < platformBounds.getMaxX();
    
        // Predict next bottom Y position
        double nextBottom = playerBounds.getMaxY() + velocityY;
    
        /* Check vertical collision:
        - Only care if player is falling so if velocityY > 0
        - PLayer is above platform
        - But their next bottom will cross or land top of the platform 
        */
        boolean vertical = velocityY > 0 &&
                           playerBounds.getMaxY() <= platformBounds.getMinY() &&
                           nextBottom >= platformBounds.getMinY();
    
        if (horizontal && vertical) {
            // Collision detected! (player is about to land on platform)
            // Move player so they stand exactly on top of platform
            player.setY(platformBounds.getMinY() - player.getHeight());
            // Stop vertical movement
            velocityY = 0;
            jumping = false; // Reset jumping so player can jump again
            System.out.println("Landed on platform!");
        } else {
            // No platofrom collison so apply gravity as normal
            velocityY += gravity;
            if (velocityY > maxFallSpeed) {
                velocityY = maxFallSpeed;
            }
    
            player.setY(nextY);
        }
    
        // Check (in case of fall-through)
        if (player.getY() >= groundLevel) {
            // Lock player to ground
            player.setY(groundLevel);
            velocityY = 0;
            jumping = false;
        }
    }
    

    @FXML 
    public void onKeyPressed(KeyEvent event) {
        // Updated options to use left/right arrow keys or A/D keys
        switch (event.getCode()) {
            case LEFT:
            case A:
                player.setX(player.getX() - playerStats.moveSpeed);
                break;
            case RIGHT:
            case D:
                player.setX(player.getX() + playerStats.moveSpeed);
                break;
            case SPACE:
            case W:
            case UP:
                if (!jumping) {
                    jumping = true;
                    velocityY = jumpStrength;
                }
                break;
        }
    }

    @FXML
    private void openInventory() {
        // Logic to open the inventory
        System.out.println("Inventory opened, add GUI here."); 
    }

    private void updateHealthLabel() {
        healthLabel.setText("Health: " + playerStats.currentHealth + "/" + playerStats.maxHealth);
        // Update health bar width based on current health
        double healthPercentage = (double) playerStats.currentHealth / playerStats.maxHealth;
        healthBar.setWidth(healthPercentage * 200); // 200 is the max width of the health bar
    }

    
}