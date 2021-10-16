package application.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Bot implements Serializable {
    private int x;
    private int y;
    private Vertex source;
    private final ArrayList<Vertex> visitedVertices;

    public final static int MAX_DISTANCE = 4;

    public Bot(int x, int y) {
        this.x = x;
        this.y = y;
        this.source = new Vertex(x, y);
        this.visitedVertices = new ArrayList<>();
        this.visitedVertices.add(new Vertex(this.x, this.y));
    }

    public Bot(Vertex vertex) {
        this(vertex.X(), vertex.Y());
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public ArrayList<Vertex> getVisitedVertices() {
        return this.visitedVertices;
    }

    public boolean move(int x, int y) {
        int dx = this.x + x - source.X();
        int dy = this.y + y - source.Y();
        if (dx * dx + dy * dy <= MAX_DISTANCE * MAX_DISTANCE) {
            this.x += x;
            this.y += y;
            this.visitedVertices.add(new Vertex(this.x, this.y));

            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        this.visitedVertices.clear();
        this.visitedVertices.add(new Vertex(this.x, this.y));
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex toVertex() {
        return new Vertex(this.x, this.y);
    }

    public boolean equals(Bot bot) {
        return this.x == bot.x  &&  this.y == bot.y;
    }
}
