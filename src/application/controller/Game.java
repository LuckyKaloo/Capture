package application.controller;

import application.model.logic.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Game {
    @FXML
    private Canvas canvas;
    @FXML
    private Label won;
    @FXML
    private AnchorPane information;


    private GraphicsContext gc;
    private String gameName;
    private Logic logic;


    private final static int SPACING = 60;
    private final static int PADDING = 20;
    private final static int CIRCLE_RADIUS = 3;

    private final static Color BACKGROUND = Color.rgb(2, 8, 16);
    private final static Color LINE = Color.rgb(60, 60, 60);
    private final static Color PLAYER1_LINE = Color.rgb(255, 40, 40);
    private final static Color PLAYER1_AREA = Color.rgb(255, 90, 90);
    private final static Color PLAYER2_LINE = Color.rgb(40, 40, 255);
    private final static Color PLAYER2_AREA = Color.rgb(120, 120, 255);
    private final static Color MIXED_LINE = Color.rgb(255, 40, 255);
    private final static Color VERTEX_COLOR = Color.WHITE;
    private final static Color BOT_COLOR = Color.rgb(72, 217, 67);
    private final static Color BOUNDARY_COLOR = Color.rgb(229, 210, 68);

    public void start(Board board, boolean isPlayer1) {
        gameName = board.getPlayer1().getName();
        logic = new Logic(board, isPlayer1);

        canvas = new Canvas();
        canvas.setHeight(800);
        canvas.setWidth(1200);

        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(2);

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 500, 500);

        System.out.println(gameName + " " + logic + " " + canvas + " " + gc);

        FirebaseDatabase.getInstance().getReference().child("games").child(gameName).child("board").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logic.setBoard(Board.loadData((String) dataSnapshot.getValue()));
                Platform.runLater(() -> drawCanvas());
                if (logic.getBoard().isWon()) {
                    end();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        drawCanvas();
    }

    public EventHandler<KeyEvent> getAction() {
        return e -> {
            switch (e.getCode()) {
                case T -> logic.changeBot();
                case DOWN -> this.moveBot(0, 1);
                case LEFT -> this.moveBot(-1, 0);
                case UP -> this.moveBot(0, -1);
                case RIGHT -> this.moveBot(1, 0);
                case Q -> this.moveBot(-1, -1);
                case E -> this.moveBot(1, -1);
                case A -> this.moveBot(-1, 1);
                case D -> this.moveBot(1, 1);
            }

            drawCanvas();
        };
    }

    private void moveBot(int x, int y) {
        logic.moveBot(x, y);
        FirebaseDatabase.getInstance().getReference().child("games").child(gameName).child("board").
                setValue(logic.getBoard().toData(), ((databaseError, databaseReference) -> {}));
    }

    void end() {
        won.setText("game ended!");
    }

    public Logic getLogic() {
        return this.logic;
    }

    private void drawCanvas() {
        ArrayList<Vertex[]> player1Edges = new ArrayList<>();
        ArrayList<Vertex[]> player2Edges = new ArrayList<>();
        ArrayList<Vertex[]> commonEdges = new ArrayList<>();

        for (Bot bot: logic.getBoard().getPlayer1().getBots()) {
            for (int i = 0; i < bot.getVisitedVertices().size()-1; i++) {
                player1Edges.add(new Vertex[]{bot.getVisitedVertices().get(i), bot.getVisitedVertices().get(i+1)});
            }
        }
        for (Bot bot: logic.getBoard().getPlayer2().getBots()) {
            for (int i = 0; i < bot.getVisitedVertices().size()-1; i++) {
                player2Edges.add(new Vertex[]{bot.getVisitedVertices().get(i), bot.getVisitedVertices().get(i+1)});
            }
        }

        for (int i = player1Edges.size()-1; i >= 0; i--) {
            Vertex[] vertices1 = player1Edges.get(i);
            for (int j = player2Edges.size()-1; j >= 0; j--) {
                Vertex[] vertices2 = player2Edges.get(j);
                if ((vertices1[0].equals(vertices2[0]) && vertices1[1].equals(vertices2[1])) ||
                        (vertices1[0].equals(vertices2[1]) && vertices1[1].equals(vertices2[0]))) {
                    commonEdges.add(vertices1);
                    player1Edges.remove(vertices1);
                    player2Edges.remove(vertices2);
                    break;
                }
            }
        }


        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawTiles(PLAYER1_AREA, logic.getBoard().getPlayer1());
        drawTiles(PLAYER2_AREA, logic.getBoard().getPlayer2());

        gc.setStroke(LINE);
        gc.beginPath();
        // drawing vertical lines
        for (int x = 0; x <= Board.BOARD_WIDTH; x++) {
            gc.moveTo(x * SPACING + PADDING, PADDING);
            gc.lineTo(x * SPACING + PADDING, Board.BOARD_HEIGHT * SPACING + PADDING);
        }
        // drawing horizontal lines
        for (int y = 0; y <= Board.BOARD_HEIGHT; y++) {
            gc.moveTo(PADDING, y * SPACING + PADDING);
            gc.lineTo(Board.BOARD_WIDTH * SPACING + PADDING, y * SPACING + PADDING);
        }
        // drawing positive gradient lines
        for (int i = 0; i < Board.BOARD_WIDTH + Board.BOARD_HEIGHT; i++) {
            int startX = Math.max(0, i - Board.BOARD_HEIGHT);
            int startY = Math.min(i, Board.BOARD_HEIGHT);
            int endX = Math.min(i, Board.BOARD_WIDTH);
            int endY = Math.max(0, i - Board.BOARD_WIDTH);
            gc.moveTo(startX * SPACING + PADDING, startY * SPACING + PADDING);
            gc.lineTo(endX * SPACING + PADDING, endY * SPACING + PADDING);
        }
        // drawing negative gradient lines
        for (int i = 0; i < Board.BOARD_WIDTH + Board.BOARD_HEIGHT; i++) {
            int startX = Math.max(0, i - Board.BOARD_HEIGHT);
            int startY = Math.max(0, Board.BOARD_HEIGHT - i);
            int endX = Math.min(i, Board.BOARD_WIDTH);
            int endY = Math.min(Board.BOARD_HEIGHT, Board.BOARD_HEIGHT + Board.BOARD_WIDTH - i);
            gc.moveTo(startX * SPACING + PADDING, startY * SPACING + PADDING);
            gc.lineTo(endX * SPACING + PADDING, endY * SPACING + PADDING);
        }
        gc.stroke();
        gc.closePath();

        drawEdges(player1Edges, PLAYER1_LINE);
        drawEdges(player2Edges, PLAYER2_LINE);
        drawEdges(commonEdges, MIXED_LINE);

        gc.setFill(VERTEX_COLOR);
        for (int col = 0; col <= Board.BOARD_WIDTH; col++) {
            for (int row = 0; row <= Board.BOARD_HEIGHT; row++) {
                gc.fillOval(col * SPACING - CIRCLE_RADIUS + PADDING, row * SPACING - CIRCLE_RADIUS + PADDING, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
            }
        }

        gc.setFill(BOT_COLOR);
        gc.fillOval(logic.getSelectedBot().getX() * SPACING - CIRCLE_RADIUS + PADDING, logic.getSelectedBot().getY() * SPACING - CIRCLE_RADIUS + PADDING, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);

        gc.setStroke(BOUNDARY_COLOR);
        gc.strokeOval((logic.getBoard().getCurrentPlayer().getSource().X() - Bot.MAX_DISTANCE) * SPACING + PADDING,
                (logic.getBoard().getCurrentPlayer().getSource().Y() - Bot.MAX_DISTANCE) * SPACING + PADDING,
                Bot.MAX_DISTANCE * SPACING * 2, Bot.MAX_DISTANCE * SPACING * 2);
    }

    private void drawEdges(ArrayList<Vertex[]> edges, Color lineColor) {
        gc.setStroke(lineColor);
        gc.beginPath();
        for (Vertex[] edge: edges) {
            gc.moveTo(edge[0].X() * SPACING + PADDING, edge[0].Y() * SPACING + PADDING);
            gc.lineTo(edge[1].X() * SPACING + PADDING, edge[1].Y() * SPACING + PADDING);
        }
        gc.stroke();
        gc.closePath();
    }

    private void drawTiles(Color tileColor, Player player) {
        gc.setFill(tileColor);
        for (int col = 0; col < Board.BOARD_WIDTH; col++) {
            for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
                for (int i = 0; i < 4; i++) {
                    if (logic.getBoard().getTiles().get(row).get(col)[i] == player.getId()) {
                        double[] xCoords;
                        double[] yCoords;

                        if (i == 0) {
                            xCoords = new double[]{(col + 1) * SPACING + PADDING, (col + 1) * SPACING + PADDING, (col + 0.5) * SPACING + PADDING};
                            yCoords = new double[]{row * SPACING + PADDING, (row + 1) * SPACING + PADDING, (row + 0.5) * SPACING + PADDING};
                        } else if (i == 1) {
                            xCoords = new double[]{col * SPACING + PADDING, (col + 1) * SPACING + PADDING, (col + 0.5) * SPACING + PADDING};
                            yCoords = new double[]{row * SPACING + PADDING, row * SPACING + PADDING, (row + 0.5) * SPACING + PADDING};
                        } else if (i == 2) {
                            xCoords = new double[]{col * SPACING + PADDING, col * SPACING + PADDING, (col + 0.5) * SPACING + PADDING};
                            yCoords = new double[]{row * SPACING + PADDING, (row + 1) * SPACING + PADDING, (row + 0.5) * SPACING + PADDING};
                        } else {
                            xCoords = new double[]{col * SPACING + PADDING, (col + 1) * SPACING + PADDING, (col + 0.5) * SPACING + PADDING};
                            yCoords = new double[]{(row + 1) * SPACING + PADDING, (row + 1) * SPACING + PADDING, (row + 0.5) * SPACING + PADDING};
                        }

                        gc.fillPolygon(xCoords, yCoords, 3);
                    }
                }
            }
        }
    }
}
