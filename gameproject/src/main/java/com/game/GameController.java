package com.game; 

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

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
    private double ViewWidth = 800; // Width of the game view
    private double WorldWidth = 0; // Width of the level/Dynamically use actual width

    private boolean jumping = false;
    private double velocityY = 0;
    private final double gravity = 0.5; // Gravity strength can adjust
    private final double jumpStrength = -15; // (suggested by ashley)
    private final double maxFallSpeed = 15; // Maximum falling speed can adjust
    // (Ashley's suggestion)
    @FXML
    private Rectangle groundPlatform, groundPlatform2, groundPlatform3, groundPlatform4;
    @FXML
    private Rectangle wall;
    @FXML 
    private Rectangle floatingPlatform, floatingPlatform2, floatingPlatform3, floatingPlatform4, 
    floatingPlatform5, floatingPlatform6, floatingPlatform7, floatingPlatform8, floatingPlatform9, 
    floatingPlatform10, floatingPlatform11, floatingPlatform12, floatingPlatform13, floatingPlatform14,
    floatingPlatform15; 
    @FXML
    private Rectangle water;
    @FXML
    private Rectangle enemy;
    // Cooldown stuff
    private long lastSpikeDamageTime = 0; // Last time spikes were checked
    private final long spikeDamageCooldown = 1000_000_000L; // 1 second in nanoseconds

    private Enemy enemyStats = new Enemy(); // Create a new enemy object
    @FXML
    private Label healLabel, speedLabel, damageLabel;
    @FXML 
    private Label enemyHealthLabel;
    private List<Potion> activePotions = new ArrayList<>(); 
    private List<ImageView> potionImages = new ArrayList<>(); 
    private List<ImageView> spikeImages = new ArrayList<>();
    private List<Rectangle> platforms; // List to hold all platforms
// ------------------------------------------------------------------------------------

    @FXML
    public void initialize() {
        // Delay until everything good and loaded
        Platform.runLater(() -> {
            WorldWidth = world.getWidth(); // Get actual width of the world
            System.out.println("World width: " + WorldWidth);
        });
        updateHealthLabel();
        updateEnemyHealthLabel();
        // Hide labels initially
        healLabel.setVisible(false); 
        speedLabel.setVisible(false);
        damageLabel.setVisible(false); 

        // More efficient way to initialize platforms?
        platforms = Arrays.asList(
            groundPlatform, groundPlatform2, groundPlatform3, groundPlatform4, floatingPlatform,
            floatingPlatform2, floatingPlatform3, floatingPlatform4, floatingPlatform5,
            floatingPlatform6, floatingPlatform7, floatingPlatform8, floatingPlatform9,
            floatingPlatform10, floatingPlatform11, floatingPlatform12, floatingPlatform13,
            floatingPlatform14, wall, floatingPlatform15
            /*orangePlatform, greenPlatform,*/
            
            // add more
        );

        // Add potions dynamically
        Potion healthPotion = new HealthPotion("Health Potion", "/com/game/healthPotion.PNG", 3);
        Potion damagePotion = new DamagePotion("Damage Potion", "/com/game/damagePotion.PNG", 3, 5);
        Potion speedPotion = new SpeedPotion("Speed Potion", "/com/game/speedPotion.PNG", 2, 5);

        double healthPotionX = floatingPlatform2.getLayoutX() + 50; 
        double healthPotionY = floatingPlatform2.getLayoutY() - 55;
        double speedPotionX = floatingPlatform3.getLayoutX() + 50; 
        double speedPotionY = floatingPlatform3.getLayoutY() - 55;
        double damagePotionX = floatingPlatform.getLayoutX() + 50; 
        double damagePotionY = floatingPlatform.getLayoutY() - 55;

        addPotionToWorld(healthPotion, healthPotionX, healthPotionY);
        addPotionToWorld(damagePotion, damagePotionX, damagePotionY);
        addPotionToWorld(speedPotion, speedPotionX, speedPotionY);

        // Add spikes to the world
        // Far left 
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform5.getLayoutX(), floatingPlatform5.getLayoutY() - 60);
        // Far right 
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform5.getLayoutX() + floatingPlatform5.getWidth() - 50, floatingPlatform5.getLayoutY() - 60);
        // Middle of ground platform
        double middleGroundX = groundPlatform4.getLayoutX() + (groundPlatform.getWidth() / 2) - 40;
        addSpikeToWorld("/com/game/spikes.png", middleGroundX, groundPlatform.getLayoutY() - 45);
        // Floating platform 7
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform7.getLayoutX() + 20, floatingPlatform7.getLayoutY() - 60);
        // Edge of floatingPlatform12
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform12.getLayoutX(), floatingPlatform12.getLayoutY() - 60);
        // Edge of floatingPlatform11
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform11.getLayoutX() + floatingPlatform11.getWidth() - 50, floatingPlatform11.getLayoutY() - 60);


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
                checkWaterCollision();
                checkSpikeCollision(); 

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
                    restartGame();
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
    }
    
