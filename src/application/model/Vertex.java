package application.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Vertex implements Serializable {
    private final int x;
    private final int y;
    private final ArrayList<Vertex> connectedVertices;

    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
        this.connectedVertices = new ArrayList<>();
    }

    public void addConnectedVertex(Vertex vertex) {
        this.connectedVertices.add(vertex);
    }

    public ArrayList<Vertex> getConnectedVertices() {
        return this.connectedVertices;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Vertex copy() {
        return new Vertex(this.x, this.y);
    }

    public boolean equals(Vertex vertex) {
        return this.x == vertex.x  &&  this.y == vertex.y;
    }

    public static double distance(Vertex v1, Vertex v2) {
        double dx = v1.getX() - v2.getX();
        double dy = v1.getY() - v2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
