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
import javafx.scene.paint.Color;
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
    private ImageView player; // Matches element type in FXML
    @FXML
    private Button inventoryButton;
    @FXML
    private Label healthLabel;
    @FXML
    private Rectangle healthBar;
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
    private ImageView groundPlatform, groundPlatform2, groundPlatform3, groundPlatform4;
    @FXML
    private ImageView wall;
    @FXML 
    private ImageView floatingPlatform, floatingPlatform2, floatingPlatform3, floatingPlatform4, 
    floatingPlatform5, floatingPlatform6, floatingPlatform7, floatingPlatform8, floatingPlatform9, 
    floatingPlatform10, floatingPlatform11, floatingPlatform12, floatingPlatform13, floatingPlatform14,
    floatingPlatform15; 
    @FXML
    private Rectangle water;
   
    // Cooldown stuff
    private long lastSpikeDamageTime = 0; // Last time spikes were checked
    private final long spikeDamageCooldown = 1000_000_000L; // 1 second in nanoseconds
    @FXML
    private Label healLabel, speedLabel, damageLabel;

    private List<Potion> activePotions = new ArrayList<>(); 
    private List<ImageView> potionImages = new ArrayList<>(); 
    private List<ImageView> spikeImages = new ArrayList<>();
    private List<ImageView> platforms; // List to hold all platforms
    private List<ImageView> enemies = new ArrayList<>();
    private List<Enemy> enemyStats = new ArrayList<>(); 
    private List<Label> enemyHealthLabels = new ArrayList<>();
    private List<Rectangle> enemyHealthBars = new ArrayList<>();
    // Holds patrol x boundaries for each enemy to prevent them from chasing forever
    // aka from floating over water like before....
    private List<Double[]> enemyPatrolBounds = new ArrayList<>(); 
// ------------------------------------------------------------------------------------

    @FXML
    public void initialize() {
        // Player
        Image playerImage = new Image(getClass().getResourceAsStream("/com/game/player.PNG"));
        player.setImage(playerImage);
        player.setFitWidth(50);
        player.setFitHeight(50);

        // Delay until everything good and loaded
        Platform.runLater(() -> {
            WorldWidth = world.getWidth(); // Get actual width of the world
            System.out.println("World width: " + WorldWidth);
        });
        updateHealthLabel();
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
        );

        // Add potions dynamically
        Potion healthPotion = new HealthPotion("Health Potion", "/com/game/healthPotion.PNG", 3);
        Potion damagePotion = new DamagePotion("Damage Potion", "/com/game/damagePotion.PNG", 3, 5);
        Potion speedPotion = new SpeedPotion("Speed Potion", "/com/game/speedPotion.PNG", 2, 5);

        double healthPotionX = wall.getLayoutX() + 50; 
        double healthPotionY = wall.getLayoutY() - 55;
        double speedPotionX = floatingPlatform8.getLayoutX() + 50; 
        double speedPotionY = floatingPlatform8.getLayoutY() - 55;
        double damagePotionX = floatingPlatform3.getLayoutX() + 50; 
        double damagePotionY = floatingPlatform3.getLayoutY() - 55;

        addPotionToWorld(healthPotion, healthPotionX, healthPotionY);
        addPotionToWorld(damagePotion, damagePotionX, damagePotionY);
        addPotionToWorld(speedPotion, speedPotionX, speedPotionY);

        // Add spikes to the world
        // Far left 
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform5.getLayoutX(), floatingPlatform5.getLayoutY() - 60);
        // Far right 
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform5.getLayoutX() + floatingPlatform5.getFitWidth() - 50, floatingPlatform5.getLayoutY() - 60);
        // Middle of ground platform
        double middleGroundX = groundPlatform4.getLayoutX() + (groundPlatform.getFitWidth() / 2) - 40;
        addSpikeToWorld("/com/game/spikes.png", middleGroundX, groundPlatform.getLayoutY() - 45);
        // Floating platform 7
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform7.getLayoutX() + 20, floatingPlatform7.getLayoutY() - 60);
        // Edge of floatingPlatform12
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform12.getLayoutX(), floatingPlatform12.getLayoutY() - 60);
        // Edge of floatingPlatform11
        addSpikeToWorld("/com/game/onespike.png", floatingPlatform11.getLayoutX() + floatingPlatform11.getFitWidth() - 50, floatingPlatform11.getLayoutY() - 60);

        // Set enemy image
        spawnEnemies();

        // Loop animation (allows for smooth movement)
        AnimationTimer timer = new AnimationTimer() {
           @Override
           public void handle(long now) {
                applyGravity();
                /*  Every frame, runs applyGravity which check for 
                collisions and update player position */
                checkPotionCollision(); 
                checkWaterCollision();
                checkSpikeCollision(); 
                moveEnemies();
                checkPlayerEnemyCollisions();
           }
        };
        timer.start();
    }

