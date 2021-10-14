package application.controller;

import application.model.Board;
import application.model.Vertex;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    Board board = new Board();
    private Canvas canvas;
    private GraphicsContext gc;

    int playerX = 1;
    int playerY = 1;

    @Override
    public void start(Stage stage) throws Exception {
        canvas = new Canvas();
        canvas.setWidth(1000);
        canvas.setHeight(600);
        gc = canvas.getGraphicsContext2D();
        canvas.setOnMouseMoved(e -> {
            board.update();
            drawCanvas();
        });

        Pane pane = new Pane(canvas);
        Scene scene = new Scene(pane);

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN -> {
                    if (playerY < Board.BOARD_HEIGHT) {
                        playerY += 1;
                    }
                }
                case LEFT -> {
                    if (playerX > 0) {
                        playerX -= 1;
                    }
                }
                case UP -> {
                    if (playerY > 0) {
                        playerY -= 1;
                    }
                }
                case RIGHT -> {
                    if (playerX < Board.BOARD_WIDTH) {
                        playerX += 1;
                    }
                }
                case Q -> {
                    playerX -= 1;
                    playerY -= 1;
                }
                case E -> {
                    playerX += 1;
                    playerY -= 1;
                }
                case A -> {
                    playerX -= 1;
                    playerY += 1;
                }
                case D -> {
                    playerX += 1;
                    playerY += 1;
                }
            }
            board.getPlayer1().getVisitedVertices().add(board.getVertices().get(playerX).get(playerY));

            board.update();
            drawCanvas();
        });

        stage.setScene(scene);
        stage.show();
    }

    public void drawCanvas() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double padding = 10;

        double rowSpacing = (canvas.getHeight() - padding * 2) / Board.BOARD_HEIGHT;
        double colSpacing = (canvas.getWidth() - padding * 2) / Board.BOARD_WIDTH;

        gc.setStroke(Color.RED);
        gc.beginPath();
        for (Vertex vertex: board.getPlayer1().getVisitedVertices()) {
            gc.lineTo(vertex.getX() * colSpacing - padding, vertex.getY() * rowSpacing - padding);
        }
        gc.stroke();
        gc.closePath();

        gc.setFill(Color.rgb(255, 100, 100));
        for (int col = 0; col < Board.BOARD_WIDTH; col++) {
            for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
                for (int i = 0; i < 4; i++) {
                    if (board.getTiles().get(row).get(col).get(i).getControllingPlayer() == board.getPlayer1()) {
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
        for (int col = 0; col < Board.BOARD_WIDTH; col++) {
            for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
                gc.fillOval(col * colSpacing - circleRadius - padding, row * rowSpacing - circleRadius - padding, circleRadius * 2, circleRadius * 2);
            }
        }
    }
}
