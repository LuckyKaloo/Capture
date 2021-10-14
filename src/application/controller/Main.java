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

    public void drawCanvas() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double padding = 10;

        double rowSpacing = (canvas.getHeight() - padding * 2) / Board.BOARD_HEIGHT;
        double colSpacing = (canvas.getWidth() - padding * 2) / Board.BOARD_WIDTH;

        gc.setStroke(Color.YELLOW);
        for (Bot bot: currentPlayer.getBots()) {
            gc.beginPath();
            for (Vertex vertex: bot.getVisitedVertices()) {
                gc.lineTo(vertex.getX() * colSpacing + padding, vertex.getY() * rowSpacing + padding);
            }
            gc.stroke();
            gc.closePath();
        }

        gc.setFill(Color.rgb(255, 100, 100));
        for (int col = 0; col < Board.BOARD_WIDTH; col++) {
            for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
                for (int i = 0; i < 4; i++) {
                    if (board.getTiles().get(row).get(col)[i].getControllingPlayer() == currentPlayer) {
                        double[] xCoords;
                        double[] yCoords;

                        if (i == 0) {
                            xCoords = new double[]{(col + 1) * colSpacing - padding, (col + 1) * colSpacing- padding, (col + 0.5) * colSpacing- padding};
                            yCoords = new double[]{row * rowSpacing- padding, (row + 1) * rowSpacing- padding, (row + 0.5) * rowSpacing- padding};
                        } else if (i == 1) {
                            xCoords = new double[]{col * colSpacing- padding, (col + 1) * colSpacing- padding, (col + 0.5) * colSpacing- padding};
                            yCoords = new double[]{row * rowSpacing - padding, row * rowSpacing- padding, (row + 0.5) * rowSpacing- padding};
                        } else if (i == 2) {
                            xCoords = new double[]{col * colSpacing - padding, col * colSpacing - padding, (col + 0.5) * colSpacing - padding};
                            yCoords = new double[]{row * rowSpacing - padding, (row + 1) * rowSpacing - padding, (row + 0.5) * rowSpacing - padding};
                        } else {
                            xCoords = new double[]{col * colSpacing - padding, (col + 1) * colSpacing - padding, (col + 0.5) * colSpacing - padding};
                            yCoords = new double[]{(row + 1) * rowSpacing - padding, (row + 1) * rowSpacing - padding, (row + 0.5) * rowSpacing - padding};
                        }

                        gc.fillPolygon(xCoords, yCoords, 3);
                    }
                }
            }
        }

        gc.setFill(Color.WHITE);
        int circleRadius = 2;
        for (int col = 0; col <= Board.BOARD_WIDTH; col++) {
            for (int row = 0; row <= Board.BOARD_HEIGHT; row++) {
                gc.fillOval(col * colSpacing - circleRadius + padding, row * rowSpacing - circleRadius + padding, circleRadius * 2, circleRadius * 2);
            }
        }

        gc.setFill(Color.GREEN);
        gc.fillOval(selectedBot.getX() * colSpacing - circleRadius + padding, selectedBot.getY() * rowSpacing - circleRadius + padding, circleRadius * 2, circleRadius * 2);

        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeOval((currentPlayer.getSource().getX() - Bot.MAX_DISTANCE) * colSpacing + padding, (currentPlayer.getSource().getY() - Bot.MAX_DISTANCE) * rowSpacing + padding,
                Bot.MAX_DISTANCE * colSpacing * 2, Bot.MAX_DISTANCE * rowSpacing * 2);
    }
}
