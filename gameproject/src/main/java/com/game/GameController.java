package com.game; 

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    @FXML
    private Rectangle enemyHealthBar; 
    @FXML
    private Pane world;
    @FXML
    private Group gameView;

    private HealthPotion potion; // Instance of HealthPotion
    private ImageView potionImage; // ImageView for potion

    private boolean jumping = false;
    private double velocityY = 0;
    private final double gravity = 0.5; // Gravity strength can adjust
    private final double jumpStrength = -15; // (suggested by ashley)
    private final double groundLevel = 510; // Y position of the ground can adjust
    // (Ashley's suggestion:
    // Ground level = 510
    // Water level = 560
    private final double maxFallSpeed = 15; // Maximum falling speed can adjust
    // (Ashley's suggestion)

    @FXML 
    private Rectangle floatingPlatform; // Matches element type in FXML
    @FXML
    private Rectangle orangePlatform; 
    @FXML
    private Rectangle greenPlatform;
    @FXML
    private Rectangle enemy;

    private Enemy enemyStats = new Enemy(); // Create a new enemy object
    @FXML 
    private Label enemyHealthLabel;

    //private boolean movingRight = true; forgot why i had this here, but it doesn't seem to be used

    private final List<Rectangle> platforms = new ArrayList<>(); // List to hold all platforms
    
// ------------------------------------------------------------------------------------

    @FXML
    public void initialize() {
        updateHealthLabel();
        updateEnemyHealthLabel(); // Update enemy health label
        
        platforms.add(floatingPlatform);
        platforms.add(orangePlatform);
        platforms.add(greenPlatform);

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

        // Loop animation (allows for smooth movement)
        AnimationTimer timer = new AnimationTimer() {
           @Override
           public void handle(long now) {
                applyGravity();
                /*  Every frame, runs applyGravity which check for 
                collisions and update player position */
                moveEnemy(); // Call the enemy movement function
                checkPlayerEnemyCollision(); // Check for player-enemy collision
                checkPotionCollision(); 

                // Show player health bar and label
                enemyHealthBar.setLayoutX(enemy.getLayoutX());
                enemyHealthBar.setLayoutY(enemy.getLayoutY() - 15);
                // Doesn't work with .setLayoutX/Y, for some reason
                // Used .setTranslateX/Y randomly and it worked, so I guess it works
                enemyHealthLabel.setTranslateX(enemy.getLayoutX());
                enemyHealthLabel.setTranslateY(enemy.getLayoutY() - 30);
                

           }
        };
        timer.start();
    }

// ------------------------------------------------------------------------------------

    private void moveEnemy() {
        double playerX = player.getX();
        double enemyX = enemy.getX();
        double distance = Math.abs(playerX - enemyX);
        double speed = enemyStats.moveSpeed; // Enemy speed

        if (distance > speed) {
            // Move enemy toward player
            if (playerX < enemyX) {
                enemy.setX(enemyX - speed);
            } else if (playerX > enemyX) {
                enemy.setX(enemyX + speed);
            } // Let enemy move off screen for now since screen will follow later
        } else {
            // Snap enemy nect to playeer so collision works
            enemy.setX(playerX);
        }

        // Update enemy health bar and label position so it follows enemy!
        double healthBarOffsetY = 15; 
        double labelOffsetY = 30; 

        // .set for shapes, .setLayout for labels/imageview/pane (note to self)
        enemyHealthBar.setX(enemy.getX());
        enemyHealthBar.setY(enemy.getY() - healthBarOffsetY);

        enemyHealthLabel.setLayoutX(enemy.getX());
        enemyHealthLabel.setLayoutY(enemy.getY() - labelOffsetY);
    }

// ------------------------------------------------------------------------------------

    private void checkPlayerEnemyCollision() {
        if (playerStats.isDead || enemyStats.isDead) {
            enemy.setVisible(false); // Hide enemy if dead
            enemyHealthLabel.setVisible(false); // Hide enemy health label if dead
            return; // No collision check if player or enemy is dead
        }
        // Get bounds for player and enemy
        Bounds playerBounds = player.getBoundsInParent();
        Bounds enemyBounds = enemy.getBoundsInParent();
        // Check for collision
        if (playerBounds.intersects(enemyBounds)) {
            if (enemyStats.canAttack()) {
                // Collision detected! Player takes damage
                playerStats.damaged(enemyStats.power); // Assuming enemy deals 1 damage
                // Will add something to show damage on screen later:
                // red flash on player, health bar decrease, sound effect, etc....
                enemyStats.attackedPlayer(); // Update last damage time
                updateHealthLabel(); // Update health label after taking damage
                
                if (playerStats.isDead) {
                    System.out.println("Player is dead!");
                    // Handle player death later -> restart game, show game over screen
                }
                System.out.println("Enemy hit player! Player health is now: " + playerStats.currentHealth);
            }
        }
    }