// ---------------------------------------------------------------------------

    @FXML 
    public void onKeyPressed(KeyEvent event) {
        double currentX = player.getX();
        double moveAmount = playerStats.moveSpeed; // Amount to move left/right
        // Updated options to use left/right arrow keys or A/D keys
        switch (event.getCode()) {
            case LEFT:
            case A:
                if (!isCollidingHorizontally(currentX - moveAmount)) {
                    player.setX(currentX - moveAmount);
                    updateCamera(); // Update camera position
                    playerStats.setFacingRight(false); 
                }
                break;
            case RIGHT:
            case D:
                if (!isCollidingHorizontally(currentX + moveAmount)) {
                    player.setX(currentX + moveAmount);
                    updateCamera(); // Update camera position
                    playerStats.setFacingRight(true); 
                }
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
   
    /* Leave out for now
    @FXML
    private void openInventory() {
        // Logic to open the inventory
        System.out.println("Inventory opened, add GUI here."); 
    }*/

    private void updateHealthLabel() {
        healthLabel.setText("Health: " + playerStats.currentHealth + "/" + playerStats.maxHealth);
        // Update health bar width based on current health
        double healthPercentage = (double) playerStats.currentHealth / playerStats.maxHealth;
        healthBar.setWidth(healthPercentage * 200); // 200 is the max width of the health bar
    }

    private void updateEnemyHealthLabel() {
        enemyHealthLabel.setVisible(true); 
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
    private void updateCamera() {
        /* 
        - Center the camera on the player as screen moves instead of player going off screen.
        - So bascially world "slides/scrolls" left/right to follow player
        - Works by shifting Pane that holds everything in the world
        */
        if (WorldWidth <= 0) {
            return; // If world width is not set yet, do nothing
        }
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
            int damage = playerStats.getPower(); // Get player's attack power
            enemyStats.takeDamage(damage);
            updateEnemyHealthLabel(); 
            System.out.println("Enemy hit! Enemy health is now: " + enemyStats.currentHealth);

        } else {
            System.out.println("Missed! Enemy out of range.");
        }
    }

// --------------------------------------------------------------------------------

    private void checkPotionCollision() {
        for (int i = 0; i < potionImages.size(); i++) {
            ImageView img = potionImages.get(i);
            Potion potion = activePotions.get(i);
            if (img.isVisible() && player.getBoundsInParent().intersects(img.getBoundsInParent())) {
                img.setVisible(false);
                potion.use(playerStats, this); // pass GameController for GUI updates
                updateHealthLabel(); 

                System.out.println("Potion used: " + potion.getName());
                System.out.println("Player HP: " + playerStats.getCurrentHealth());
                System.out.println("Player Power: " + playerStats.getPower());
                System.out.println("Player Speed: " + playerStats.getMoveSpeed());
            }
        }
    }
    
// --------------------------------------------------------------------------------

    private void checkWaterCollision() {
        if (player.getBoundsInParent().intersects(water.getBoundsInParent())) {
            if (!playerStats.isDead) {
                playerStats.isDead = true;
                System.out.println("Player drowned in water! Game over!");
                restartGame();
            }
            
        }
    }

// --------------------------------------------------------------------------------

    private void restartGame() {
        System.out.println("Restarting game...");

        // Player x and y positions
        double startingX = 100; // Starting X position of player
        double startingY = 850; // Starting Y position of player
        // Enemy x and y positions
        double enemyStartingX = 200; // Starting X position of enemy
        double enemyStartingY = 937; // Starting Y position of enemy

        // Reset player
        player.setX(startingX);
        player.setY(startingY);
        playerStats.isDead = false;
        velocityY = 0; 
        playerStats.currentHealth = playerStats.maxHealth; 

        // Reset enemy
        enemy.setVisible(true); 
        enemy.setLayoutX(enemyStartingX);
        enemy.setLayoutY(enemyStartingY);
        enemyStats.isDead = false;
        enemyStats.currentHealth = enemyStats.maxHealth; // Reset enemy health
        
        // Reset player stats
        playerStats.power = 1; 
        playerStats.moveSpeed = 20;

        // Reset potion
        for (int i = 0; i < potionImages.size(); i++) {
            ImageView potionImage = potionImages.get(i);
            Potion potion = activePotions.get(i);
            
            potionImage.setVisible(true);
            potionImage.setLayoutX(potion.getSpawnX());
            potionImage.setLayoutY(potion.getSpawnY());
        }
        

        // Reset health labels
        updateHealthLabel();
        updateEnemyHealthLabel();

        // Reset camera position
        world.setLayoutX(0); // Reset camera to start position

        // Reset tiner 
        // Maybe add timer to game to track time played or something (later)
        //startTime = System.nanoTime();
        //gameTimer.start();
    }

// --------------------------------------------------------------------------------

    private void addPotionToWorld(Potion potion, double x, double y) {
        ImageView imageView = new ImageView(new Image(getClass().getResource(potion.getImagePath()).toExternalForm()));
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);

        // Save potion spawn position
        potion.setSpawnPosition(x, y);
    
        activePotions.add(potion);
        potionImages.add(imageView);
        world.getChildren().add(imageView);
    }

// --------------------------------------------------------------------------------
    public void showBoostMessage(String type, String text, int durationSeconds) {
        // Pick label based on boost type (heal, speed, damage)
        Label labelToUse = switch (type) {
            // FYI: '->' in switch is Java 14+ syntax for cleaner case expressions
            case "heal" -> healLabel;
            case "speed" -> speedLabel;
            case "damage" -> damageLabel;
            default -> null;
        };

        if (labelToUse != null) {
            labelToUse.setText(text); // Show message
            labelToUse.setVisible(true);
            // Short pause then hide message
            PauseTransition pt = new PauseTransition(Duration.seconds(durationSeconds));
            pt.setOnFinished(e -> {
                labelToUse.setText(""); // clear text after duration
                labelToUse.setVisible(false); // hide label again
            });
            pt.play(); // start timer
        }
    }
// --------------------------------------------------------------------------------

    private boolean isCollidingHorizontally(double nextX) {
        Bounds futureBounds = player.getBoundsInParent();

        // Loops through all platforms to check for side (left/right) collisions 
        // — works for walls
        for (Rectangle platform : platforms) {
            Bounds platformBounds = platform.getBoundsInParent();

            // Predict horizontal position
            double predictedMinX = futureBounds.getMinX() + (nextX - player.getX());
            double predictedMaxX = futureBounds.getMaxX() + (nextX - player.getX());
            // Check if player and wall are on same vertical level
            boolean verticalOverlap = futureBounds.getMaxY() > platformBounds.getMinY() &&
                                    futureBounds.getMinY() < platformBounds.getMaxY();
            // Check if player will overlap/hit wall from side
            boolean horizontalOverlap = predictedMaxX > platformBounds.getMinX() &&
                                        predictedMinX < platformBounds.getMaxX();
            // If both overlap, its a collision, so block movement
            if (verticalOverlap && horizontalOverlap) {
                return true;
            }
        }
        return false;
    }
// --------------------------------------------------------------------------------

    private void addSpikeToWorld(String imagePath, double x, double y) {
        ImageView spike = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        spike.setFitWidth(60); 
        spike.setFitHeight(60); 
        spike.setLayoutX(x);
        spike.setLayoutY(y);
        world.getChildren().add(spike);
        spikeImages.add(spike);
    }
// --------------------------------------------------------------------------------
    private void checkSpikeCollision() {
        long now = System.nanoTime();
        if (now - lastSpikeDamageTime < spikeDamageCooldown) {
            return; // skip, still in cooldown
        }
        for (ImageView spike : spikeImages) {
            if (spike.isVisible() && player.getBoundsInParent().intersects(spike.getBoundsInParent())) {
                System.out.println("OUCH! Player hit spikes!");
                playerStats.damaged(1); // Assuming spikes deal 1 damage
                updateHealthLabel();
                if (playerStats.isDead) {
                    System.out.println("Player is dead from spikes!");
                    restartGame(); 
                } else {
                    System.out.println("Player health is now: " + playerStats.getCurrentHealth());
                }

                lastSpikeDamageTime = now; // Update last damage time
                break; 
            }
        } 
    }


}