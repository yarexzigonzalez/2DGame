package com.game; 
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

public class GameController {
    @FXML
    private Rectangle player; // matches element type in FXML
    @FXML
    private Button inventoryButton;
    @FXML
    private Label healthLabel;

    private double moveSpeed = 10;
    private int health = 3;

    @FXML
    public void initialize() {
        updateHealthLabel();
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                player.setY(player.getY() - 10);
                break;
            case DOWN:
                player.setY(player.getY() + 10);
                break;
            case LEFT:
                player.setX(player.getX() - 10);
                break;
            case RIGHT:
                player.setX(player.getX() + 10);
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