package application.model;

import java.util.ArrayList;

public class Vertex {
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

    public boolean equals(Vertex vertex) {
        return this.x == vertex.x  &&  this.y == vertex.y;
    }
}
