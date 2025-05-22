package com.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class GameApp extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        /*Parent root = FXMLLoader.load(getClass().getResource("/com/game/GameView.fxml"));
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("2D Platformer Game");
        stage.setScene(scene);
        stage.show(); not this , try below works*/

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameView.fxml"));
        Parent root = loader.load();
    
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("2D Platformer");
        stage.show();

        root.requestFocus(); // Request focus for the root node to capture key events   
    }
    public static void main(String[] args) {
        launch();
    }

}
