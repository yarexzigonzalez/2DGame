package com.game; 

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
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
    private ImageView keyImage;
    private boolean hasKey = false;
    private ImageView doorImage;
    private boolean doorOpened = false;
    private ImageView babyImage;
    private boolean babySpawned = false;
   
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
    private final double maxFallSpeed = 3; //(better for jumping "over" spikes)

    @FXML
    private ImageView groundPlatform, groundPlatform2, groundPlatform3, groundPlatform4;
    @FXML
    private ImageView wall1, wall2, wall4;
    @FXML 
    private ImageView floatingPlatform, floatingPlatform2, floatingPlatform3, floatingPlatform4, 
    floatingPlatform5, floatingPlatform6, floatingPlatform7, floatingPlatform8, floatingPlatform9, 
    floatingPlatform10, floatingPlatform11, floatingPlatform12, floatingPlatform13, floatingPlatform14,
    floatingPlatform15, floatingPlatform16, floatingPlatform17, floatingPlatform18, floatingPlatform19,
    floatingPlatform20, floatingPlatform21, floatingPlatform22, floatingPlatform23, floatingPlatform24,
    floatingPlatform25, floatingPlatform26; 
    @FXML
    private ImageView water;
   
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

    private long startTime;
    private long endTime;
    private long timeTaken; // milliseconds

    private boolean deathHandled = false;
    private boolean canAttack = true;
    private final int attackCooldownMillis = 500;
    private boolean keyDropped = false;
    private boolean gamePaused = true; // Game starts paused
    private boolean introShown = false;
    private boolean restartPromptShown = false;

