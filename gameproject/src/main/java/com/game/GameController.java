package com.game;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

public class GameController {
    @FXML
    private Rectangle player; // Matches element type in FXML
    @FXML
    private Button inventoryButton;
    @FXML
    private Label healthLabel;

    private double moveSpeed = 30; // Adjust however you like
    private int health = 3;

    private boolean jumping = false;
    private double velocityY = 0;
    private final double gravity = 0.5; // Gravity strength can adjust
    private final double jumpStrength = -20; // Jump strength can adjust
    private final double groundLevel = 510; // Y position of the ground can adjust
    private final double maxFallSpeed = 5; // Maximum falling speed can adjust

    @FXML 
    private Rectangle floatingPlatform; // Matches element type in FXML

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
           }
        };
        timer.start();
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
                player.setX(player.getX() - moveSpeed);
                break;
            case RIGHT:
            case D:
                player.setX(player.getX() + moveSpeed);
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
        healthLabel.setText("Health: " + health);
    }

    
}