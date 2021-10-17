package application.controller;

import application.model.logic.Board;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javafx.application.Application;
import javafx.application.Platform;
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
        stage.setMaximized(true);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    public static void startGame(Board board, boolean isPlayer1) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/application/view/game.fxml"));
                Main.stage.getScene().setRoot(loader.load());

                Game game = loader.getController();
                game.start(board, isPlayer1);
                Main.stage.getScene().setOnKeyPressed(game.getAction());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void endGame() {
        game.end();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