// ------------------------------------------------------------------------------------

    @FXML
    public void initialize() {
        if (!introShown) {
            showIntroScreen();
            introShown = true;
        }

        // Player
        Image playerImage = new Image(getClass().getResourceAsStream("/com/game/player.PNG"));
        player.setImage(playerImage);
        player.setFitWidth(50);
        player.setFitHeight(50);

        // Door
        doorImage = new ImageView(new Image("/com/game/door.png"));
        doorImage.setFitWidth(60);
        doorImage.setFitHeight(80);
        doorImage.setLayoutX(7270); // replace with actual coords
        doorImage.setLayoutY(900);
        world.getChildren().add(doorImage);


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
            floatingPlatform14, wall1, wall2, wall4, floatingPlatform15, floatingPlatform16, 
            floatingPlatform17, floatingPlatform18, floatingPlatform19, floatingPlatform20,
            floatingPlatform21, floatingPlatform22, floatingPlatform23, floatingPlatform24, 
            floatingPlatform25, floatingPlatform26
        );

        // Add potions dynamically
        Potion healthPotion = new HealthPotion("Health Potion", "/com/game/healthPotion.PNG", 10);
        Potion damagePotion = new DamagePotion("Damage Potion", "/com/game/damagePotion.PNG", 3, 5);
        Potion speedPotion = new SpeedPotion("Speed Potion", "/com/game/speedPotion.PNG", 3, 5);
     
        // Near boss
        Potion dPotion2 = new DamagePotion("Damage Potion", "/com/game/damagePotion.PNG",12, 10);
        Potion hP2 = new HealthPotion("Health Potion", "/com/game/healthPotion.PNG", 15);
        addPotionToWorld(healthPotion, 1730, 528);
        addPotionToWorld(damagePotion, 685, 952);
        addPotionToWorld(speedPotion, 2988, 490);
        addPotionToWorld(dPotion2,5945, 949);
        addPotionToWorld(hP2, 5524, 81);
   
       
        // Add spikes to the world (x-23, y-31) L to R across game screen
        addSpikeToWorld("/com/game/onespike.png", 1685, 743); // far left
        addSpikeToWorld("/com/game/onespike.png", 2030,743 ); // far right
        addSpikeToWorld("/com/game/onespike.png", 1855, 623); // middle 
        addSpikeToWorld("/com/game/onespike.png", 2238, 451); // middle
        addSpikeToWorld("/com/game/onespike.png", 2506,623); //left
        addSpikeToWorld("/com/game/upsideDSpike.png", 2643, 495); // top
        addSpikeToWorld("/com/game/onespike.png", 2779, 623); // right
        addSpikeToWorld("/com/game/onespike.png", 3063, 491); // left
        addSpikeToWorld("/com/game/onespike.png", 3248, 491); // right
        addSpikeToWorld("/com/game/onespike.png", 3441, 380); // middle
        addSpikeToWorld("/com/game/onespike.png", 3941, 303); // middle
        addSpikeToWorld("/com/game/onespike.png", 4016, 708); // midlle
        addSpikeToWorld("/com/game/onespike.png", 4198, 815); // midlle
        addSpikeToWorld("/com/game/onespike.png", 4198, 221); // left
        addSpikeToWorld("/com/game/onespike.png", 4338, 221); // midlle
        addSpikeToWorld("/com/game/onespike.png", 4482, 221); // right
        addSpikeToWorld("/com/game/onespike.png", 4948, 438); // midlle
        addSpikeToWorld("/com/game/onespike.png", 5263, 698); // midlle
        addSpikeToWorld("/com/game/onespike.png", 5332, 208); // midlle
        addSpikeToWorld("/com/game/onespike.png", 5134, 74); // left
        addSpikeToWorld("/com/game/onespike.png", 5308, 74); // right

        // Set enemy image
        spawnEnemies();

        // Loop animation (allows for smooth movement)
        AnimationTimer timer = new AnimationTimer() {
           @Override
           public void handle(long now) {
                if (gamePaused) return;
                applyGravity();
                /*  Every frame, runs applyGravity which check for 
                collisions and update player position */
                checkPotionCollision(); 
                checkWaterCollision();
                checkSpikeCollision(); 
                moveEnemies();
                checkPlayerEnemyCollisions();
                checkKeyPickup();
                checkDoorUnlock();
                moveBaby();
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
        if (deathHandled) return;

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
                // If this is boss (only spawn key is boss dead and all others are too)
                if (isBossEnemy(i) && !keyDropped && areAllBasicEnemiesDead()) {
                    spawnKey(enemy.getLayoutX(), enemy.getLayoutY() - 80); 
                    // - (number) so it spawns "above" invisible enemy and not get hidden
                    keyDropped = true;
                } else if (isBossEnemy(i) && !keyDropped && !areAllBasicEnemiesDead() && !restartPromptShown) {
                    // Boss is dead but some enemies are still alive — show message
                    showRestartPrompt();
                    restartPromptShown = true; // so it doesn't repeat ocer and over
                }
                continue; // Dead enemies do nothing, skip rest for this enemy
            }

            if (playerStats.isDead) continue;
    
            if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                if (stats.canAttack()) {
                    playerStats.damaged(stats.power);
                    stats.attackedPlayer(); // enemy attack cooldown
                    updateHealthLabel();
                    if (playerStats.isDead) {
                        deathHandled = true; // To prevent repeat
                        if (!babySpawned) {
                            showDeathScreen(); // Died before completing level
                        } else {
                            restartGame(); // Completed level
                        }
                    }
                    
                    System.out.println("Enemy " + i + " hit player! Player HP: " + playerStats.currentHealth);
                }
            }
        }
    }

    private boolean isBossEnemy(int index) {
        return index == 4; // 5th enemy added
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
        double playerWidth = player.getBoundsInParent().getWidth();
        double minX = 0; // Left edge of the world
        double maxX = 7270 - playerWidth; // Right edge of world - player width

        // Updated options to use left/right arrow keys or A/D keys
        switch (event.getCode()) {
            case LEFT:
            case A:
                if (!isCollidingHorizontally(currentX - moveAmount)) {
                    double newX = currentX - moveAmount;
                    newX = Math.max(minX, newX); // clamp to min
                    player.setLayoutX(newX);
                    updateCamera(); // Update camera position
                    playerStats.setFacingRight(false); 
                }
                break;
            case RIGHT:
            case D:
                if (!isCollidingHorizontally(currentX + moveAmount)) {
                    double newX = currentX + moveAmount;
                    newX = Math.min(maxX, newX); // clamp to max
                    player.setLayoutX(newX);
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
        if (!canAttack) return;

        canAttack = false;

        // Cooldown timer 
        Timeline cooldownTimer = new Timeline(
            new KeyFrame(Duration.millis(attackCooldownMillis), e -> canAttack = true)
        );
        cooldownTimer.setCycleCount(1);
        cooldownTimer.play();

        for (int i = 0; i < enemies.size(); i++) {
            ImageView enemy = enemies.get(i);
            Enemy stats = enemyStats.get(i);

            if (stats.isDead) continue; // skip if already deaad

            double playerX = player.getBoundsInParent().getMinX();
            double playerY = player.getBoundsInParent().getMinY();
            double enemyX = enemy.getBoundsInParent().getMinX();
            double enemyY = enemy.getBoundsInParent().getMinY();
            double enemyRight = enemy.getBoundsInParent().getMaxX();
            double range = 115;

            double playerRight = playerX + range;
            double playerLeft = playerX - range;

            // Depending on if player is facing left or right, checks
            // MAKE SURE PLAYER FACES DIRECTION OF ENEMY WHEN ATTACKING
            // if enemy is n that attack direction 
            boolean inRange = playerStats.isFacingRight()
                ? enemyX <= playerRight && enemyRight >= playerX
                : enemyRight >= playerLeft && enemyX <= playerX;

            // Enemy is close horizontally and about same height
            if (inRange && Math.abs(playerY - enemyY) < 50) {
                int damage = playerStats.getPower();
                stats.takeDamage(damage);

                // Blood splatter effect instead of sword too lazy. So, green blood (not too graphic)
                ImageView blood = new ImageView(new Image(getClass().getResource("/com/game/blood.png").toExternalForm()));
                blood.setFitWidth(50); 
                blood.setFitHeight(50);
                blood.setLayoutX(enemy.getLayoutX()); // center near enemy
                blood.setLayoutY(enemy.getLayoutY());
                world.getChildren().add(blood); 

                // Clean up the blood after 500ms
                Timeline removeBlood = new Timeline(
                    new KeyFrame(Duration.millis(500), e -> world.getChildren().remove(blood))
                );
                removeBlood.setCycleCount(1);
                removeBlood.play();
                
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
        if (deathHandled) return;

        if (player.getBoundsInParent().intersects(water.getBoundsInParent())) {
            if (!playerStats.isDead) {
                playerStats.isDead = true;
                System.out.println("Player drowned in water! Game over!");
                if (!deathHandled) {
                    deathHandled = true;
                    if (!babySpawned) {
                        showDeathScreen(); // Died before completing level
                    } else {
                        restartGame(); // Completed level
                    }
                }
            }
        }
    }

// --------------------------------------------------------------------------------

    private void restartGame() {
        System.out.println("Restarting game...");

        deathHandled = false;

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

        // Reset key
        if (keyImage != null) {
            keyImage.setVisible(false);
            keyImage.setLayoutX(-100); // Move offscreen
            keyImage.setLayoutY(-100);
        }
        hasKey = false;

        // Reset Door
        if (doorImage != null) {
            // Go back to closed door
            doorImage.setImage(new Image("com/game/door.png"));
        }
        doorOpened = false;

        // Reset baby
        if (babyImage != null) {
            world.getChildren().remove(babyImage);
            babyImage = null;
        }
        babySpawned = false;

        // Reset health label
        updateHealthLabel();

        // Reset camera position
        world.setLayoutX(0); // Reset camera to start position

        AnchorPane rootPane = (AnchorPane) gameView.getParent();
        rootPane.requestFocus(); // So keyboard focus is brought back
        // Makes key events work again

        // Reset tiner 
        startTime = System.currentTimeMillis();
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
        spike.setFitWidth(40); 
        spike.setFitHeight(40); 
        spike.setLayoutX(x);
        spike.setLayoutY(y);
        world.getChildren().add(spike);
        spikeImages.add(spike);
    }
// --------------------------------------------------------------------------------

    private void checkSpikeCollision() {
        long now = System.nanoTime();
        if (deathHandled) return; // No double death

        if (now - lastSpikeDamageTime < spikeDamageCooldown) {
            return; // skip, still in cooldown
        }
        for (ImageView spike : spikeImages) {
            if (spike.isVisible() && player.getBoundsInParent().intersects(spike.getBoundsInParent())) {
                System.out.println("OUCH! Player hit spikes!");
                playerStats.damaged(1); // Assuming spikes deal 1 damage
                updateHealthLabel();

                lastSpikeDamageTime = now; // Update cooldown timer

                if (playerStats.isDead) {
                    System.out.println("Player is dead from spikes!");
                    deathHandled = true; // Needed to add to stop repeeat death 
                    if (!babySpawned) {
                        showDeathScreen(); // Died before completing level
                    } else {
                        restartGame(); // Completed level
                    }
                } else {
                    System.out.println("Player health is now: " + playerStats.getCurrentHealth());
                }
                break; 
            }
        } 
    }

// ----------------------------------------------------------------------------------

    // Calls spawnEnemy() to create enemies at specific locations with patrol bounds!
    // Basically, enemies get placed and know where to walk (limit their movement)
    // Don't walk on water anymore/float
    private void spawnEnemies() {
    spawnEnemy(860, 940, 610, 1032); // X/Y + patrol min/max
    spawnEnemy(1433, 940, 1238, 1662);
    spawnEnemy(2730, 623, 2395, 3811);
    spawnEnemy(4627, 940, 4333, 5400);
    spawnBoss(5700, 900, 5605, 7270);
    spawnEnemy(809, 594,617, 1015);
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

// --------------------------------------------------------------------------------------------------

    private void spawnBoss(double x, double y, double patrolMinX, double patrolMaxX) {
        ImageView boss = new ImageView(new Image("/com/game/boss.png"));
        boss.setFitWidth(100); // Bigger size!
        boss.setFitHeight(100);
        boss.setLayoutX(x);
        boss.setLayoutY(y);

        BossEnemy stats = new BossEnemy();

        Label hpLabel = new Label("Boss HP: " + stats.currentHealth + "/" + stats.maxHealth);
        hpLabel.setLayoutX(x);
        hpLabel.setLayoutY(y - 30);

        Rectangle hpBar = new Rectangle(100, 5, Color.DARKRED); // Longer bar
        hpBar.setX(x);
        hpBar.setY(y - 15);

        world.getChildren().addAll(boss, hpLabel, hpBar);

        enemies.add(boss);
        enemyStats.add(stats);
        enemyHealthLabels.add(hpLabel);
        enemyHealthBars.add(hpBar);
        enemyPatrolBounds.add(new Double[]{patrolMinX, patrolMaxX});
    }

// -----------------------------------------------------------------------------------------------

    private void spawnKey(double x, double y) {
        // PRevent dupes just in case--if key already exists
        if (keyImage != null && world.getChildren().contains(keyImage)) {
            world.getChildren().remove(keyImage);
        }

        hasKey = false;

        keyImage = new ImageView(new Image("/com/game/key.png"));
        keyImage.setFitWidth(30);
        keyImage.setFitHeight(30);
        keyImage.setLayoutX(x);
        keyImage.setLayoutY(y);
        keyImage.setVisible(true);
        keyImage.setOpacity(1.0);
        world.getChildren().add(keyImage);
        keyImage.toFront();
        System.out.println("Key dropped at: " + x + ", " + y);
        System.out.println("Key visibility: " + keyImage.isVisible());

    }

// ------------------------------------------------------------------------------------------------

    private void checkKeyPickup() {
        if (keyImage != null && keyImage.isVisible() && player.getBoundsInParent().intersects(keyImage.getBoundsInParent())) {
            hasKey = true;
            keyImage.setVisible(false);
            System.out.println("Key picked up!");
        }
    }

// ------------------------------------------------------------------------------------------------

    private void checkDoorUnlock() {
        if (hasKey && !doorOpened && player.getBoundsInParent().intersects(doorImage.getBoundsInParent())) {
            doorImage.setImage(new Image("/com/game/openDoor.png"));
            doorOpened = true;
            spawnBaby(doorImage.getLayoutX(), doorImage.getLayoutY() + 40);
            System.out.println("Door unlocked and baby spawned!");
        }
    }

// ------------------------------------------------------------------------------------------------

    private void spawnBaby(double x, double y) {
        babyImage = new ImageView(new Image("/com/game/baby.png"));
        babyImage.setFitWidth(40);
        babyImage.setFitHeight(40);
        babyImage.setLayoutX(x);
        babyImage.setLayoutY(y);
        world.getChildren().add(babyImage);
        babySpawned = true;

        // Stop timer cus game level complete
        endTime = System.currentTimeMillis();
        timeTaken = endTime - startTime; // in milliseconds

        // Show "BABY RESCUED!" label at the top center of the screen
        Label rescuedLabel = new Label("BABY RESCUED!");
        rescuedLabel.setStyle(
        "-fx-font-size: 36px; " +
        "-fx-font-family: 'Impact'; " +
        "-fx-text-fill: white;");
        rescuedLabel.setLayoutX(6282); 
        rescuedLabel.setLayoutY(424);  
        world.getChildren().add(rescuedLabel);

        // Wait 7 seconds then show end screen
        PauseTransition delay = new PauseTransition(Duration.seconds(7));
        // Using lambda shorthand to handle what happens when timer done
        // cleaner way to handle events basically
        delay.setOnFinished(e -> {
            world.getChildren().remove(rescuedLabel); 
            showLevelCompleteScreen(); 
        });
        delay.play();

    }

// ------------------------------------------------------------------------------------------------

    private void moveBaby() {
        if (!babySpawned || babyImage == null) return;

        double babyX = babyImage.getLayoutX();
        double playerX = player.getLayoutX();

        double speed = 0.7; // make it cute and slow

        if (Math.abs(playerX - babyX) > speed) {
            if (playerX < babyX) {
                babyImage.setLayoutX(babyX - speed);
            } else {
                babyImage.setLayoutX(babyX + speed);
            }
        }
    }

// ------------------------------------------------------------------------------------------------

    private void showLevelCompleteScreen() {
        AnchorPane rootPane = (AnchorPane) gameView.getParent();

        // Menu VBox
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        menu.setStyle("-fx-background-color: transparent;");

        Label completeLabel = new Label("Level 1 Complete!");
        completeLabel.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-font-family: 'Impact'; " +
            "-fx-text-fill: white;"
        );

        long seconds = timeTaken / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        Label timeLabel = new Label(String.format("Time: %02d:%02d", minutes, remainingSeconds));
        timeLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-family: 'Verdana'; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;");

        Button playAgainBtn = new Button("Play Again");
        Button exitBtn = new Button("Exit");

        StackPane overlay = new StackPane(menu);
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        // Handles "Play Again" button clicked using lambda syntax 
        playAgainBtn.setOnAction(e -> {
            rootPane.getChildren().remove(overlay); // remove whole overlay
            restartGame();
        });
        // Handles "Exit" button click using lambda syntax
        exitBtn.setOnAction(e -> {
            Platform.exit();
        });

        menu.getChildren().addAll(completeLabel, timeLabel, playAgainBtn, exitBtn);

        rootPane.getChildren().add(overlay);
        overlay.toFront(); 
    }

// -------------------------------------------------------------------------------------------------------
    
    // Pretty much same thing as showLevelCompleteScreen() but for player death
    private void showDeathScreen() {
        AnchorPane rootPane = (AnchorPane) gameView.getParent();

        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        menu.setStyle("-fx-background-color: transparent;");

        Label deathLabel = new Label("YOU DIED");
        deathLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: red; -fx-font-weight: bold;");

        // Time survived
        long timeTaken = System.currentTimeMillis() - startTime;
        long seconds = timeTaken / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        Label timeLabel = new Label(String.format("You Lasted: %02d:%02d", minutes, remainingSeconds));
        timeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        Button tryAgainBtn = new Button("Try Again");
        Button exitBtn = new Button("Exit");

        StackPane overlay = new StackPane(menu);
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        tryAgainBtn.setOnAction(e -> {
            rootPane.getChildren().remove(overlay); // Remove overlay
            restartGame();
        });

        exitBtn.setOnAction(e -> {
            Platform.exit();
        });

        menu.getChildren().addAll(deathLabel, timeLabel, tryAgainBtn, exitBtn);

        rootPane.getChildren().add(overlay);
        overlay.toFront();
    }
// ---------------------------------------------------------------------------------------------------------------

    // Pretty same thing again as other screen but only show up at very start 
    private void showIntroScreen() {
        AnchorPane rootPane = (AnchorPane) gameView.getParent();

        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        menu.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Level 1: Rescue the Baby");
        title.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-font-family: 'Impact'; " +
            "-fx-text-fill: white;"
        );
        
        Label objective = new Label("Objective:\n- Defeat ALL enemies\n- Kill the boss to acquire the key\n- Unlock the door to save the baby!");
        objective.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-family: 'Verdana'; " +
            "-fx-text-fill: white;"
        );
        objective.setWrapText(true);
        
        Label controls = new Label("Controls: A/D or Arrows to move | F to attack");
        controls.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-family: 'Verdana'; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;"
        );
        


        Button startBtn = new Button("Start Game");

        StackPane overlay = new StackPane(menu);
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        startBtn.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
            rootPane.requestFocus();
            gamePaused = false; // Unpause game loop
            startTime = System.currentTimeMillis(); // Start the game timer now
        });

        menu.getChildren().addAll(title, objective, controls, startBtn);
        rootPane.getChildren().add(overlay);
        overlay.toFront();
    }

// ---------------------------------------------------------------------------------------------------

    private boolean areAllBasicEnemiesDead() {
        for (int i = 0; i < enemies.size(); i++) {
            if (!isBossEnemy(i)) { // ignore boss since not basic enemy
                if (!enemyStats.get(i).isDead) {
                    return false; // found a regular enemy still alive, so not true
                }
            }
        }
        return true; // all basic enemies are dead
    }
// ---------------------------------------------------------------------------------------------------

    // another screen message pop up but only pops up if player forgets to kill all enemies
    private void showRestartPrompt() {
        AnchorPane rootPane = (AnchorPane) gameView.getParent();

        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setStyle("-fx-background-color: transparent;");

        Label warning = new Label("You must defeat ALL enemies before facing the boss!");
        warning.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-font-family: 'Impact'; " +
            "-fx-text-fill: white;");

        Button tryAgain = new Button("Try Again");
        Button exit = new Button("Exit");

        StackPane overlay = new StackPane(messageBox);
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        tryAgain.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
            restartGame();
        });
        
        exit.setOnAction(e -> Platform.exit());

        messageBox.getChildren().addAll(warning, tryAgain, exit);
        rootPane.getChildren().add(overlay);
        overlay.toFront();
    }

}