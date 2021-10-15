package application.model;

public class Logic {
    private final Board board;

    private Player currentPlayer;
    private Bot selectedBot;

    public Logic() {
        this.board = new Board();
        this.currentPlayer = board.getPlayer1();
        this.selectedBot = this.currentPlayer.getBots().get(0);
    }

    public void selectBot1() {
        this.selectedBot = this.currentPlayer.getBots().get(0);
    }

    public void selectBot2() {
        this.selectedBot = this.currentPlayer.getBots().get(1);
    }

    public void moveBot(int x, int y) {
        int newX = this.selectedBot.getX() + x;
        int newY = this.selectedBot.getY() + y;
        if (0 <= newX && newX <= Board.BOARD_WIDTH  &&  0 <= newY && newY <= Board.BOARD_HEIGHT) {
            if (this.selectedBot.move(x, y)) {
                this.board.update();
                Move move = new Move(this.board);
                this.currentPlayer = this.board.changePlayer(this.currentPlayer);
                this.selectedBot = this.currentPlayer.getBots().get(0);
            }
        }
    }

    public Board getBoard() {
        return this.board;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Bot getSelectedBot() {
        return this.selectedBot;
    }
}