// ------------------------------------------------------------------------------------

    private void moveEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            ImageView enemy = enemies.get(i);
            Enemy stats = enemyStats.get(i);
            Double[] patrol = enemyPatrolBounds.get(i);
    
            if (stats.isDead) continue;
    
            double playerX = player.getLayoutX();
            double enemyX = enemy.getLayoutX();
            double speed = stats.moveSpeed;
    
            if (Math.abs(playerX - enemyX) > speed) {
                // Player is to the left and within patrol range
                if (playerX < enemyX && enemyX - speed >= patrol[0]) {
                    enemy.setLayoutX(enemyX - speed);
                // Player is to the right and within patrol range
                } else if (playerX > enemyX && enemyX + speed <= patrol[1]) {
                    enemy.setLayoutX(enemyX + speed);
                }
            }
    
            // Update health bar and label to stay stuck to enemy
            enemyHealthBars.get(i).setX(enemy.getLayoutX());
            enemyHealthBars.get(i).setY(enemy.getLayoutY() - 15);
    
            enemyHealthLabels.get(i).setLayoutX(enemy.getLayoutX());
            enemyHealthLabels.get(i).setLayoutY(enemy.getLayoutY() - 30);
        }
    }

// ------------------------------------------------------------------------------------

    private void checkPlayerEnemyCollisions() {
        for (int i = 0; i < enemies.size(); i++) {
            ImageView enemy = enemies.get(i);
            Enemy stats = enemyStats.get(i);
    
            // If enemy is dead, hide them and their UI
            if (stats.isDead) {
                if (enemy.isVisible()) {
                    enemy.setVisible(false); // Remove enemy
                    enemyHealthLabels.get(i).setVisible(false); // Remove label
                    enemyHealthBars.get(i).setVisible(false);   // Remove health bar
                    System.out.println("Enemy " + i + " has died and disappeared.");
                }
                continue; // Dead enemies do nothing, skip rest for this enemy
            }
    
            if (playerStats.isDead) continue;
    
            if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                if (stats.canAttack()) {
                    playerStats.damaged(stats.power);
                    stats.attackedPlayer(); // enemy attack cooldown
                    updateHealthLabel();
                    if (playerStats.isDead) restartGame();
                    System.out.println("Enemy " + i + " hit player! Player HP: " + playerStats.currentHealth);
                }
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
    // FOR IMAGEVIEW USE getLayoutX/Y() for position and setLayoutX/Y() to move it
   private void applyGravity() {
        // Calculate where player will move next vertically (Y position)
        double nextY = player.getLayoutY() + velocityY;
        // Get bounds for next frame
        Bounds playerBounds = player.getBoundsInParent();

        // Loop through all platforms to check for collision
        for (ImageView platform : platforms) {
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
                player.setLayoutY(platformBounds.getMinY() - player.getBoundsInParent().getHeight());
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
            player.setLayoutY(nextY); // Update player Y position
    }
    
// ---------------------------------------------------------------------------

    @FXML 
    public void onKeyPressed(KeyEvent event) {
        double currentX = player.getLayoutX();
        double moveAmount = playerStats.moveSpeed; // Amount to move left/right
        // Updated options to use left/right arrow keys or A/D keys
        switch (event.getCode()) {
            case LEFT:
            case A:
                if (!isCollidingHorizontally(currentX - moveAmount)) {
                    player.setLayoutX(currentX - moveAmount);
                    updateCamera(); // Update camera position
                    playerStats.setFacingRight(false); 
                }
                break;
            case RIGHT:
            case D:
                if (!isCollidingHorizontally(currentX + moveAmount)) {
                    player.setLayoutX(currentX + moveAmount);
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
        double playerX = player.getLayoutX() + player.getBoundsInParent().getWidth() / 2;
        // How far to move the camera left/right to center player
        double cameraX = playerX - (ViewWidth / 2);
        
        // Clamp camera so it doesn't go out of bounds like the edges of the world
        cameraX = Math.max(0, Math.min(cameraX, WorldWidth - ViewWidth));
        // Move world pane left/right to follow player
        // So player moves right and world moves left
        world.setLayoutX(-cameraX);
    }

// --------------------------------------------------------------------------------

    // No visual atm still
    private void swingSword() {
        for (int i = 0; i < enemies.size(); i++) {
            ImageView enemy = enemies.get(i);
            Enemy stats = enemyStats.get(i);
    
            if (stats.isDead) continue; // skip if already deaad
    
            double playerX = player.getBoundsInParent().getMinX();
            double playerY = player.getBoundsInParent().getMinY();
            double enemyX = enemy.getBoundsInParent().getMinX();
            double enemyY = enemy.getBoundsInParent().getMinY();
            double enemyRight = enemy.getBoundsInParent().getMaxX();
            double range = 110;
    
            double playerRight = playerX + range;
            double playerLeft = playerX - range;

            // Depending on if player is facing left or right, checks
            // if enemy is n that attack direction 
            boolean inRange = playerStats.isFacingRight()
                ? enemyX <= playerRight && enemyRight >= playerX
                : enemyRight >= playerLeft && enemyX <= playerX;
            
            // Enemy is close horizontally and about same height
            if (inRange && Math.abs(playerY - enemyY) < 50) {
                int damage = playerStats.getPower();
                stats.takeDamage(damage);
                updateEnemyHealthLabel(i);
                System.out.println("Hit enemy " + i + "! HP now: " + stats.currentHealth);
                break; // only hit one enemy per swing
            }
        }
    }
    
    // Updates enemy's HP label/bar after taking damage
    private void updateEnemyHealthLabel(int i) {
        Enemy stats = enemyStats.get(i);
        enemyHealthLabels.get(i).setText("Enemy HP: " + stats.currentHealth + "/" + stats.maxHealth);
        double percent = (double) stats.currentHealth / stats.maxHealth;
        enemyHealthBars.get(i).setWidth(percent * 50);
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
    
        // Reset player
        player.setLayoutX(startingX);
        player.setLayoutY(startingY);
        playerStats.isDead = false;
        velocityY = 0; 
        playerStats.currentHealth = playerStats.maxHealth; 

        // Reset enemies
        for (int i = 0; i < enemies.size(); i++) {
            ImageView enemy = enemies.get(i);
            Enemy stats = enemyStats.get(i);
            Rectangle healthBar = enemyHealthBars.get(i);
            Label healthLabel = enemyHealthLabels.get(i);
            double maxWidth = 50; // Max width of health bar

            // Reset visibility and stats
            enemy.setVisible(true);
            stats.isDead = false;
            stats.currentHealth = stats.maxHealth;

            // Reset health bar visuals
            healthBar.setVisible(true);
            healthBar.setWidth((stats.currentHealth / (double) stats.maxHealth) * maxWidth);
            healthLabel.setVisible(true);
            healthLabel.setText("HP: " + stats.currentHealth);
        }
        
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

        // Reset health label
        updateHealthLabel();

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
        for (ImageView platform : platforms) {
            Bounds platformBounds = platform.getBoundsInParent();

            // Predict horizontal position
            double predictedMinX = futureBounds.getMinX() + (nextX - player.getLayoutX());
            double predictedMaxX = futureBounds.getMaxX() + (nextX - player.getLayoutX());
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

// ----------------------------------------------------------------------------------

    // Calls spawnEnemy() to create enemies at specific locations with patrol bounds!
    // Basically, enemies get placed and know where to walk (limit their movement)
    // Don't walk on water anymore/float
    private void spawnEnemies() {
    spawnEnemy(860, 940, 610, 1132); // X/Y + patrol min/max
    spawnEnemy(1433, 940, 1132, 1670);
    spawnEnemy(2730, 940, 2395, 3811);
    spawnEnemy(4627, 940, 4333, 5400);
    }
    // Enemy also spawned with image, health bar, and label    
    private void spawnEnemy(double x, double y, double patrolMinX, double patrolMaxX) {
        ImageView newEnemy = new ImageView(new Image("/com/game/basicEnemy.png"));
        newEnemy.setFitWidth(50);
        newEnemy.setFitHeight(50);
        newEnemy.setLayoutX(x);
        newEnemy.setLayoutY(y);

        Enemy stats = new Enemy();

        Label hpLabel = new Label("Enemy HP: " + stats.currentHealth + "/" + stats.maxHealth);
        hpLabel.setLayoutX(x);
        hpLabel.setLayoutY(y - 30);

        Rectangle hpBar = new Rectangle(50, 5, Color.RED); // red bar to pop out
        hpBar.setX(x);
        hpBar.setY(y - 15);

        world.getChildren().addAll(newEnemy, hpLabel, hpBar); // shows on gui

        enemies.add(newEnemy);
        enemyStats.add(stats);
        enemyHealthLabels.add(hpLabel);
        enemyHealthBars.add(hpBar);
        enemyPatrolBounds.add(new Double[]{patrolMinX, patrolMaxX}); // save patrol bounds
    }


}