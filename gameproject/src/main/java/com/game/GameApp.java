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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameView.fxml"));
        Parent root = loader.load();
    
        int ViewWidth = 1900;
        int ViewHeight = 1080;
        Scene scene = new Scene(root, ViewWidth, ViewHeight); // window size
        stage.setScene(scene);
        stage.setTitle("2D Platformer");
        stage.show();

        root.requestFocus(); // Request focus for the root node to capture key events   
    }
    public static void main(String[] args) {
        launch();
    }

}