package application.controller;

import application.model.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Game {
    private final Board board = new Board();
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;

    private Player currentPlayer = board.getPlayer1();
    private Bot selectedBot = currentPlayer.getBots().get(0);

    private boolean moveMade = false;

    void start() {
        canvas = new Canvas();
        canvas.setWidth(1000);
        canvas.setHeight(600);
        gc = canvas.getGraphicsContext2D();

        scene = new Scene(new Pane(canvas));

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case T -> {
                    selectedBot = currentPlayer.getBots().get(0);
                }
                case Y -> {
                    selectedBot = currentPlayer.getBots().get(1);
                }
                case DOWN -> {
                    if (selectedBot.getY() < Board.BOARD_HEIGHT) {
                        moveMade = selectedBot.move(0, 1);
                    }
                }
                case LEFT -> {
                    if (selectedBot.getX() > 0) {
                        moveMade = selectedBot.move(-1, 0);
                    }
                }
                case UP -> {
                    if (selectedBot.getY() > 0) {
                        moveMade = selectedBot.move(0, -1);
                    }
                }
                case RIGHT -> {
                    if (selectedBot.getX() < Board.BOARD_WIDTH) {
                        moveMade = selectedBot.move(1, 0);
                    }
                }
                case Q -> {
                    if (selectedBot.getX() > 0  &&  selectedBot.getY() > 0) {
                        moveMade = selectedBot.move(-1, -1);
                    }
                }
                case E -> {
                    if (selectedBot.getX() < Board.BOARD_WIDTH  &&  selectedBot.getY() > 0) {
                        moveMade = selectedBot.move(1, -1);
                    }
                }
                case A -> {
                    if (selectedBot.getX() > 0  &&  selectedBot.getY() < Board.BOARD_HEIGHT) {
                        moveMade = selectedBot.move(-1, 1);
                    }
                }
                case D -> {
                    if (selectedBot.getX() < Board.BOARD_WIDTH  &&  selectedBot.getY() < Board.BOARD_HEIGHT) {
                        moveMade = selectedBot.move(1, 1);
                    }
                }
            }

            if (moveMade) {
                board.update();
                Move move = new Move(board);
                moveMade = false;
            }

//            currentPlayer = board.changePlayer(currentPlayer);
//            selectedBot = currentPlayer.getBots().get(0);

            drawCanvas();
        });

        drawCanvas();
    }

    void end() {

    }

    public Scene getScene() {
        return scene;
    }

    private void drawCanvas() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double padding = 20;

        double rowSpacing = 50;
        double colSpacing = 50;

        gc.setStroke(Color.YELLOW);
        for (Bot bot: currentPlayer.getBots()) {
            gc.beginPath();
            for (Vertex vertex: bot.getVisitedVertices()) {
                gc.lineTo(vertex.X() * colSpacing + padding, vertex.Y() * rowSpacing + padding);
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
                            xCoords = new double[]{(col + 1) * colSpacing + padding, (col + 1) * colSpacing + padding, (col + 0.5) * colSpacing + padding};
                            yCoords = new double[]{row * rowSpacing + padding, (row + 1) * rowSpacing + padding, (row + 0.5) * rowSpacing + padding};
                        } else if (i == 1) {
                            xCoords = new double[]{col * colSpacing + padding, (col + 1) * colSpacing + padding, (col + 0.5) * colSpacing + padding};
                            yCoords = new double[]{row * rowSpacing + padding, row * rowSpacing + padding, (row + 0.5) * rowSpacing + padding};
                        } else if (i == 2) {
                            xCoords = new double[]{col * colSpacing + padding, col * colSpacing + padding, (col + 0.5) * colSpacing + padding};
                            yCoords = new double[]{row * rowSpacing + padding, (row + 1) * rowSpacing + padding, (row + 0.5) * rowSpacing + padding};
                        } else {
                            xCoords = new double[]{col * colSpacing + padding, (col + 1) * colSpacing + padding, (col + 0.5) * colSpacing + padding};
                            yCoords = new double[]{(row + 1) * rowSpacing + padding, (row + 1) * rowSpacing + padding, (row + 0.5) * rowSpacing + padding};
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
        gc.strokeOval((currentPlayer.getSource().X() - Bot.MAX_DISTANCE) * colSpacing + padding, (currentPlayer.getSource().Y() - Bot.MAX_DISTANCE) * rowSpacing + padding,
                Bot.MAX_DISTANCE * colSpacing * 2, Bot.MAX_DISTANCE * rowSpacing * 2);
    }
}
