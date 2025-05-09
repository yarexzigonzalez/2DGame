package com.game; 

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
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

    
    private void applyGravity() {
        velocityY += gravity; // Apply gravity to the vertical velocity

        // Limit the maximum falling speed
        if (velocityY > maxFallSpeed) {
            velocityY = maxFallSpeed; // Cap the falling speed
        }

        // Update player position
        player.setY(player.getY() + velocityY);


        // Floor collision detection for player to not fall through the ground
        // Works fine
        if (player.getY() >= groundLevel) {
            player.setY(groundLevel);
            velocityY = 0;
            jumping = false;
        }

        // Platform collision detection (struggling with this)
        // TRYING THIS NOT GOING GOOD
       
        // Platform collision detection
        double nextPlayerBottom = player.getY() + player.getHeight() + velocityY;
        double platformTop = floatingPlatform.getLayoutY(); // Changed to getLayoutY() to get the Y position of the platform

        boolean horizontallyAligned = player.getX() + player.getWidth() > floatingPlatform.getLayoutX() && player.getX() < floatingPlatform.getLayoutX() + floatingPlatform.getWidth();
        
        // Fix for vertically aligned check
        boolean verticallyAligned = nextPlayerBottom > platformTop && player.getY() + player.getHeight() <= platformTop;

        // Check if falling and aligned with the platform
        if (velocityY > 0 && horizontallyAligned && verticallyAligned) {
            // Player lands on the platform
            player.setY(platformTop - player.getHeight()); // Land clean
            velocityY = 0;
            jumping = false;
            System.out.println("Player landed on the platform.");
        }

        // Debugging: Check player and platform positions
        System.out.println("Player Y: " + player.getY() + ", Platform Y: " + platformTop);
        System.out.println("Horizontally aligned: " + horizontallyAligned);
        System.out.println("Vertically aligned: " + verticallyAligned);
        System.out.println(floatingPlatform.getBoundsInParent());



        // WHAT I TRIED BEFORE not working either
        /*if (velocityY > 0 && player.getBoundsInParent().intersects(floatingPlatform.getBoundsInParent())) {
            // Player is falling and intersects with the platform
            double playerBottom = player.getY() + player.getHeight();
            double platformTop = floatingPlatform.getY();

            // If player bottom is bout to pass platform stop them
            if (playerBottom <= platformTop + velocityY) {
                player.setY(platformTop - player.getHeight()); // Set player on top of the platform
                velocityY = 0; // Stop falling
                jumping = false; // No longer jumping
                // For debugging
                System.out.println("Player landed on the platform.");
            }
        }*/   
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