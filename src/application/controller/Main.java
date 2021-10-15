package application.controller;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static Game game;

    @Override
    public void start(Stage stage) throws Exception {
        game = new Game();
        game.start();

        stage.setScene(game.getScene());
        stage.show();
    }

    public static void endGame() {
        game.end();
    }
}
