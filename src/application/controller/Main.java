package application.controller;

import application.model.logic.Board;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    private static Game game;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Main.stage = stage;

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(Objects.requireNonNull(getClass().getResourceAsStream("/application/resources/google-credentials.json"))))
                .setDatabaseUrl("https://capture-c2db4-default-rtdb.asia-southeast1.firebasedatabase.app")
                .build();

        FirebaseApp.initializeApp(options);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/login.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    public static void newLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/application/view/login.fxml"));
            Stage newStage = new Stage();
            newStage.setScene(new Scene(loader.load()));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startGame(Board board, boolean isPlayer1) {
        game = new Game(board, isPlayer1);
        game.start();

        Main.stage.setScene(game.getScene());
        Main.stage.show();
    }

    public static void endGame() {
        game.end();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
