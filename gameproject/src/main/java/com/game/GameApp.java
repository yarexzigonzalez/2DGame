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

    public static Scene scene;

   /*@Override
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
    }*/

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameView.fxml"));
        Parent root = loader.load();

        // NEW WINDOW SIZE
        int ViewWidth = 1200; //1200
        int ViewHeight = 750; //750

        // SCALE FACTORS
        double scaleX = ViewWidth / 1280.0; //1280  1900 was og
        double scaleY = ViewHeight / 1100.0; //1100 1080 was 
        root.setScaleX(scaleX);
        root.setScaleY(scaleY);

        // SHIFT VIEW UP TO REVEAL CROPPED BOTTOM
        root.setTranslateY(-120); // adjust (try -30, -70 etc.)
        root.setTranslateX(-40);

        // Setup Scene and Stage
        scene = new Scene(root, ViewWidth, ViewHeight); 
        stage.setScene(scene);
        stage.setTitle("2D Platformer");
        stage.setResizable(false);
        stage.show();

        root.requestFocus();

    }
    public static void main(String[] args) {
        launch();
    }

}