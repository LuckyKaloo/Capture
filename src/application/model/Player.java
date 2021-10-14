package application.model;

import java.util.ArrayList;

public class Player {
    private final String name;
    private final ArrayList<Vertex> bots;
    private final ArrayList<Vertex> visitedVertices;
    private double area;

    public Player(String name) {
        this.name = name;
        this.bots = new ArrayList<>();
        this.visitedVertices = new ArrayList<>();
        this.visitedVertices.add(new Vertex(1, 1));
        this.area = 0;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Vertex> getBots() {
        return bots;
    }

    public ArrayList<Vertex> getVisitedVertices() {
        return visitedVertices;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public boolean formsClosedLoop() {
        if (visitedVertices.size() >= 2) {
            return visitedVertices.get(0).equals(visitedVertices.get(visitedVertices.size() - 1));
        } else {
            return false;
        }
    }
}