// ------------------------------------------------------------------------------------

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

        // Loop through all platforms to check for collision
        for (Rectangle platform : platforms) {
            Bounds platformBounds = platform.getBoundsInParent();
            boolean horizontal = playerBounds.getMaxX() > platformBounds.getMinX() &&
                                 playerBounds.getMinX() < platformBounds.getMaxX();
            // Predict next bottom Y position
            double nextBottom = playerBounds.getMaxY() + velocityY;
            // Check vertical collision
            boolean vertical = velocityY > 0 &&
                               playerBounds.getMaxY() <= platformBounds.getMinY() &&
                               nextBottom >= platformBounds.getMinY();
            if (horizontal && vertical) {
                // Collision detected! Move player to stand on platform
                player.setY(platformBounds.getMinY() - player.getHeight());
                velocityY = 0; // Stop vertical movement
                jumping = false; // Reset jumping so player can jump again
                return; // Exit the loop after collision
            }
        }   
            // No platform collision, apply gravity as normal
            velocityY += gravity;
            if (velocityY > maxFallSpeed) {
                velocityY = maxFallSpeed; // Cap fall speed
            }
            player.setY(nextY); // Update player Y position

            // Ground check
            if (player.getY() >= groundLevel) {
                // Lock player to ground
                player.setY(groundLevel);
                velocityY = 0; // Stop vertical movement
                jumping = false; // Reset jumping so player can jump again
            }
    }
    
// ---------------------------------------------------------------------------

    @FXML 
    public void onKeyPressed(KeyEvent event) {
        // Updated options to use left/right arrow keys or A/D keys
        switch (event.getCode()) {
            case LEFT:
            case A:
                player.setX(player.getX() - playerStats.moveSpeed);
                updateCamera(); // Update camera position
                playerStats.setFacingRight(false); 
                break;
            case RIGHT:
            case D:
                player.setX(player.getX() + playerStats.moveSpeed);
                updateCamera(); // Update camera position
                playerStats.setFacingRight(true); 
                break;
            case F:
                swingSword();
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

// --------------------------------------------------------------------------------
   
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

    private void updateEnemyHealthLabel() {
        enemyHealthLabel.setText("Enemy HP: " + enemyStats.currentHealth + "/" + enemyStats.maxHealth);
        double healthPercentage = (double) enemyStats.currentHealth / enemyStats.maxHealth;
        enemyHealthBar.setWidth(healthPercentage * 50);
    }

// --------------------------------------------------------------------------------

    /*
     - 'world' is pane that holds all game elements and stuff like player, platforms, etc.
     - 'gameView' is the main view that holds the world and other UI stuff
     - Bsically needed so we can move world inside gameView,
     but gameView stays still
     - Group is needed to hold world without affecting other UI stuff like the health bar
     */
    private final double ViewWidth = 800; // Width of the game view
    private final double WorldWidth = 2000; // Width of the level

    private void updateCamera() {
        /* 
        - Center the camera on the player as screen moves instead of player going off screen.
        - So bascially world "slides/scrolls" left/right to follow player
        - Works by shifting Pane that holds everything in the world
        */
        // Get the player's X position and center the camera on them
        double playerX = player.getX() + player.getWidth() / 2;
        // How far to move the camera left/right to center player
        double cameraX = playerX - (ViewWidth / 2);
        
        // Clamp camera so it doesn't go out of bounds like the edges of the world
        cameraX = Math.max(0, Math.min(cameraX, WorldWidth - ViewWidth));
        // Move world pane left/right to follow player
        // So player moves right and world moves left
        world.setLayoutX(-cameraX);
    }

// --------------------------------------------------------------------------------
    
    private void swingSword() {
        if (enemyStats.isDead) {
            return; // No attack if enemy is dead
        }
        // Get actuall positions of player and enemy
        double playerX = player.getBoundsInParent().getMinX();
        double playerY = player.getBoundsInParent().getMinY();
        double enemyX = enemy.getBoundsInParent().getMinX();
        double enemyY = enemy.getBoundsInParent().getMinY();

        double range = 110; // Attack range
        boolean enemyInRange = false;
        // Check if enemy is within attack range based on player's facing direction
        if (playerStats.isFacingRight()) {
            enemyInRange = enemyX > playerX &&
                           enemyX <= playerX + range;
        } else {
            enemyInRange = enemyX < playerX &&
                           enemyX >= playerX - range;
        }

        // Check if enemy is within vertical range (Y position)
        if (enemyInRange && Math.abs(playerY - enemyY) < 50) {
            enemyStats.takeDamage(1);
            updateEnemyHealthLabel(); 
            System.out.println("Enemy hit! Enemy health is now: " + enemyStats.currentHealth);

        } else {
            System.out.println("Missed! Enemy out of range.");
        }
    }
// --------------------------------------------------------------------------------

    private void checkPotionCollision() {
        if (potionImage != null && player.getBoundsInParent().intersects(potionImage.getBoundsInParent())) {
            world.getChildren().remove(potionImage);
            potionImage = null;
            potion.use();
        }
    }
}