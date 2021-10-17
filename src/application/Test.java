package application;

import application.controller.Game;
import application.controller.Main;
import application.model.logic.Board;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Test extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(Objects.requireNonNull(getClass().getResourceAsStream("/application/resources/google-credentials.json"))))
                .setDatabaseUrl("https://capture-c2db4-default-rtdb.asia-southeast1.firebasedatabase.app")
                .build();

        FirebaseApp.initializeApp(options);

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/application/view/game.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();

        Game game = loader.getController();
        game.start(new Board("player1", "player2"), true);
        stage.getScene().setOnKeyPressed(game.getAction());
    }
}
