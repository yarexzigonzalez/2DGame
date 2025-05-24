package com.game;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class GameController {
    @FXML
    private Rectangle player; // Matches element type in FXML
    @FXML
    private Button inventoryButton;
    @FXML
    private Label healthLabel;
    @FXML
    private Pane world;

    private double moveSpeed = 30; // Adjust however you like
    private int health = 3;

    private ImageView potionImage;
    private HealthPotion potion;

    private boolean jumping = false;
    private double velocityY = 0;
    private final double gravity = 0.5; // Gravity strength can adjust
    private final double jumpStrength = -20; // Jump strength can adjust
    private final double groundLevel = 510; // Y position of the ground can adjust
    private final double maxFallSpeed = 5; // Maximum falling speed can adjust

    private void checkPotionCollision() {
    if (potionImage != null && player.getBoundsInParent().intersects(potionImage.getBoundsInParent())) {
        world.getChildren().remove(potionImage);
        potionImage = null;
        potion.use();
    }
}

    @FXML 
    private Rectangle floatingPlatform; // Matches element type in FXML

@FXML
public void initialize() {
    updateHealthLabel();

    // Create potion instance
    potion = new HealthPotion("Health Potion", getClass().getResource("/com/game/healthPotion.PNG").toExternalForm(), 10);

    // Create ImageView for potion
    potionImage = new ImageView(new Image(potion.getImagePath()));
    potionImage.setFitWidth(32);
    potionImage.setFitHeight(32);
    potionImage.setLayoutX(400); // Position in world coordinates
    potionImage.setLayoutY(510);

    // Add potion to world pane
    world.getChildren().add(potionImage);

    // Single AnimationTimer for both gravity and collision detection
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            applyGravity();
            checkPotionCollision();
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